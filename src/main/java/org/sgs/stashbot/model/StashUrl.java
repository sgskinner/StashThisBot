package org.sgs.stashbot.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "stash_url_t")
public class StashUrl implements Serializable {
    private static final long serialVersionUID = 5169553373729915231L;

    private BigInteger id;
    private StashResult stashResult;
    private String originalUrl;
    private String stashedUrl;
    private Date lastStashed;



    public StashUrl() {
        // Necessary for ORM
    }


    public StashUrl(String originalUrl) {
        this.originalUrl = originalUrl;
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


    @ManyToOne(targetEntity = StashResult.class)
    @JoinColumn(name = "stash_result_id", nullable = false)
    public StashResult getStashResult() {
        return stashResult;
    }


    public void setStashResult(StashResult stashResult) {
        this.stashResult = stashResult;
    }


    @Column(name = "stashed_url")
    public String getStashedUrl() {
        return stashedUrl;
    }


    public void setStashedUrl(String stashedUrl) {
        this.stashedUrl = stashedUrl;
    }


    @Column(name = "original_url")
    public String getOriginalUrl() {
        return originalUrl;
    }


    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }


    @Transient
    public boolean isStashed() {
        return getLastStashed() != null;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_stashed")
    public Date getLastStashed() {
        return lastStashed;
    }


    public void setLastStashed(Date date) {
        this.lastStashed = date;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StashUrl[");
        sb.append(String.format("originalUrl: '%s', ", getOriginalUrl()));
        sb.append(String.format("stashedUrl: '%s',", getStashedUrl()));
        sb.append(String.format("lastStashed: %s", getLastStashed()));
        sb.append(String.format("isStashed: %s]", isStashed()));

        return sb.toString();
    }
}
