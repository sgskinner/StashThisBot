package org.sgs.stashbot.model;

import org.sgs.stashbot.util.TimeUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "stash_result_t")
public class StashResult {

    @Id
    private BigInteger id;
    private String submissionUrl;
    private String summoningCommentAuthor;
    private String summoningCommentId;
    private String summoningCommentUrl;
    private String targetPostableAuthor;
    private String targetPostableId;
    private String targetPostableUrl;
    private Date requestDate;
    private Date servicedDate;

    @Transient
    private Comment summoningComment;

    @OneToMany(targetEntity = StashUrl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "stashResult")
    private List<StashUrl> stashUrls;


    public StashResult() {
        //
    }


    public StashResult(Submission submission, Comment summoningComment, Postable targetPostable, List<String> urlsToArchive) {
        this.submissionUrl = submission.getUrl();
        this.summoningCommentAuthor = summoningComment.getAuthor();
        this.summoningCommentId = summoningComment.getId();
        this.summoningCommentUrl = buildRedditCommentUrl(submission, targetPostable.getId());
        this.summoningComment = summoningComment;
        this.targetPostableAuthor = targetPostable.getAuthor();
        this.targetPostableId = targetPostable.getId();
        this.targetPostableUrl = buildRedditCommentUrl(submission, targetPostable.getId());
        this.requestDate = TimeUtils.getTimeGmt();
        addStashUrls(buildStashUrls(urlsToArchive));
    }


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getSubmissionUrl() {
        return submissionUrl;
    }

    public void setSubmissionUrl(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    public String getSummoningCommentAuthor() {
        return summoningCommentAuthor;
    }

    public void setSummoningCommentAuthor(String summoningCommentAuthor) {
        this.summoningCommentAuthor = summoningCommentAuthor;
    }

    public String getSummoningCommentId() {
        return summoningCommentId;
    }

    public void setSummoningCommentId(String summoningCommentId) {
        this.summoningCommentId = summoningCommentId;
    }

    public String getSummoningCommentUrl() {
        return summoningCommentUrl;
    }

    public void setSummoningCommentUrl(String summoningCommentUrl) {
        this.summoningCommentUrl = summoningCommentUrl;
    }

    public String getTargetPostableAuthor() {
        return targetPostableAuthor;
    }

    public void setTargetPostableAuthor(String targetCommentAuthor) {
        this.targetPostableAuthor = targetCommentAuthor;
    }

    public String getTargetPostableId() {
        return targetPostableId;
    }

    public void setTargetPostableId(String targetCommentId) {
        this.targetPostableId = targetCommentId;
    }

    public String getTargetPostableUrl() {
        return targetPostableUrl;
    }

    public void setTargetPostableUrl(String targetCommentUrl) {
        this.targetPostableUrl = targetCommentUrl;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getServicedDate() {
        return servicedDate;
    }

    public void setServicedDate(Date servicedDate) {
        this.servicedDate = servicedDate;
    }

    public List<StashUrl> getStashUrls() {
        return stashUrls;
    }

    public void setStashUrls(List<StashUrl> stashUrls) {
        this.stashUrls = stashUrls;
    }


    public Comment getSummoningComment() {
        return summoningComment;
    }

    private String buildRedditCommentUrl(Submission submission, String postableId) {
        // |--------------------------------------------- 1 ---------------------------------------------------------||-- 2 --|
        // https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6qdqub/yatr_yet_another_test_run_here_we_are_again/dkwgsdw/
        // 1. submission.getUrl()
        // 2. commentNode.getComment().getId()
        return submission.getUrl() + postableId;
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

    private void addStashUrls(List<StashUrl> stashUrls) {
        for (StashUrl stashUrl : stashUrls) {
            addStashUrl(stashUrl);
        }
    }

    private void addStashUrl(StashUrl stashUrl) {
        if (stashUrls == null) {
            stashUrls = new ArrayList<>();
        }
        stashUrl.setStashResult(this);
        stashUrls.add(stashUrl);
    }
}
