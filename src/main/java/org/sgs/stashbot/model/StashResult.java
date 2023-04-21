package org.sgs.stashbot.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.List;


@Entity
@Table(name = "stash_result_t")
public class StashResult {
    private Long id;
    private String submissionUrl;
    private String targetAuthor;
    private String targetId;
    private String targetUrl;
    private Date requestDate;
    private Date processedDate;
    private RedditComment summoningComment;
    private List<StashUrl> stashUrls;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubmissionUrl() {
        return submissionUrl;
    }

    public void setSubmissionUrl(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    public String getTargetAuthor() {
        return targetAuthor;
    }

    public void setTargetAuthor(String targetAuthor) {
        this.targetAuthor = targetAuthor;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    @OneToOne(cascade = CascadeType.ALL)
    public RedditComment getSummoningComment() {
        return summoningComment;
    }

    public void setSummoningComment(RedditComment summoningComment) {
        this.summoningComment = summoningComment;
    }

    @OneToMany(targetEntity = StashUrl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    public List<StashUrl> getStashUrls() {
        return stashUrls;
    }

    public void setStashUrls(List<StashUrl> stashUrls) {
        this.stashUrls = stashUrls;
    }
}
