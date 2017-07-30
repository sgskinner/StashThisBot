package org.sgs.atbot.service.impl;

import java.math.BigInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.service.ArchiveResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ArchiveResultServiceImpl implements ArchiveResultService {
    private static final Logger LOG = LogManager.getLogger(ArchiveResultServiceImpl.class);

    final ArchiveResultDao dao;


    @Autowired
    public ArchiveResultServiceImpl(ArchiveResultDao dao) {
        this.dao = dao;
    }


    @Override
    public ArchiveResult findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(ArchiveResult archiveResult) {
        LOG.info("Saving archive result...");
        dao.save(archiveResult);
        LOG.info("Successfully saved ArchiveResult(id: %d)", archiveResult.getId());
    }


    @Override
    public void delete(ArchiveResult archiveResult) {
        dao.delete(archiveResult);
    }


    @Override
    public ArchiveResult findByParentCommentId(String parentCommentId) {
        return dao.findByParentCommentId(parentCommentId);
    }


    @Override
    public boolean existsByParentCommentId(String parentCommentId) {
        return this.findByParentCommentId(parentCommentId) != null;
    }

}
