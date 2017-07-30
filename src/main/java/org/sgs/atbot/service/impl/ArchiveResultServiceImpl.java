package org.sgs.atbot.service.impl;

import java.math.BigInteger;

import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.service.ArchiveResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class ArchiveResultServiceImpl implements ArchiveResultService {

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
        dao.save(archiveResult);
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
