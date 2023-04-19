package org.sgs.stashbot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.math.BigInteger;
import java.util.Date;


@Entity
public class BlacklistedUser {

    private BigInteger id;
    private String username;
    private Date dateCreated;
    private String reason;


    public BlacklistedUser() {
        // Needed for ORM
    }


    @Id
    @Column
    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    @Column
    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column
    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }


    @Column
    public String getReason() {
        return reason;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

}
