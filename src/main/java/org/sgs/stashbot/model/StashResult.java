package org.sgs.stashbot.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigInteger;
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
    private String targetCommentAuthor;
    private String targetCommentId;
    private String targetCommentUrl;
    private Date requestDate;
    private Date servicedDate;

    @OneToMany(targetEntity = StashUrl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "stashResult")
    private List<StashUrl> stashUrls;


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

    public String getTargetCommentAuthor() {
        return targetCommentAuthor;
    }

    public void setTargetCommentAuthor(String targetCommentAuthor) {
        this.targetCommentAuthor = targetCommentAuthor;
    }

    public String getTargetCommentId() {
        return targetCommentId;
    }

    public void setTargetCommentId(String targetCommentId) {
        this.targetCommentId = targetCommentId;
    }

    public String getTargetCommentUrl() {
        return targetCommentUrl;
    }

    public void setTargetCommentUrl(String targetCommentUrl) {
        this.targetCommentUrl = targetCommentUrl;
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
}
