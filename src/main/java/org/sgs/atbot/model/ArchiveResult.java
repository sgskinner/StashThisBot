package org.sgs.atbot.model;

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
import javax.persistence.Transient;

import org.sgs.atbot.util.TimeUtils;

import net.dean.jraw.models.CommentNode;
import net.dean.jraw.models.Submission;

@Entity
@Table(name = "archive_result_t")
public class ArchiveResult implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

    private BigInteger id;
    private String submissionUrl;
    private String parentCommentAuthor;
    private String parentCommentId;
    private String parentCommentUrl;
    private String summoningCommentAuthor;
    private String summoningCommentId;
    private String summoningCommentUrl;
    private Date requestDate;
    private Date servicedDate;
    private List<AtbotUrl> archivedUrls;
    private CommentNode summoningCommentNode;
    private CommentNode parentCommentNode;


    public ArchiveResult() {
        // Necessary for ORM
    }


    public ArchiveResult(Submission submission, CommentNode parentCommentNode, CommentNode summoningCommentNode, List<String> urlsToArchive) {
        this.submissionUrl = submission.getUrl();
        this.parentCommentAuthor = parentCommentNode.getComment().getAuthor();
        this.parentCommentId = parentCommentNode.getComment().getId();
        this.parentCommentUrl = buildRedditCommentUrl(submission, parentCommentNode);
        this.parentCommentNode = parentCommentNode;
        this.summoningCommentAuthor = summoningCommentNode.getComment().getAuthor();
        this.summoningCommentId = summoningCommentNode.getComment().getId();
        this.summoningCommentUrl = buildRedditCommentUrl(submission, summoningCommentNode);
        this.summoningCommentNode = summoningCommentNode;
        this.requestDate = TimeUtils.getTimeGmt();
        addAtbotUrls(buildAtbotUrls(urlsToArchive));
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


    @Column(name = "parent_comment_author")
    public String getParentCommentAuthor() {
        return parentCommentAuthor;
    }


    public void setParentCommentAuthor(String parentCommentAuthor) {
        this.parentCommentAuthor = parentCommentAuthor;
    }


    @Column(name = "parent_comment_id")
    public String getParentCommentId() {
        return parentCommentId;
    }


    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }


    @Column(name = "parent_comment_url")
    public String getParentCommentUrl() {
        return parentCommentUrl;
    }


    public void setParentCommentUrl(String parentCommentUrl) {
        this.parentCommentUrl = parentCommentUrl;
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


    @Column(name = "request_date")
    public Date getRequestDate() {
        return requestDate;
    }


    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }


    @Column(name = "serviced_date")
    public Date getServicedDate() {
        return servicedDate;
    }


    public void setServicedDate(Date servicedDate) {
        this.servicedDate = servicedDate;
    }


    public void addAtbotUrls(List<AtbotUrl> atbotUrls) {
        for (AtbotUrl atbotUrl : atbotUrls) {
            addAtbotUrl(atbotUrl);
        }
    }


    public void addAtbotUrl(AtbotUrl atbotUrl) {
        if (archivedUrls == null) {
            archivedUrls = new ArrayList<>();
        }
        atbotUrl.setArchiveResult(this);
        archivedUrls.add(atbotUrl);
    }


    @OneToMany(targetEntity = AtbotUrl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "archiveResult")
    public List<AtbotUrl> getArchivedUrls() {
        return archivedUrls;
    }


    public void setArchivedUrls(List<AtbotUrl> archivedUrls) {
        this.archivedUrls = archivedUrls;
    }


    @Transient
    public CommentNode getSummoningCommentNode() {
        return summoningCommentNode;
    }


    public void setSummoningCommentNode(CommentNode summoningCommentNode) {
        this.summoningCommentNode = summoningCommentNode;
    }


    @Transient
    public CommentNode getParentCommentNode() {
        return parentCommentNode;
    }


    public void setParentCommentNode(CommentNode parentCommentNode) {
        this.parentCommentNode = parentCommentNode;
    }


    private List<AtbotUrl> buildAtbotUrls(List<String> urlsToArchive) {
        List<AtbotUrl> atbotUrls = new ArrayList<>();
        for (String rawUrl : urlsToArchive) {
            AtbotUrl atbotUrl = new AtbotUrl();
            atbotUrl.setOriginalUrl(rawUrl);
            atbotUrls.add(atbotUrl);
        }

        return atbotUrls;
    }


    private String buildRedditCommentUrl(Submission submission, CommentNode commentNode) {
        // |--------------------------------------------- 1 ---------------------------------------------------------||-- 2 --|
        // https://www.reddit.com/r/ArchiveThisBotSandbox/comments/6qdqub/yatr_yet_another_test_run_here_we_are_again/dkwgsdw/
        // 1. submission.getUrl()
        // 2. commentNode.getComment().getId()
        return submission.getUrl() + commentNode.getComment().getId();
    }

}
