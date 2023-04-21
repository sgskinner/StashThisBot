package org.sgs.stashbot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigInteger;
import java.util.Date;


@Entity
@Table(name = "reddit_polling_time_t")
public class RedditPollingTime {

    private BigInteger id;
    private Date date;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public BigInteger getId() {
        return id;
    }


    public void setId(BigInteger id) {
        this.id = id;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }

}
