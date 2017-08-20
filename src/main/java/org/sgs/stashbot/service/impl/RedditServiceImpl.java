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

package org.sgs.stashbot.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.model.Postable;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.service.AuthService;
import org.sgs.stashbot.service.RedditService;
import org.sgs.stashbot.util.StashResultPostFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.AuthenticatedUserReference;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.fluent.InboxReference;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;
import net.dean.jraw.paginators.InboxPaginator;
import net.dean.jraw.paginators.Paginator;
import net.dean.jraw.paginators.SubredditPaginator;


@Service
public class RedditServiceImpl implements RedditService {
    private static final Logger LOG = LogManager.getLogger(RedditServiceImpl.class);

    private final AuthService authService;
    private final RedditClient redditClient;


    @Autowired
    public RedditServiceImpl(AuthService authService, RedditClient redditClient) {
        this.authService = authService;
        this.redditClient = redditClient;
    }


    @Override
    public Listing<Submission> getSubredditSubmissions(String subredditName) {
        SubredditPaginator paginator = new SubredditPaginator(getRedditClient());
        paginator.setSubreddit(subredditName);
        paginator.setLimit(Paginator.RECOMMENDED_MAX_LIMIT);
        return paginator.next();
    }


    /*
     * Necessary due to reddit api: the Paginator only returns the root submission, and
     * doesn't set any of the comment data. This requires an explicit call to the RedditClient
     * with the Submission's id, as detailed by the JRAW maintainers:
     * https://web.archive.org/web/20170716202732/https://github.com/thatJavaNerd/JRAW/issues/29
     */
    @Override
    public Submission getFullSubmissionData(Submission submission) {
        if (submission == null || submission.getCommentCount() < 1) {
            LOG.info("No comments to fetch for submission: " + (submission == null ? null : submission.getUrl()));
            return null;
        }

        return getRedditClient().getSubmission(submission.getId());
    }


    @Override
    public void performAuth() {
        getAuthService().authenticate(getRedditClient());
    }


    @Override
    public boolean isAuthenticated() {
        return getAuthService().isAuthenticated(getRedditClient());
    }


    @Override
    public void postStashResult(StashResult stashResult) {

        AccountManager accountManager = new AccountManager(redditClient);
        try {
            String postText = StashResultPostFormatter.format(stashResult);
            accountManager.reply(stashResult.getSummoningComment(), postText);
        } catch (ApiException e) {
            LOG.warn("Reddit API barfed on posting a reply to comment with ID: " + stashResult.getSummoningComment());
        }

    }


    @Override
    public Listing<Message> getUnreadMessages() {
        FluentRedditClient client = new FluentRedditClient(redditClient);
        AuthenticatedUserReference userRef = client.me();
        InboxReference inbox = userRef.inbox();

        InboxPaginator inboxPaginator;
        try {
            inboxPaginator = inbox.read();
        } catch (Exception e) {
            LOG.error(e);
            return new Listing<>(Message.class);
        }

        return inboxPaginator.next(true);
    }


    @Override
    public Comment getSummoningComment(Message message) {
        String id = message.data("name");
        if (id == null) {
            LOG.warn("Comment ID is null for passed in Message(id: %s)", message.getFullName());
            return null;
        }

        Listing<Thing> listing = getRedditClient().get(id);
        if (listing == null || listing.size() == 0) {
            LOG.warn("No Comment returned for passed in Message: %s", message.getFullName());
            return null;
        }

        return (Comment) listing.get(0);
    }


    @Override
    public Submission getSubmissionById(String submissionId) {
        Listing<Thing> listing = getRedditClient().get(submissionId);
        if (listing == null || listing.size() == 0) {
            return null;
        }

        return (Submission) listing.get(0);
    }


    @Override
    public void markMessageRead(Message message) {
        FluentRedditClient client = new FluentRedditClient(redditClient);
        AuthenticatedUserReference userRef = client.me();
        InboxReference inbox = userRef.inbox();
        inbox.readMessage(true, message);
    }


    @Override
    public Postable getTargetPostable(Message message) {
        String targetId = message.getParentId();
        if (targetId == null) {
            LOG.warn("Target ID is null for passed in Message: %s", message.getFullName());
            return null;
        }

        Listing<Thing> listing = getRedditClient().get(targetId);
        if (listing == null || listing.size() == 0) {
            LOG.warn("No comment returned for passed in Message: %s", message.getFullName());
            return null;
        }

        return new Postable(listing.get(0));
    }


    @Override
    public void deliverStashResultByMessage(StashResult stashResult) {
        String to = stashResult.getSummoningCommentAuthor();
        String subject = "StashThis Result";
        String body = StashResultPostFormatter.format(stashResult);

        FluentRedditClient client = new FluentRedditClient(redditClient);
        AuthenticatedUserReference userRef = client.me();
        InboxReference inbox = userRef.inbox();

        try {
            inbox.compose(to, subject, body);
        } catch (ApiException e) {
            LOG.error("Reddit API puked while PM'ing a summoner: %s", e.getMessage());
        }
    }


    private RedditClient getRedditClient() {
        return redditClient;
    }


    public AuthService getAuthService() {
        return authService;
    }

}
