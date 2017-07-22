package org.sgs.atbot.url;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "atbot_url_t")
public class AtbotUrl {
    private BigInteger urlId;
    private BigInteger archivedResultId;
    private String originalUrl;
    private String archivedUrl;
    private Date lastArchived;


    public AtbotUrl() {
        // Necessary for ORM
    }


    public AtbotUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="url_id")
    public BigInteger getUrlId() {
        return urlId;
    }


    public void setUrlId(BigInteger id) {
        this.urlId = id;
    }


    @JoinColumn(name = "result_id")
    public BigInteger getArchivedResultId() {
        return archivedResultId;
    }


    public void setArchiveResultId (BigInteger archivedResultId) {
        this.archivedResultId = archivedResultId;
    }


    public void setArchivedResultId(BigInteger archivedResultId) {
        this.archivedResultId = archivedResultId;
    }


    @Column(name="archived_url")
    public String getArchivedUrl() {
        return archivedUrl;
    }


    public void setArchivedUrl(String archivedUrl) {
        this.archivedUrl = archivedUrl;
    }


    @Column(name="original_url")
    public String getOriginalUrl() {
        return originalUrl;
    }


    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }


    @Transient
    public boolean isArchived() {
        return StringUtils.isNotBlank(getArchivedUrl()) && getLastArchived() != null;
    }


    @Temporal(TemporalType.DATE)
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
