package org.sgs.stashbot.service.impl;

import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.dao.StashResultDao;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.service.StashResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class StashResultServiceImpl implements StashResultService {
    private static final Logger LOG = LogManager.getLogger(StashResultServiceImpl.class);

    final StashResultDao dao;


    @Autowired
    public StashResultServiceImpl(StashResultDao dao) {
        this.dao = dao;
    }


    @Override
    public StashResult findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(StashResult stashResult) {
        LOG.info("Saving archive result...");
        dao.save(stashResult);
        LOG.info("Successfully saved StashResult(id: %d)", stashResult.getId());
    }


    @Override
    public void delete(StashResult stashResult) {
        dao.delete(stashResult);
    }


    @Override
    public StashResult findByTargetCommentId(String targetCommentId) {
        return dao.findByTargetCommentId(targetCommentId);
    }


    @Override
    public boolean existsByTargetCommentId(String targetCommentId) {
        return this.findByTargetCommentId(targetCommentId) != null;
    }

}
