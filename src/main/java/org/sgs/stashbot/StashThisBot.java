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
 * StashThisBot - Summon this bot to archive URLs in an archive service.
 * Copyright (C) 2017  S.G. Skinner
 */

package org.sgs.stashbot;


import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.AuthPollingTime;
import org.sgs.stashbot.model.Postable;
import org.sgs.stashbot.service.BlacklistedSubredditService;
import org.sgs.stashbot.service.StashResultService;
import org.sgs.stashbot.service.ArchiveService;
import org.sgs.stashbot.service.AuthTimeService;
import org.sgs.stashbot.service.RedditService;
import org.sgs.stashbot.service.RedditTimeService;
import org.sgs.stashbot.service.UserService;
import org.sgs.stashbot.spring.SpringContext;
import org.sgs.stashbot.util.TimeUtils;
import org.sgs.stashbot.util.UrlMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.Submission;


@Component
public class StashThisBot {
    private static final Logger LOG = LogManager.getLogger(StashThisBot.class);
    private static final long AUTH_SLEEP_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final long SUBMISSION_POLLING_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final long OAUTH_REFRESH_INTERVAL = 50 * 60 * 1000; // 50 minutes in millis
    private static final int MAX_AUTH_ATTEMPTS = 3;
    private static final String SUMMONING_SUBJECT_TEXT = "username mention";

    @Resource(name = "botsRedditUsername")
    private String botsRedditUsername;
    private final RedditService redditService;
    private final ArchiveService archiveIsService;
    private final StashResultService stashResultService;
    private final UserService userService;
    private final RedditTimeService redditTimeService;
    private final AuthTimeService authTimeService;
    private final BlacklistedSubredditService blacklistedSubredditService;
    private boolean killSwitchClick;


    @Autowired
    public StashThisBot(RedditService redditService, ArchiveService archiveIsService, StashResultService stashResultService,
                        UserService userService, RedditTimeService redditTimeService, AuthTimeService authTimeService,
                        BlacklistedSubredditService blacklistedSubredditService) {
        this.redditService = redditService;
        this.archiveIsService = archiveIsService;
        this.stashResultService = stashResultService;
        this.userService = userService;
        this.redditTimeService = redditTimeService;
        this.authTimeService = authTimeService;
        this.blacklistedSubredditService = blacklistedSubredditService;
        this.killSwitchClick = false;
    }


    private void run() {

        if (!performAuth()) {
            LOG.fatal("Failed initial authentication, exiting!");
            System.exit(1);
        }

        while (!killSwitchClick) {

            LOG.info("--------------------------------------------------------------------------------");
            LOG.info("Polling for new messages...");
            Listing<Message> messages = getRedditService().getUnreadMessages();

            if (messages == null || messages.size() == 0) {
                LOG.info("No new messages.");
            } else {
                LOG.info("Found %d message(s).", messages.size());

                for (Message message : messages) {
                    LOG.info("----------------------------------------");

                    // Mark as read so we don't keep processing the same message
                    getRedditService().markMessageRead(message);

                    String subject = message.getSubject();
                    if (subject == null || !subject.equals(SUMMONING_SUBJECT_TEXT)) {
                        LOG.info("Skipping Message(id: %s) with subject '%s'.", message.getId(), subject);
                        continue;
                    }

                    Comment summoningComment = getRedditService().getSummoningComment(message);
                    Postable targetPostable = getRedditService().getTargetPostable(message);

                    if (summoningComment == null || targetPostable == null) {
                        LOG.warn("Could not pull comments for user mention: %s", message.data("context"));
                        continue;
                    }

                    if (targetPostable.getAuthor().equals(botsRedditUsername)) {
                        LOG.info("Skipping due to being called on bot's own comment.");
                        continue;
                    }

                    if (!isAlreadyServiced(targetPostable) && !isUserBlacklisted(summoningComment.getAuthor())) {
                        processSummons(summoningComment, targetPostable);
                    }

                    LOG.info("Completed processing messages.");

                }//for

            }//else

            // OAuth token needs refreshing every 60 minutes
            if (authNeedsRefreshing()) {
                retryAuthTillSuccess();
            }

            try {
                LOG.info("Sleeping...");
                Thread.sleep(SUBMISSION_POLLING_INTERVAL);
                LOG.info("Awake now.");
            } catch (InterruptedException e) {
                LOG.warn("Unexpectedly woken from sleep!: " + e.getMessage());
            }

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
        boolean isBlacklisted = StringUtils.isBlank(authorUsername) || getUserService().isUserBlacklisted(authorUsername);
        if (isBlacklisted) {
            LOG.info("User '%s' is blacklisted.");
        }

        return isBlacklisted;
    }


    private boolean isAlreadyServiced(Postable targetPostable) {
        String targetCommentId = targetPostable.getId();
        boolean isServiced = getStashResultService().existsByTargetCommentId(targetCommentId);
        LOG.info("Comment(id: %s) %s previously been serviced.", targetCommentId, (isServiced ? "HAS" : "has NOT"));

        return isServiced;
    }


    private void processSummons(Comment summoningComment, Postable targetPostable) {
        LOG.info("Processing summons: " + summoningComment.getId());

        String body = targetPostable.getBody();

        List<String> extractedUrls = UrlMatcher.extractUrls(body);

        if (extractedUrls.size() > 0) {
            LOG.info("Found %d URLs to archive.", extractedUrls.size());

            // May or may not be able to archive all urls, which we'll guard against in a sec
            String submissionId = summoningComment.getSubmissionId();
            Submission submission = getRedditService().getSubmissionById(submissionId);

            // Attempt actual archiving
            StashResult stashResult = new StashResult(submission, summoningComment, targetPostable, extractedUrls);
            getArchiveService().archive(stashResult);

            // Regardless if the URLs were successful of being archived, still want to save record of having tried
            getStashResultService().save(stashResult);

            // Deliver results to summoner
            deliverStashResult(stashResult, summoningComment);

        } else {
            LOG.info("Didn't find any URLs to archive: %s", targetPostable.getUrl());
        }

    }


    private void deliverStashResult(StashResult stashResult, Comment summoningComment) {
        String subredditName = summoningComment.getSubredditName();
        if (getBlacklistedSubredditService().isSubredditBlacklisted(subredditName)) {
            // This sub does not allow bots to post, so send a PM instead
            LOG.info("Making reddit post for StashResult(id: %d)...", stashResult.getId());
            getRedditService().deliverStashResultByMessage(stashResult);
            LOG.info("Completed reddit post for StashResult(id: %d)...", stashResult.getId());
        } else {
            // The sub is not blacklisted, so make a post
            LOG.info("Making private message for StashResult(id: %d)...", stashResult.getId());
            getRedditService().postStashResult(stashResult);
            LOG.info("Completed private message for StashResult(id: %d)...", stashResult.getId());
        }
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


    public StashResultService getStashResultService() {
        return stashResultService;
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


    private RedditService getRedditService() {
        return redditService;
    }


    private ArchiveService getArchiveService() {
        return archiveIsService;
    }


    public BlacklistedSubredditService getBlacklistedSubredditService() {
        return blacklistedSubredditService;
    }


    public static void main(String... sgs) {
        LOG.info("Intializing bot...");
        StashThisBot stashbot = SpringContext.getBean(StashThisBot.class);

        LOG.info("Intializing complete, starting main loop.");
        stashbot.run();
    }

}
