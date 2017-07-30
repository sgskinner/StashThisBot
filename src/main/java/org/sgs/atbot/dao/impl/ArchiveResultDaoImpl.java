package org.sgs.atbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.dao.AbstractDao;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.model.ArchiveResult;
import org.springframework.stereotype.Repository;


@Repository
public class ArchiveResultDaoImpl extends AbstractDao<BigInteger, ArchiveResult> implements ArchiveResultDao {
    private static final String PARENT_COMMENT_ID_KEY = "parentCommentId";
    private static final String SELECT_BY_PARENT_ID = String.format("select a from ArchiveResult a where parent_comment_id = :%s", PARENT_COMMENT_ID_KEY);


    @Override
    public ArchiveResult findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(ArchiveResult archiveResult) {
        persist(archiveResult);
    }


    @Override
    public void update(ArchiveResult archiveResult) {
        super.update(archiveResult);
    }


    @Override
    public void delete(ArchiveResult archiveResult) {
        archiveResult = getEntityManager().contains(archiveResult) ? archiveResult : getEntityManager().merge(archiveResult);
        super.delete(archiveResult);
    }


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public ArchiveResult findByParentCommentId(String parentCommentId) {
        List<ArchiveResult> archiveResultList =  getEntityManager()
                .createQuery(SELECT_BY_PARENT_ID)
                .setParameter(PARENT_COMMENT_ID_KEY, parentCommentId)
                .getResultList();

        if (archiveResultList == null || archiveResultList.size() < 1) {
            return null;
        }

        return archiveResultList.get(0);
    }


    @Override
    public boolean existsByParentCommentId(String parentCommentId) {
        ArchiveResult archiveResult = findByParentCommentId(parentCommentId);
        return archiveResult != null;
    }

}
