package org.sgs.stashbot.service;

import org.sgs.stashbot.model.Postable;
import org.sgs.stashbot.model.RedditComment;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.TimeUtils;
import org.springframework.stereotype.Service;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;

import java.util.ArrayList;
import java.util.List;


@Service
public class StashResultService {

    public StashResult buildStashResult(Submission submission, Comment summoningComment, Postable targetPostable, List<String> urlsToArchive) {
        StashResult stashResult = new StashResult();
        stashResult.setSubmissionUrl(submission.getUrl());
        stashResult.setTargetId(targetPostable.getId());
        stashResult.setTargetAuthor(targetPostable.getAuthor());
        stashResult.setRequestDate(TimeUtils.getTimeGmt());

        String targetUrl = buildRedditCommentUrl(submission, targetPostable.getId());
        stashResult.setTargetUrl(targetUrl);

        RedditComment redditComment = new RedditComment();
        redditComment.setRedditId(summoningComment.getId());
        redditComment.setAuthor(summoningComment.getAuthor());
        redditComment.setBody(summoningComment.getBody());
        redditComment.setUrl(buildRedditCommentUrl(submission, targetPostable.getId()));
        stashResult.setSummoningComment(redditComment);

        List<StashUrl> stashUrls = buildStashUrls(urlsToArchive);
        stashResult.setStashUrls(stashUrls);

        return stashResult;
    }


    private List<StashUrl> buildStashUrls(List<String> urlsToStash) {
        List<StashUrl> stashUrls = new ArrayList<>();
        for (String rawUrl : urlsToStash) {
            StashUrl stashUrl = new StashUrl();
            stashUrl.setOriginalUrl(rawUrl);
            stashUrls.add(stashUrl);
        }

        return stashUrls;
    }


    private String buildRedditCommentUrl(Submission submission, String postableId) {
        // |--------------------------------------------- 1 ---------------------------------------------------------||-- 2 --|
        // https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6qdqub/yatr_yet_another_test_run_here_we_are_again/dkwgsdw/
        // 1. submission.getUrl()
        // 2. commentNode.getComment().getId()
        return submission.getUrl() + postableId;
    }

}
