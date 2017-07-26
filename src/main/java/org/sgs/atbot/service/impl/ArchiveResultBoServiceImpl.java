package org.sgs.atbot.service.impl;

import java.math.BigInteger;

import org.sgs.atbot.dao.ArchiveResultBoDao;
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.ArchiveResultBo;
import org.sgs.atbot.service.ArchiveResultBoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("archiveResultBoService")
@Transactional
public class ArchiveResultBoServiceImpl implements ArchiveResultBoService {

    final ArchiveResultBoDao dao;


    @Autowired
    public ArchiveResultBoServiceImpl(ArchiveResultBoDao dao) {
        this.dao = dao;
    }


    @Override
    public ArchiveResultBo findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(ArchiveResult archiveResult) {
        this.save(new ArchiveResultBo(archiveResult));
    }


    @Override
    public void save(ArchiveResultBo archiveResultBo) {
        dao.save(archiveResultBo);
    }


    @Override
    public void delete(ArchiveResult archiveResult) {
        this.delete(new ArchiveResultBo(archiveResult));
    }


    @Override
    public void delete(ArchiveResultBo archiveResultBo) {
        dao.delete(archiveResultBo);
    }


    @Override
    public ArchiveResultBo findByParentCommentId(String parentCommentId) {
        return dao.findByParentCommentId(parentCommentId);
    }


    @Override
    public boolean existsByParentCommentId(String parentCommentId) {
        return this.findByParentCommentId(parentCommentId) != null;
    }
}
