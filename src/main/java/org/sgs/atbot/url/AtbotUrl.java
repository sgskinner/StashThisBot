package org.sgs.atbot.url;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "atbot_url_t")
public class AtbotUrl {
    private Long urlId;
    private Long archivedResultId;
    private final String originalUrl;
    private String archivedUrl;
    private Date lastArchived;


    public AtbotUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="url_id")
    public Long getId() {
        return urlId;
    }


    public void setId(Long id) {
        this.urlId = id;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_id")
    public Long getArchivedResultId() {
        return archivedResultId;
    }


    public void setArchivedResultId(Long archivedResultId) {
        this.archivedResultId = archivedResultId;
    }


    @Column(name="archived_url")
    public String getArchivedUrl() {
        return archivedUrl;
    }


    public void setArchivedUrl(String archivedUrl) {
        this.archivedUrl = archivedUrl;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="original_url")
    public String getOriginalUrl() {
        return originalUrl;
    }


    public boolean isArchived() {
        return StringUtils.isNotBlank(getArchivedUrl()) && getLastArchived() != null;
    }


    @Column(name="last_archived")
    public Date getLastArchived() {
        return lastArchived;
    }


    public void setLastArchived(Date date) {
        this.lastArchived = date;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AtbotUrl[");
        sb.append(System.lineSeparator());
        sb.append("    originalUrl: " + getOriginalUrl());
        sb.append(System.lineSeparator());
        sb.append("    archivedUrl: " + getArchivedUrl());
        sb.append(System.lineSeparator());
        sb.append("    lastArchived: " + getLastArchived());
        sb.append(System.lineSeparator());
        sb.append("    isArchived: " + isArchived() + "]");

        return sb.toString();
    }
}
