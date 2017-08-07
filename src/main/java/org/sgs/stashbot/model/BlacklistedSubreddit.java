package org.sgs.stashbot.model;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "blacklisted_subreddit_t")
public class BlacklistedSubreddit {


    private BigInteger id;
    private String name;
    private Date dateCreated;
    private String note;


    public BlacklistedSubreddit() {
        // Needed by ORM
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


    @Column(name = "name")
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateAdded) {
        this.dateCreated = dateAdded;
    }


    @Column(name = "note")
    public String getNote() {
        return note;
    }


    public void setNote(String note) {
        this.note = note;
    }

}
