package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.ScrapedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface ScrapedUrlDao extends JpaRepository<ScrapedUrl, BigInteger> {
    ScrapedUrl getNextScrapedUrl();
    //private static final String SELECT_BY_LOWEST_ID = "SELECT s FROM ScrapedUrl s WHERE s.id = (SELECT MIN(s2.id) FROM ScrapedUrl s2)";
}
