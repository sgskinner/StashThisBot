/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * ArchiveThisBot - Summon this bot to archive Reddit URLs in archive.is
 * Copyright (C) 2016  S.G. Skinner
 */

package org.sgs.atbot;


import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.AtbotUrl;
import org.sgs.atbot.model.AuthPollingTime;
import org.sgs.atbot.service.ArchiveResultService;
import org.sgs.atbot.service.ArchiveService;
import org.sgs.atbot.service.AuthTimeService;
import org.sgs.atbot.service.RedditService;
import org.sgs.atbot.service.RedditTimeService;
import org.sgs.atbot.service.UserService;
import org.sgs.atbot.spring.SpringContext;
import org.sgs.atbot.util.SummonTokenMatcher;
import org.sgs.atbot.util.TimeUtils;
import org.sgs.atbot.util.UrlMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;


@Component
public class ArchiveThisBot {
    private static final Logger LOG = LogManager.getLogger(ArchiveThisBot.class);
    private static final long AUTH_SLEEP_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final long SUBMISSION_POLLING_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final long OAUTH_REFRESH_INTERVAL = 50 * 60 * 1000; // 50 minutes in millis
    private static final int MAX_AUTH_ATTEMPTS = 3;

    @Resource(name = "subredditList")
    private List<String> subredditList;
    private final RedditService redditService;
    private final ArchiveService archiveIsService;
    private final ArchiveResultService archiveResultService;
    private final UserService userService;
    private final RedditTimeService redditTimeService;
    private final AuthTimeService authTimeService;
    private final SummonTokenMatcher summonTokenMatcher;
    private boolean killSwitchClick;


    @Autowired
    public ArchiveThisBot(RedditService redditService, ArchiveService archiveIsService, ArchiveResultService archiveResultService, UserService userService, RedditTimeService redditTimeService, AuthTimeService authTimeService, SummonTokenMatcher summonTokenMatcher) {
        this.redditService = redditService;
        this.archiveIsService = archiveIsService;
        this.archiveResultService = archiveResultService;
        this.userService = userService;
        this.redditTimeService = redditTimeService;
        this.authTimeService = authTimeService;
        this.summonTokenMatcher = summonTokenMatcher;
        this.killSwitchClick = false;
    }


    private void run() {

        if (!performAuth()) {
            LOG.fatal("Failed initial authentication, exiting!");
            System.exit(1);
        }

        while (!killSwitchClick) {

            for (String subredditName : getSubredditList()) {

                LOG.info("Polling for submissions in subreddit: " + subredditName);
                Listing<Submission> submissions = getRedditService().getSubredditSubmissions(subredditName);
                LOG.info("Polling complete, found %d submissions.", submissions.size());

                for (Submission submission : submissions) {
                    if (submission.getCommentCount() < 1) {
                        LOG.info("Skipping submission(id: %s) with no comments.", submission.getId());
                        continue;
                    }

                    LOG.info("Hydrating full submission for: " + submission.getUrl());
                    submission = getRedditService().getFullSubmissionData(submission);

                    LOG.info("Starting to recurse through comments for Submission(id: %s)", submission.getId());
                    CommentNode commentNode = submission.getComments();
                    recurseThroughComments(commentNode, submission);
                }
            }

            // OAuth token needs refreshing every 60 minutes
            if (authNeedsRefreshing()) {
                retryAuthTillSuccess();
            }

            try {
                Thread.sleep(SUBMISSION_POLLING_INTERVAL);
            } catch (InterruptedException e) {
                LOG.warn("Unexpectedly woken from sleep!: " + e.getMessage());
            }

        }
    }


    private void recurseThroughComments(CommentNode commentNode, Submission submission) {
        if (commentNode == null) {
            LOG.warn("No comments found for submission!: " + submission.getShortURL());
            return;
        }

        // Depth-first traversal, might want to also try breadth-first and assess which is better
        if (commentNode.getChildren().size() > 0) {
            for (CommentNode childNode : commentNode.getChildren()) {
                recurseThroughComments(childNode, submission);
            }
        }

        // Base case: if we're here, we're a leaf node, so do summons search
        if (isCommentSummoning(commentNode) && !isAlreadyServiced(commentNode) && !isUserBlacklisted(commentNode.getComment().getAuthor())) {
            processSummons(commentNode, submission);
        }

    }


    private void retryAuthTillSuccess() {
        int attempts = 0;

        boolean success = performAuth();
        attempts++;

        while (!success) {
            if (attempts >= MAX_AUTH_ATTEMPTS) {
                LOG.fatal("Could not authenticate before exhausting %d attempts, exiting.", MAX_AUTH_ATTEMPTS);
                killSwitchClick = true;
                return;
            }

            try {
                Thread.sleep(AUTH_SLEEP_INTERVAL);
                success = performAuth();
                attempts++;
            } catch (InterruptedException e) {
                LOG.warn("Woken up from sleep unexpectedly!");
            }
        }
    }


    private boolean authNeedsRefreshing() {
        AuthPollingTime lastAuthTime = getAuthTimeService().getLastSuccessfulAuth();
        long now = TimeUtils.getTimeGmt().getTime();
        long lastAuth = lastAuthTime.getDate().getTime();

        return (now - lastAuth) >= OAUTH_REFRESH_INTERVAL;
    }


    private boolean isUserBlacklisted(String authorUsername) {
        return StringUtils.isBlank(authorUsername) || getUserService().isUserBlacklisted(authorUsername);
    }


    private boolean isAlreadyServiced(CommentNode summoningCommentNode) {
        String parentCommentId = summoningCommentNode.getParent().getComment().getId();

        if (parentCommentId == null) {
            // This is a root comment node, so skip
            return true;
        }

        boolean isServiced = getArchiveResultService().existsByParentCommentId(parentCommentId);
        LOG.info("Comment(id: %s) %s previously been serviced.", parentCommentId, (isServiced ? "HAS" : "has NOT"));

        return isServiced;
    }


    private boolean isCommentSummoning(CommentNode commentNode) {
        Comment comment = commentNode.getComment();
        String body = comment.getBody();

        if (StringUtils.isNotBlank(body)) {
            List<String> tokenHits = summonTokenMatcher.extractTokens(body);
            if (tokenHits != null && tokenHits.size() > 0) {
                LOG.info("Found summon hit in comment(id: %s) with summon tokens: %s", comment.getId(), tokenHits);
                return true;
            }
        }

        return false;
    }


    private void processSummons(CommentNode summoningCommentNode, Submission submission) {
        LOG.debug("Processing summons: " + summoningCommentNode.getComment().getId());

        CommentNode parentCommentNode = summoningCommentNode.getParent();
        Comment parentComment = parentCommentNode.getComment();
        String body = parentComment.getBody();

        List<String> extractedUrls = UrlMatcher.extractUrls(body);

        if (extractedUrls.size() > 0) {
            LOG.info("Found %d URLs to archive.", extractedUrls.size());

            // May or may not be able to archive all urls, which we'll guard against in a sec
            ArchiveResult archiveResult = new ArchiveResult(submission, parentCommentNode, summoningCommentNode, extractedUrls);
            getArchiveService().archive(archiveResult);

            // Regardless if the URLs were successful of being archived, still want to save record of having tried
            getArchiveResultService().save(archiveResult);

            // Only make a reddit post if we actually have some successfully archived URLs
            if (shouldPost(archiveResult)) {
                LOG.info("Making reddit post for ArchiveResult(id: %d)...", archiveResult.getId());
                getRedditService().postArchiveResult(archiveResult);
                LOG.info("Completed reddit post for ArchiveResult(id: %d)...", archiveResult.getId());
            }

        }

    }


    private boolean shouldPost(ArchiveResult archiveResult) {
        for (AtbotUrl atbotUrl : archiveResult.getArchivedUrls()) {
            if (atbotUrl.isArchived()) {
                // Post if we have even one good archived URL
                return true;
            }
        }

        return false;
    }


    private RedditService getRedditService() {
        return redditService;
    }


    private ArchiveService getArchiveService() {
        return archiveIsService;
    }


    protected boolean performAuth() {
        LOG.info("Attempting reddit authentication...");
        boolean success;
        AuthPollingTime time = new AuthPollingTime();
        time.setDate(TimeUtils.getTimeGmt());

        getRedditService().performAuth();

        if (isAuthenticated()) {
            success = true;
        } else {
            LOG.warn("Could not authenticate!");
            success = false;
        }

        time.setSuccess(success);
        getAuthTimeService().save(time);

        LOG.info("Authentication returned: " + success);
        return success;
    }


    protected boolean isAuthenticated() {
        return getRedditService().isAuthenticated();
    }


    public List<String> getSubredditList() {
        return subredditList;
    }


    public void setSubredditList(List<String> subredditList) {
        this.subredditList = subredditList;
    }


    public ArchiveResultService getArchiveResultService() {
        return archiveResultService;
    }


    public UserService getUserService() {
        return userService;
    }


    public RedditTimeService getRedditTimeService() {
        return redditTimeService;
    }


    public AuthTimeService getAuthTimeService() {
        return authTimeService;
    }


    public static void main(String... sgs) {
        LOG.info("Intializing bot...");
        ArchiveThisBot atbot = SpringContext.getBean(ArchiveThisBot.class);

        LOG.info("Intializing complete, starting main loop.");
        atbot.run();
    }

}
