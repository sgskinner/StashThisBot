package org.sgs.stashbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.stashbot.dao.StashResultDao;
import org.sgs.stashbot.model.StashResult;
import org.springframework.stereotype.Repository;


@Repository
public class StashResultDaoImpl extends AbstractDao<BigInteger, StashResult> implements StashResultDao {
    private static final String TARGET_COMMENT_ID = "targetCommentId";
    private static final String SELECT_BY_TARGET_ID = String.format("select a from StashResult a where target_postable_id = :%s", TARGET_COMMENT_ID);


    @Override
    public StashResult findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(StashResult stashResult) {
        persist(stashResult);
    }


    @Override
    public void update(StashResult stashResult) {
        super.update(stashResult);
    }


    @Override
    public void delete(StashResult stashResult) {
        stashResult = getEntityManager().contains(stashResult) ? stashResult : getEntityManager().merge(stashResult);
        super.delete(stashResult);
    }


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public StashResult findByTargetCommentId(String targetCommentId) {
        List<StashResult> stashResultList =  getEntityManager()
                .createQuery(SELECT_BY_TARGET_ID)
                .setParameter(TARGET_COMMENT_ID, targetCommentId)
                .getResultList();

        if (stashResultList == null || stashResultList.size() < 1) {
            return null;
        }

        return stashResultList.get(0);
    }


    @Override
    public boolean existsByTargetCommentId(String targetCommentId) {
        StashResult stashResult = findByTargetCommentId(targetCommentId);
        return stashResult != null;
    }

}
