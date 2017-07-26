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

package org.sgs.atbot.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.atbot.service.AuthService;
import org.sgs.atbot.service.RedditService;
import org.sgs.atbot.model.ArchiveResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.dean.jraw.ApiException;
import net.dean.jraw.RedditClient;
import net.dean.jraw.managers.AccountManager;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.TimePeriod;


@Component
public class RedditServiceImpl implements RedditService {
    private static final Logger LOG = LogManager.getLogger(RedditServiceImpl.class);

    private final AuthService authService;
    private final RedditClient redditClient;
    private final List subredditList;
    private boolean isFirstRun;


    @Autowired
    public RedditServiceImpl(AuthService authService, RedditClient redditClient, List subredditList) {
        this.authService = authService;
        this.redditClient = redditClient;
        this.subredditList = subredditList;
        this.isFirstRun = false;
    }


    @Override
    public Listing<Submission> getSubredditSubmissions(String subredditName) {
        SubredditPaginator paginator = new SubredditPaginator(getRedditClient());
        paginator.setSubreddit(subredditName);

        TimePeriod timePeriod = TimePeriod.HOUR;
        if (isFirstRun) {
            timePeriod = TimePeriod.WEEK;
            isFirstRun = false;
        }
        paginator.setTimePeriod(timePeriod);

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
            LOG.info("No comments to fetch for submission: " + (submission == null ? null : submission.getShortURL()));
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
    public void postArchiveResult(ArchiveResult archiveResult) {

        AccountManager accountManager = new AccountManager(redditClient);
        try {
            // TODO: need to aggregate *all* archived links, i.e., ArchiveResult->ArchivedUrl is a 1->n relationship
            accountManager.reply(archiveResult.getSummoningCommentNode().getComment(), archiveResult.getUrlsToArchive().get(0).getArchivedUrl());
        } catch (ApiException e) {
            LOG.warn("Reddit API barfed on posting a reply to comment with ID: " + archiveResult.getSummoningCommentNode().getComment());
            return;
        }

        saveArchiveResult(archiveResult);

    }


    private void saveArchiveResult(ArchiveResult archiveResult) {

    }


    private RedditClient getRedditClient() {
        return redditClient;
    }


    public AuthService getAuthService() {
        return authService;
    }


    public List getSubredditList() {
        return subredditList;
    }
}
