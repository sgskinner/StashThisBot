package org.sgs.stashbot.service.impl;

import java.math.BigInteger;

import org.sgs.stashbot.dao.ScrapedUrlDao;
import org.sgs.stashbot.model.ScrapedUrl;
import org.sgs.stashbot.service.ScrapedUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScrapedUrlServiceImpl implements ScrapedUrlService {

    private final ScrapedUrlDao dao;


    @Autowired
    public ScrapedUrlServiceImpl(ScrapedUrlDao dao) {
        this.dao = dao;
    }


    @Override
    public ScrapedUrl getNextUrl() {
        return dao.getNextUrl();
    }


    @Override
    public ScrapedUrl findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(ScrapedUrl scrapedUrl) {
        dao.save(scrapedUrl);
    }


    @Override
    public void delete(ScrapedUrl scrapedUrl) {
        dao.delete(scrapedUrl);
    }


    @Override
    public void update(ScrapedUrl scrapedUrl) {
        dao.delete(scrapedUrl);
    }
}
