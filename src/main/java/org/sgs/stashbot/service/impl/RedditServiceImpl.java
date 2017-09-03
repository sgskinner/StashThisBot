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


/**
 * A wrapper class around JRAW's RedditClient, with contained methods
 * for the very specific use cases of the StashThisBot.
 *
 * Note: There is a ton of exception handeling here, and the nature of
 * the app is that a StashResult is saved for every summons. Thus, if
 * we see a failure in here that is logged instead of throwing an
 * exception up, that same summons is recorded in the DB so we won't
 * perpetually reply to the same summons (for instance).
 */
@Service
public class RedditServiceImpl implements RedditService {
    private static final Logger LOG = LogManager.getLogger(RedditServiceImpl.class);

    private final AuthService authService;
    private final RedditClient redditClient;
    private final StashResultPostFormatter stashResultPostFormatter;


    @Autowired
    public RedditServiceImpl(AuthService authService, RedditClient redditClient, StashResultPostFormatter stashResultPostFormatter) {
        this.authService = authService;
        this.redditClient = redditClient;
        this.stashResultPostFormatter = stashResultPostFormatter;
    }


    @Override
    public boolean performAuth() {
        return getAuthService().authenticate(getRedditClient());
    }


    @Override
    public boolean isAuthenticated() {
        return getAuthService().isAuthenticated(getRedditClient());
    }


    @Override
    public void postStashResult(StashResult stashResult) {
        AccountManager accountManager = new AccountManager(redditClient);
        try {
            String postText = stashResultPostFormatter.format(stashResult);
            accountManager.reply(stashResult.getSummoningComment(), postText);
        } catch (Exception e) {
            LOG.error("Could not post reply to summons (url: %d): %s",
                    stashResult.getSummoningComment().getUrl(),
                    e.getMessage());
        }
    }


    @Override
    public Listing<Message> getUnreadMessages() {

        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not instantiate fluent client to check mail: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get reference to inbox: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        InboxPaginator inboxPaginator;
        try {
            inboxPaginator = inbox.read();
        } catch (Exception e) {
            LOG.error("Could not read inbox: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        // This is where we finally get the actual listing
        Listing<Message> listing;
        try {
            listing = inboxPaginator.next(true);
        } catch (Exception e) {
            LOG.error("Could not get inbox message listing: %s", e.getMessage());
            return new Listing<>(Message.class);
        }

        return listing;
    }


    @Override
    public Comment getSummoningComment(Message message) {
        String id = message.data("name");
        if (id == null) {
            LOG.warn("Comment ID is null for passed in Message(id: %s)", message.getFullName());
            return null;
        }

        Listing<Thing> listing;
        try {
            listing = getRedditClient().get(id);
        } catch (Exception e) {
            LOG.error("Could not get summoning comment with id(%s): %s", id, e.getMessage());
            return null;
        }

        if (listing == null || listing.size() == 0) {
            LOG.error("No Comment returned for passed in Message: %s", message.getFullName());
            return null;
        }

        return (Comment) listing.get(0);
    }


    @Override
    public Submission getSubmissionById(String submissionId) {
        Listing<Thing> listing;
        try {
            listing = getRedditClient().get(submissionId);
        } catch (Exception e) {
            LOG.error("Could not get subbmission by id(%s): %s", submissionId, e.getMessage());
            return null;
        }

        if (listing == null || listing.size() == 0) {
            LOG.warn("Submission with id(%s) came back null or empty.", submissionId);
            return null;
        }

        return (Submission) listing.get(0);
    }


    @Override
    public void markMessageRead(Message message) {
        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not get fluent client to mark message with id(%s) as read: %s",
                    message.getId(), e.getMessage());
            return;
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref while marking message with id(%s) as read: %s",
                    message.getId(), e.getMessage());
            return;
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get inbox reference while marking message with id(%s) as read: %s",
                    message.getId(), e.getMessage());
            return;
        }

        try {
            inbox.readMessage(true, message);
        } catch (Exception e) {
            LOG.error("Could not mark message with id(%s) as read: %s", message.getId(), e.getMessage());
        }
    }


    @Override
    public Postable getTargetPostable(Message message) {
        String targetId = message.getParentId();
        if (targetId == null) {
            LOG.warn("Target ID is null for passed in Message: %s", message.getFullName());
            return null;
        }

        Listing<Thing> listing = null;
        try {
            listing = getRedditClient().get(targetId);
        } catch (Exception e) {
            LOG.error("Could not get listing for target reddit Thing with id(%s): %s", targetId, e.getMessage());
        }

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
        String body = stashResultPostFormatter.format(stashResult);

        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not instantiate fluent client to check mail: %s", e.getMessage());
            return;
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref: %s", e.getMessage());
            return;
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get inbox ref when trying to send PM: %s", e.getMessage());
            return;
        }

        try {
            inbox.compose(to, subject, body);
        } catch (Exception e) {
            LOG.error("Could not send PM to summoner: %s", e.getMessage());
        }
    }


    @Override
    public boolean isHealthy() {
        if (!getAuthService().authenticate(getRedditClient())) {
            return false;
        } else if (!isRedditHealthy()) {
            return false;
        }

        return true;
    }


    private boolean isRedditHealthy() {
        try {
            FluentRedditClient client = new FluentRedditClient(redditClient);
            AuthenticatedUserReference userRef = client.me();
            InboxReference inbox = userRef.inbox();
            InboxPaginator inboxPaginator = inbox.read();
            inboxPaginator.next(true);
        } catch (Throwable t) {
            LOG.info("Health check failed: %s", t.getMessage());
            return false;
        }

        return true;
    }


    private RedditClient getRedditClient() {
        return redditClient;
    }


    private AuthService getAuthService() {
        return authService;
    }

}
