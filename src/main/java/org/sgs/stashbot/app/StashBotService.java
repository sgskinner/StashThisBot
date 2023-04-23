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
 * StashBotService - Summon this bot to archive URLs in an archive service.
 * Copyright (C) 2017  S.G. Skinner
 */
package org.sgs.stashbot.app;

import org.apache.commons.lang3.StringUtils;
import org.sgs.stashbot.dao.BlacklistedSubredditDao;
import org.sgs.stashbot.dao.BlacklistedUserDao;
import org.sgs.stashbot.dao.StashResultDao;
import org.sgs.stashbot.model.Postable;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.service.ArchiveService;
import org.sgs.stashbot.service.RedditService;
import org.sgs.stashbot.service.StashResultService;
import org.sgs.stashbot.util.UrlMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.Submission;

import java.util.List;


@Service
public class StashBotService {
    private static final Logger LOG = LoggerFactory.getLogger(StashBotService.class);
    private static final long SUBMISSION_POLLING_INTERVAL = 10 * 1000; // 10 seconds in millis
    private static final String SUMMONING_SUBJECT_TEXT = "username mention";

    private RedditService redditService;
    private StashResultService stashResultService;
    private BlacklistedSubredditDao blacklistedSubredditDao;
    private BlacklistedUserDao blacklistedUserDao;
    private ArchiveService archiveService;
    private StashResultDao stashResultDao;
    private Environment env;


    public void run() {

        while (true) {

            redditService.authenticate();

            LOG.info("--------------------------------------------------------------------------------");
            LOG.info("Polling for new messages...");
            Listing<Message> messages = redditService.getUnreadMessages();

            if (messages == null || messages.size() == 0) {
                LOG.info("No new messages.");
            } else {
                LOG.info("Found {} message(s).", messages.size());

                for (Message message : messages) {
                    LOG.info("----------------------------------------");

                    // Mark as read so we don't keep processing the same message
                    redditService.markMessageRead(message);

                    String subject = message.getSubject();
                    if (subject == null || !subject.equals(SUMMONING_SUBJECT_TEXT)) {
                        LOG.info("Skipping Message(id: {}) with subject '{}'.", message.getId(), subject);
                        continue;
                    }

                    Comment summoningComment = redditService.getSummoningComment(message);
                    Postable targetPostable = redditService.getTargetPostable(message);

                    if (summoningComment == null || targetPostable == null) {
                        LOG.warn("Could not pull comments for user mention: {}", message.data("context"));
                        continue;
                    }

                    if (targetPostable.getAuthor().equals(env.getProperty("bots.reddit.username"))) {
                        LOG.info("Skipping due to being called on bot's own comment.");
                        continue;
                    }

                    if (!isProcessed(targetPostable) && !isUserBlacklisted(summoningComment.getAuthor())) {
                        processMention(summoningComment, targetPostable);
                    }

                    LOG.info("Completed processing messages.");

                }//for

            }//else

            try {
                LOG.info("Sleeping...");
                Thread.sleep(SUBMISSION_POLLING_INTERVAL);
                LOG.info("Awake now.");
            } catch (InterruptedException e) {
                LOG.warn("Unexpectedly woken from sleep!: " + e.getMessage());
            }

        }
    }

    private boolean isUserBlacklisted(String authorUsername) {
        boolean isBlacklisted = StringUtils.isBlank(authorUsername) || blacklistedUserDao.existsByUsername(authorUsername);
        if (isBlacklisted) {
            LOG.info("User '{}' is blacklisted.", authorUsername);
        }

        return isBlacklisted;
    }


    private boolean isProcessed(Postable targetPostable) {
        String targetPostableId = targetPostable.getId();
        boolean isProcessed = stashResultDao.existsByTargetId(targetPostableId);
        LOG.info("Comment(id: {}) {} previously been processed.", targetPostableId, (isProcessed ? "HAS" : "has NOT"));

        return isProcessed;
    }


    private void processMention(Comment summoningComment, Postable targetPostable) {
        LOG.info("Processing summons: " + summoningComment.getId());

        String body = targetPostable.getBody();

        List<String> extractedUrls = UrlMatcher.extractUrls(body);

        if (extractedUrls.size() > 0) {
            LOG.info("Found {} URLs to archive.", extractedUrls.size());

            // May or may not be able to archive all urls, which we'll guard against in a sec
            String submissionId = summoningComment.getSubmissionId();
            Submission submission = redditService.getSubmissionById(submissionId);

            // Attempt actual archiving
            StashResult stashResult = stashResultService.buildStashResult(submission, summoningComment, targetPostable, extractedUrls);
            archiveService.archive(stashResult);

            // Regardless if the URLs were successful of being archived, still want to save record of having tried
            stashResultDao.save(stashResult);

            // Deliver results to summoner
            deliverStashResult(stashResult, summoningComment);

        } else {
            LOG.info("Didn't find any URLs to archive: {}", targetPostable.getUrl());
        }

    }


    private void deliverStashResult(StashResult stashResult, Comment summoningComment) {
        String subredditName = summoningComment.getSubredditName();
        if (blacklistedSubredditDao.existsBlacklistedSubredditsByName(subredditName)) {
            // This sub does not allow bots to post, so send a PM instead
            LOG.info("Making private message for StashResult(id: {})...", stashResult.getId());
            redditService.deliverStashResultByMessage(stashResult);
            LOG.info("Completed private message for StashResult(id: {}).", stashResult.getId());

        } else {
            // The sub is not blacklisted, so make a post
            LOG.info("Making reddit post for StashResult(id: {})...", stashResult.getId());
            redditService.postStashResult(summoningComment, stashResult);
            LOG.info("Completed reddit post for StashResult(id: {}).", stashResult.getId());
        }
    }


    public boolean isHealthy() {
        return redditService.isRedditHealthy() && archiveService.isHealthy();
    }


    @Autowired
    public void setRedditService(RedditService redditService) {
        this.redditService = redditService;
    }

    @Autowired
    public void setArchiveService(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

    @Autowired
    public void setBlacklistedUserDao(BlacklistedUserDao blacklistedUserDao) {
        this.blacklistedUserDao = blacklistedUserDao;
    }

    @Autowired
    public void setBlacklistedSubredditDao(BlacklistedSubredditDao blacklistedSubredditDao) {
        this.blacklistedSubredditDao = blacklistedSubredditDao;
    }

    @Autowired
    public void setStashResultDao(StashResultDao stashResultDao) {
        this.stashResultDao = stashResultDao;
    }

    @Autowired
    public void setStashResultService(StashResultService stashResultService) {
        this.stashResultService = stashResultService;
    }

}
