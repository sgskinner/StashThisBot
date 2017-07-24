package org.sgs.atbot.service.impl;

import java.util.List;

import org.sgs.atbot.ArchiveResultBo;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.service.PersistenceService;
import org.sgs.atbot.url.ArchiveResult;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


@EnableTransactionManagement
public class MySqlPersistenceServiceImpl implements PersistenceService {
    private ArchiveResultDao archiveResultDao;


    @Transactional
    @Override
    public void persistArchiveResult(ArchiveResult archiveResult) {
        ArchiveResultBo archiveResultBo = new ArchiveResultBo(archiveResult);
        persistArchiveResultBo(archiveResultBo);
    }


    @Transactional
    @Override
    public List<ArchiveResultBo> findByParenCommentId(String parentCommentId) {
        return getArchiveResultDao().findByParentCommentId(parentCommentId);
    }


    @Transactional
    @Override
    public void persistArchiveResultBo(ArchiveResultBo archiveResultBo) {
        getArchiveResultDao().save(archiveResultBo);
    }


    @Transactional
    @Override
    public boolean isAlreadyServiced(String parentCommentId) {
        return getArchiveResultDao().archiveResultExistsByParentCommentId(parentCommentId);
    }


    @Transactional
    @Override
    public void deleteArchiveResultBo(ArchiveResultBo archiveResultBo) {
        getArchiveResultDao().delete(archiveResultBo);
    }


    @Transactional
    @Override
    public boolean archiveResultExistsByParentCommentId(String parentCommentId) {
        return getArchiveResultDao().archiveResultExistsByParentCommentId(parentCommentId);
    }


    @Override
    public List<ArchiveResultBo> findByParentCommentId(String parentCommentId) {
        return getArchiveResultDao().findByParentCommentId(parentCommentId);
    }


    @Transactional
    @Override
    public boolean isUserBlacklisted(String authorUsername) {
        //TODO: implement
        throw new NotImplementedException();
    }


    private ArchiveResultDao getArchiveResultDao() {
        return archiveResultDao;
    }


    @Override
    public void setArchiveResultDao(ArchiveResultDao archiveResultDao) {
        this.archiveResultDao = archiveResultDao;
    }

}
