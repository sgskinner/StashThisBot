package org.sgs.atbot.service.impl;

import java.math.BigInteger;

import org.sgs.atbot.dao.RedditTimeServiceDao;
import org.sgs.atbot.model.RedditPollingTime;
import org.sgs.atbot.service.RedditTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class RedditTimeServiceImpl implements RedditTimeService {
    final RedditTimeServiceDao dao;


    @Autowired
    public RedditTimeServiceImpl(RedditTimeServiceDao dao) {
        this.dao = dao;
    }


    @Override
    public RedditPollingTime getLastPollingTime() {
        return dao.getLastRedditPollingTime();
    }


    @Override
    public RedditPollingTime findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(RedditPollingTime redditPollingTime) {
        dao.save(redditPollingTime);
    }


    @Override
    public void delete(RedditPollingTime redditPollingTime) {
        dao.delete(redditPollingTime);
    }


    @Override
    public void update(RedditPollingTime redditPollingTime) {
        dao.update(redditPollingTime);
    }

}
