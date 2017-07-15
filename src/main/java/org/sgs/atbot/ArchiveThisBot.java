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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.atbot.service.ArchiveService;
import org.sgs.atbot.service.RedditService;
import org.sgs.atbot.spring.SpringContext;
import org.sgs.atbot.url.ArchivedUrl;
import org.sgs.atbot.url.UrlMatcher;
import org.springframework.util.StopWatch;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;

public class ArchiveThisBot {
    private static final Logger LOG = LogManager.getLogger(ArchiveThisBot.class);

    private RedditService redditService;
    private ArchiveService archiveIsService;
    private List<String> subredditList;


    public void run() {

        // OAuth token is set to expire in 1 hour from authenticating, so we need to watch for refreshing
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        performAuth();
        if (!isAuthenticated()) {
            LOG.fatal("Could not authenticate, exiting!");
            return;
        }

        do {
            for (String subredditName : getSubredditList()) {
                Listing<Submission> submissions = getRedditService().getSubredditSubmissions(subredditName);
                for (Submission submission : submissions) {
                    CommentNode commentNode = submission.getComments();
                    recurseThroughComments(commentNode);
                }
            }

            // OAuth token needs refreshing every 60 minutes, so we're going to refresh every 50
            if(stopWatch.getTotalTimeMillis() > (50*60*1000)) {
                performAuth();
                stopWatch.stop();
                stopWatch.start();
            }


        } while (isAuthenticated());
    }


    private void recurseThroughComments(CommentNode commentNode) {

        // Depth-first traversal, might want to also try breadth-first and assess which is better
        if (commentNode.getChildren().size() > 0) {
            for (CommentNode childNode : commentNode.getChildren()) {
                recurseThroughComments(childNode);
            }
        }

        // If we're here, we're a leaf node, so do summons search here
        if (isCommentSummoning(commentNode)) {
            processSummons(commentNode);
        }

    }


    private boolean isCommentSummoning(CommentNode commentNode) {
        Comment comment = commentNode.getComment();
        String body = comment.getBody();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(body)) {
            if (body.contains("!ArchiveThis") || body.contains("!Archive This") || body.contains("Archive This!") || body.contains("Archive This!")) {
                return true;
            }
        }

        return false;
    }


    private void processSummons(CommentNode commentNode) {
        // Pull all urls that we can find in the parent comment
        Comment parentComment = commentNode.getParent().getComment();
        String body = parentComment.getBody();
        List<String> extractedUrls = UrlMatcher.extractUrls(body);
        if (extractedUrls.size() > 0) {
            List<ArchivedUrl> archivedUrls = getArchiveService().archiveUrls(extractedUrls);
        }
    }


    protected RedditService getRedditService() {
        return redditService;
    }


    public void setRedditService(RedditService redditService) {
        this.redditService = redditService;
    }


    protected ArchiveService getArchiveService() {
        return archiveIsService;
    }


    public void setArchiveIsService(ArchiveService archiveIsService) {
        this.archiveIsService = archiveIsService;
    }


    public void performAuth() {
        getRedditService().performAuth();
    }


    public boolean isAuthenticated() {
        return getRedditService().isAuthenticated();
    }


    public List<String> getSubredditList() {
        return subredditList;
    }


    public void setSubredditList(List<String> subredditList) {
        this.subredditList = subredditList;
    }


    public static void main(String... sgs) {
        LOG.info("Intializing bot...");
        ArchiveThisBot atbot = SpringContext.getBean(ArchiveThisBot.class);

        LOG.info("Intializing complete, starting main loop.");
        atbot.run();
    }
}
