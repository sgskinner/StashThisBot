package org.sgs.atbot;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sgs.atbot.url.ArchiveResult;
import org.sgs.atbot.url.AtbotUrl;

@Entity
@Table(name = "archive_result_t")
public class ArchiveResultBo {


    private Long resultId;
    private String submissionUrl;
    private String parentCommentAuthor;
    private String parentCommentId;
    private String parentCommentUrl;
    private String summoningCommentAuthor;
    private String summoningCommentId;
    private String summoningCommentUrl;
    private List<AtbotUrl> archivedUrls;

    public ArchiveResultBo(ArchiveResult archiveResult) {
        this.parentCommentAuthor = archiveResult.getParentCommentNode().getComment().getAuthor();
        this.parentCommentId = archiveResult.getParentCommentNode().getComment().getId();
        this.parentCommentUrl = archiveResult.getParentCommentNode().getComment().getUrl();
        this.summoningCommentAuthor = archiveResult.getSummoningCommentNode().getComment().getAuthor();
        this.summoningCommentId = archiveResult.getSummoningCommentNode().getComment().getId();
        this.summoningCommentUrl = archiveResult.getSummoningCommentNode().getComment().getUrl();
        this.archivedUrls = archiveResult.getUrlsToArchive();
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="result_id")
    public Long getResultId() {
        return resultId;
    }


    public void setResutlId(Long resultId) {
        this.resultId = resultId;
    }


    @Column(name="submission_url")
    public String getSubmissionUrl() {
        return submissionUrl;
    }


    public void setSubmissionUrl(String submissionUrl) {
        this.submissionUrl = submissionUrl;
    }

    @Column(name="parent_comment_author")
    public String getParentCommentAuthor() {
        return parentCommentAuthor;
    }


    public void setParentCommentAuthor(String parentCommentAuthor) {
        this.parentCommentAuthor = parentCommentAuthor;
    }

    @Column(name="parent_comment_id")
    public String getParentCommentId() {
        return parentCommentId;
    }


    public void setParentCommentId(String parentCommentId) {
        this.parentCommentId = parentCommentId;
    }


    @Column(name="parent_comment_url")
    public String getParentCommentUrl() {
        return parentCommentUrl;
    }


    public void setParentCommentUrl(String parentCommentUrl) {
        this.parentCommentUrl = parentCommentUrl;
    }


    @Column(name="summoning_comment_author")
    public String getSummoningCommentAuthor() {
        return summoningCommentAuthor;
    }


    public void setSummoningCommentAuthor(String summoningCommentAuthor) {
        this.summoningCommentAuthor = summoningCommentAuthor;
    }


    @Column(name="summoning_comment_id")
    public String getSummoningCommentId() {
        return summoningCommentId;
    }


    public void setSummoningCommentId(String summoningCommentId) {
        this.summoningCommentId = summoningCommentId;
    }


    @Column(name="summoning_comment_url")
    public String getSummoningCommentUrl() {
        return summoningCommentUrl;
    }


    public void setSummoningCommentUrl(String summoningCommentUrl) {
        this.summoningCommentUrl = summoningCommentUrl;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "archive_result_t")
    public List<AtbotUrl> getArchivedUrls() {
        return archivedUrls;
    }


    public void setArchivedUrls(List<AtbotUrl> archivedUrls) {
        this.archivedUrls = archivedUrls;
    }

}
