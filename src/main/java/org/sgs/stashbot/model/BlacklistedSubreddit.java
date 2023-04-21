package org.sgs.stashbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;


@Entity
@Table(name = "blacklisted_subreddit_t")
public class BlacklistedSubreddit {

    private Long id;
    private String name;
    private Date dateCreated;
    private String note;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Date getDateCreated() {
        return dateCreated;
    }


    public void setDateCreated(Date dateAdded) {
        this.dateCreated = dateAdded;
    }


    public String getNote() {
        return note;
    }


    public void setNote(String note) {
        this.note = note;
    }

}
