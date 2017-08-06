package org.sgs.stashbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.stashbot.dao.AbstractDao;
import org.sgs.stashbot.dao.StashUrlDao;
import org.sgs.stashbot.model.StashUrl;
import org.springframework.stereotype.Repository;


@Repository
public class StashUrlDaoImpl extends AbstractDao<BigInteger, StashUrl> implements StashUrlDao {
    private static final String SELECT_ALL_STASH_URL_QUERY = "SELECT s FROM StashUrl s";


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public List<StashUrl> findAll() {
        return getEntityManager()
                .createQuery(SELECT_ALL_STASH_URL_QUERY)
                .getResultList();
    }


    @Override
    public StashUrl findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(StashUrl stashUrl) {
        persist(stashUrl);
    }


    @Override
    public void delete(StashUrl stashUrl) {
        super.delete(stashUrl);
    }


    @Override
    public void update(StashUrl stashUrl) {
        super.update(stashUrl);
    }

}
