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

@Entity
@Table(name = "archive_result_t")
public class ArchiveResultBo implements Serializable {
    private static final long serialVersionUID = 4260302707444143426L;

    private BigInteger resultId;
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


    public ArchiveResultBo() {
        // Necessary for ORM
    }


    public ArchiveResultBo(ArchiveResult archiveResult) {
        this.parentCommentAuthor = archiveResult.getParentCommentNode().getComment().getAuthor();
        this.parentCommentId = archiveResult.getParentCommentNode().getComment().getId();
        this.parentCommentUrl = archiveResult.getParentCommentNode().getComment().getUrl();
        this.summoningCommentAuthor = archiveResult.getSummoningCommentNode().getComment().getAuthor();
        this.summoningCommentId = archiveResult.getSummoningCommentNode().getComment().getId();
        this.summoningCommentUrl = archiveResult.getSummoningCommentNode().getComment().getUrl();
        this.requestDate = archiveResult.getRequestDate();
        this.servicedDate = archiveResult.getServicedDate();
        addAtbotUrls(archiveResult.getUrlsToArchive());// need to register parent for one-to-many ORM
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    public BigInteger getResultId() {
        return resultId;
    }


    public void setResultId(BigInteger resultId) {
        this.resultId = resultId;
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
        atbotUrl.setArchiveResultBo(this);
        archivedUrls.add(atbotUrl);
    }


    @OneToMany(targetEntity = AtbotUrl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "archiveResultBo")
    public List<AtbotUrl> getArchivedUrls() {
        return archivedUrls;
    }


    public void setArchivedUrls(List<AtbotUrl> archivedUrls) {
        this.archivedUrls = archivedUrls;
    }

}
