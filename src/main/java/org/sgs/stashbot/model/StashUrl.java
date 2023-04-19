package org.sgs.stashbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigInteger;
import java.util.Date;


@Entity
@Table(name = "stash_url_t")
public class StashUrl {

    @Id
    private BigInteger id;
    private StashResult stashResult;
    private String originalUrl;
    private String stashedUrl;
    private Date lastStashed;


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


    public String getStashedUrl() {
        return stashedUrl;
    }


    public void setStashedUrl(String stashedUrl) {
        this.stashedUrl = stashedUrl;
    }


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


    public Date getLastStashed() {
        return lastStashed;
    }


    public void setLastStashed(Date date) {
        this.lastStashed = date;
    }


    @Override
    public String toString() {

        return "StashUrl[" +
                String.format("originalUrl: '%s', ", getOriginalUrl()) +
                String.format("stashedUrl: '%s',", getStashedUrl()) +
                String.format("lastStashed: %s", getLastStashed()) +
                String.format("isStashed: %s]", isStashed());
    }
}
