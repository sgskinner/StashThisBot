package org.sgs.atbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.dao.AbstractDao;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.model.ArchiveResult;
import org.springframework.stereotype.Repository;


@Repository
public class ArchiveResultDaoImpl extends AbstractDao<BigInteger, ArchiveResult> implements ArchiveResultDao {
    private static final String TARGET_COMMENT_ID = "targetCommentId";
    private static final String SELECT_BY_TARGET_ID = String.format("select a from ArchiveResult a where target_comment_id = :%s", TARGET_COMMENT_ID);


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
    public ArchiveResult findByTargetCommentId(String targetCommentId) {
        List<ArchiveResult> archiveResultList =  getEntityManager()
                .createQuery(SELECT_BY_TARGET_ID)
                .setParameter(TARGET_COMMENT_ID, targetCommentId)
                .getResultList();

        if (archiveResultList == null || archiveResultList.size() < 1) {
            return null;
        }

        return archiveResultList.get(0);
    }


    @Override
    public boolean existsByTargetCommentId(String targetCommentId) {
        ArchiveResult archiveResult = findByTargetCommentId(targetCommentId);
        return archiveResult != null;
    }

}
