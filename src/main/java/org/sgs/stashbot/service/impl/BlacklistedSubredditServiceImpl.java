package org.sgs.stashbot.service.impl;

import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.dao.BlacklistedSubredditDao;
import org.sgs.stashbot.model.BlacklistedSubreddit;
import org.sgs.stashbot.service.BlacklistedSubredditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BlacklistedSubredditServiceImpl implements BlacklistedSubredditService {
    private static final Logger LOG = LogManager.getLogger(BlacklistedSubredditServiceImpl.class);

    final BlacklistedSubredditDao dao;


    @Autowired
    public BlacklistedSubredditServiceImpl(BlacklistedSubredditDao dao) {
        this.dao = dao;
    }


    @Override
    public BlacklistedSubreddit getBlacklistedSubredditByName(String name) {
        return dao.getBlacklistedSubredditByName(name);
    }


    @Override
    public boolean isSubredditBlacklisted(String name) {
        boolean isBlacklisted = dao.isSubredditBlacklisted(name);
        if (isBlacklisted) {
            LOG.info("Subreddit '%s' is blacklisted.", name);
        }

        return isBlacklisted;
    }


    @Override
    public BlacklistedSubreddit findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(BlacklistedSubreddit blacklistedSubreddit) {
        dao.save(blacklistedSubreddit);
    }


    @Override
    public void delete(BlacklistedSubreddit blacklistedSubreddit) {
        dao.delete(blacklistedSubreddit);
    }


    @Override
    public void update(BlacklistedSubreddit blacklistedSubreddit) {
        dao.update(blacklistedSubreddit);
    }

}
