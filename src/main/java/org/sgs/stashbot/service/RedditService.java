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

package org.sgs.stashbot.service;

import org.sgs.stashbot.model.Postable;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.util.PostFormatterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import net.dean.jraw.RedditClient;
import net.dean.jraw.fluent.AuthenticatedUserReference;
import net.dean.jraw.fluent.FluentRedditClient;
import net.dean.jraw.fluent.InboxReference;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Message;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;
import net.dean.jraw.paginators.InboxPaginator;


/**
 * A wrapper class around JRAW's RedditClient, with contained methods
 * for the very specific use cases of the StashBotService.
 * <p>
 * Note: There is a ton of exception handeling here, and the nature of
 * the app is that a StashResult is saved for every summons. Thus, if
 * we see a failure in here that is logged instead of throwing an
 * exception up, that same summons is recorded in the DB so we won't
 * perpetually reply to the same summons (for instance).
 */
@Service
public class RedditService {
    private static final Logger LOG = LoggerFactory.getLogger(RedditService.class);
    private PostFormatterService postFormatterService;
    private final RedditClient redditClient;
    private Environment env;


    @Autowired
    public RedditService(Environment env) {
        this.env = env;
        this.redditClient =  new RedditClient(buildUserAgent());
    }


    public boolean authenticate() {
        if (redditClient.isAuthenticated()) {
            return true;
        }

        try {
            OAuthData oAuthData = redditClient
                    .getOAuthHelper()
                    .easyAuth(getCredentials());
            redditClient.authenticate(oAuthData);
        } catch (Exception e) {
            LOG.error("Could not authenticate: {}", e.getMessage());
            return false;
        }

        return redditClient.isAuthenticated();
    }


    private Credentials getCredentials() {
        return new Credentials(AuthenticationMethod.SCRIPT,
                env.getProperty("reddit.bot.username"),
                env.getProperty("reddit.bot.password"),
                env.getProperty("reddit.bot.client.id"),
                env.getProperty("reddit.bot.client.secret"),
                null,
                env.getProperty("reddit.bot.redirect.url"));
    }


    private UserAgent buildUserAgent() {
        return UserAgent.of("desktop",
                "org.sgs.stashbot",
                env.getProperty("reddit.bot.version"),
                env.getProperty("bots.reddit.username"));
    }


    public void postStashResult(Comment summoningComment, StashResult stashResult) {
        AccountManager accountManager = new AccountManager(redditClient);
        try {
            String postText = postFormatterService.format(stashResult);
            accountManager.reply(summoningComment, postText);
        } catch (Exception e) {
            LOG.error("Could not post reply to summons (url: {}): {}",
                    summoningComment.getUrl(),
                    e.getMessage());
        }
    }


    public Listing<Message> getUnreadMessages() {

        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not instantiate fluent client to check mail: {}", e.getMessage());
            return new Listing<>(Message.class);
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref: {}", e.getMessage());
            return new Listing<>(Message.class);
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get reference to inbox: {}", e.getMessage());
            return new Listing<>(Message.class);
        }

        InboxPaginator inboxPaginator;
        try {
            inboxPaginator = inbox.read();
        } catch (Exception e) {
            LOG.error("Could not read inbox: {}", e.getMessage());
            return new Listing<>(Message.class);
        }

        // This is where we finally get the actual listing
        Listing<Message> listing;
        try {
            listing = inboxPaginator.next(true);
        } catch (Exception e) {
            LOG.error("Could not get inbox message listing: {}", e.getMessage());
            return new Listing<>(Message.class);
        }

        return listing;
    }


    public Comment getSummoningComment(Message message) {
        String id = message.data("name");
        if (id == null) {
            LOG.warn("Comment ID is null for passed in Message(id: {})", message.getFullName());
            return null;
        }

        Listing<Thing> listing;
        try {
            listing = redditClient.get(id);
        } catch (Exception e) {
            LOG.error("Could not get summoning comment with id({}): {}", id, e.getMessage());
            return null;
        }

        if (listing == null || listing.size() == 0) {
            LOG.error("No Comment returned for passed in Message: {}", message.getFullName());
            return null;
        }

        return (Comment) listing.get(0);
    }


    public Submission getSubmissionById(String submissionId) {
        Listing<Thing> listing;
        try {
            listing = redditClient.get(submissionId);
        } catch (Exception e) {
            LOG.error("Could not get subbmission by id({}): {}", submissionId, e.getMessage());
            return null;
        }

        if (listing == null || listing.size() == 0) {
            LOG.warn("Submission with id({}) came back null or empty.", submissionId);
            return null;
        }

        return (Submission) listing.get(0);
    }


    public void markMessageRead(Message message) {
        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not get fluent client to mark message with id({}) as read: {}",
                    message.getId(), e.getMessage());
            return;
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref while marking message with id({}) as read: {}",
                    message.getId(), e.getMessage());
            return;
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get inbox reference while marking message with id({}) as read: {}",
                    message.getId(), e.getMessage());
            return;
        }

        try {
            inbox.readMessage(true, message);
        } catch (Exception e) {
            LOG.error("Could not mark message with id({}) as read: {}", message.getId(), e.getMessage());
        }
    }


    public Postable getTargetPostable(Message message) {
        String targetId = message.getParentId();
        if (targetId == null) {
            LOG.warn("Target ID is null for passed in Message: {}", message.getFullName());
            return null;
        }

        Listing<Thing> listing = null;
        try {
            listing = redditClient.get(targetId);
        } catch (Exception e) {
            LOG.error("Could not get listing for target reddit Thing with id({}): {}", targetId, e.getMessage());
        }

        if (listing == null || listing.size() == 0) {
            LOG.warn("No comment returned for passed in Message: {}", message.getFullName());
            return null;
        }

        return new Postable(listing.get(0));
    }


    public void deliverStashResultByMessage(StashResult stashResult) {
        String to = stashResult.getSummoningComment().getAuthor();
        String subject = "StashThis Result";
        String body = postFormatterService.format(stashResult);

        FluentRedditClient client;
        try {
            client = new FluentRedditClient(redditClient);
        } catch (Exception e) {
            LOG.error("Could not instantiate fluent client to check mail: {}", e.getMessage());
            return;
        }

        AuthenticatedUserReference userRef;
        try {
            userRef = client.me();
        } catch (Exception e) {
            LOG.error("Could not get self user-ref: {}", e.getMessage());
            return;
        }

        InboxReference inbox;
        try {
            inbox = userRef.inbox();
        } catch (Exception e) {
            LOG.error("Could not get inbox ref when trying to send PM: {}", e.getMessage());
            return;
        }

        try {
            inbox.compose(to, subject, body);
        } catch (Exception e) {
            LOG.error("Could not send PM to summoner: {}", e.getMessage());
        }
    }


    public boolean isRedditHealthy() {
        try {
            FluentRedditClient client = new FluentRedditClient(redditClient);
            AuthenticatedUserReference userRef = client.me();
            InboxReference inbox = userRef.inbox();
            InboxPaginator inboxPaginator = inbox.read();
            inboxPaginator.next(true);
        } catch (Throwable t) {
            LOG.info("Health check failed: {}", t.getMessage());
            return false;
        }

        return true;
    }


    @Autowired
    public void setPostFormatterService(PostFormatterService postFormatterService) {
        this.postFormatterService = postFormatterService;
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }

}
