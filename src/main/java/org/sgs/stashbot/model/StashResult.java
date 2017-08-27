package org.sgs.stashbot.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.sgs.stashbot.util.TimeUtils;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;

@Entity
@Table(name = "stash_result_t")
public class StashResult implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

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
    private List<StashUrl> stashUrls;
    private Comment summoningComment;
    private Postable targetPostable;


    public StashResult() {
        // Necessary for ORM
    }


    public StashResult(Submission submission, Comment summoningComment, Postable targetPostable, List<String> urlsToArchive) {
        this.submissionUrl = submission.getUrl();
        this.summoningCommentAuthor = summoningComment.getAuthor();
        this.summoningCommentId = summoningComment.getId();
        this.summoningCommentUrl = buildRedditCommentUrl(submission, summoningComment);
        this.summoningComment = summoningComment;
        this.targetPostableAuthor = targetPostable.getAuthor();
        this.targetPostableId = targetPostable.getId();
        this.targetPostableUrl = buildRedditCommentUrl(submission, targetPostable.getThing());
        this.targetPostable = targetPostable;
        this.requestDate = TimeUtils.getTimeGmt();
        addStashUrls(buildStashUrls(urlsToArchive));
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    @Column(name = "submission_url")
    public String getSubmissionUrl() {
        return submissionUrl;
    }


    public void setSubmissionUrl(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }


    @Column(name = "target_postable_author")
    public String getTargetPostableAuthor() {
        return targetPostableAuthor;
    }


    public void setTargetPostableAuthor(String targetPostableAuthor) {
        this.targetPostableAuthor = targetPostableAuthor;
    }


    @Column(name = "target_postable_id")
    public String getTargetPostableId() {
        return targetPostableId;
    }


    public void setTargetPostableId(String targetPostableId) {
        this.targetPostableId = targetPostableId;
    }


    @Column(name = "target_postable_url")
    public String getTargetPostableUrl() {
        return targetPostableUrl;
    }


    public void setTargetPostableUrl(String targetPostableUrl) {
        this.targetPostableUrl = targetPostableUrl;
    }


    @Column(name = "summoning_comment_author")
    public String getSummoningCommentAuthor() {
        return summoningCommentAuthor;
    }


    public void setSummoningCommentAuthor(String summoningCommentAuthor) {
        this.summoningCommentAuthor = summoningCommentAuthor;
    }


    @Column(name = "summoning_comment_id")
    public String getSummoningCommentId() {
        return summoningCommentId;
    }


    public void setSummoningCommentId(String summoningCommentId) {
        this.summoningCommentId = summoningCommentId;
    }


    @Column(name = "summoning_comment_url")
    public String getSummoningCommentUrl() {
        return summoningCommentUrl;
    }


    public void setSummoningCommentUrl(String summoningCommentUrl) {
        this.summoningCommentUrl = summoningCommentUrl;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "request_date")
    public Date getRequestDate() {
        return requestDate;
    }


    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "serviced_date")
    public Date getServicedDate() {
        return servicedDate;
    }


    public void setServicedDate(Date servicedDate) {
        this.servicedDate = servicedDate;
    }


    public void addStashUrls(List<StashUrl> stashUrls) {
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


    @OneToMany(targetEntity = StashUrl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "stashResult")
    public List<StashUrl> getStashUrls() {
        return stashUrls;
    }


    public void setStashUrls(List<StashUrl> stashUrls) {
        this.stashUrls = stashUrls;
    }


    @Transient
    public Comment getSummoningComment() {
        return summoningComment;
    }


    public void setSummoningComment(Comment summoningComment) {
        this.summoningComment = summoningComment;
    }


    @Transient
    public Postable getTargetPostable() {
        return targetPostable;
    }


    public void setTargetPostable(Postable targetPostable) {
        this.targetPostable = targetPostable;
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


    private String buildRedditCommentUrl(Submission submission, Thing thing) {
        // |--------------------------------------------- 1 ---------------------------------------------------------||-- 2 --|
        // https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6qdqub/yatr_yet_another_test_run_here_we_are_again/dkwgsdw/
        // 1. submission.getUrl()
        // 2. commentNode.getComment().getId()
        return submission.getUrl() + thing.getId();
    }

}
