package org.sgs.stashbot.service;

import java.math.BigInteger;

import org.sgs.stashbot.model.ScrapedUrl;

public interface ScrapedUrlService {
    ScrapedUrl getNextUrl();

    ScrapedUrl findById(BigInteger id);

    void save(ScrapedUrl scrapedUrl);

    void delete(ScrapedUrl scrapedUrl);

    void update(ScrapedUrl scrapedUrl);
}
