package org.sgs.stashbot.dao;

import java.math.BigInteger;

import org.sgs.stashbot.model.ScrapedUrl;

public interface ScrapedUrlDao {
    ScrapedUrl getNextUrl();

    ScrapedUrl findById(BigInteger id);

    void save(ScrapedUrl scrapedUrl);

    void delete(ScrapedUrl scrapedUrl);

    void update(ScrapedUrl scrapedUrl);
}
