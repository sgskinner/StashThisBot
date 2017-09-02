package org.sgs.stashbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.stashbot.dao.BlacklistedSubredditDao;
import org.sgs.stashbot.model.BlacklistedSubreddit;
import org.springframework.stereotype.Repository;


@Repository
public class BlacklistedSubredditDaoImpl extends AbstractDao<BigInteger, BlacklistedSubreddit> implements BlacklistedSubredditDao {
    private static final String NAME_PARAM = "name";
    private static final String SELECT_BY_NAME = "SELECT b FROM BlacklistedSubreddit b where name = :" + NAME_PARAM;


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public BlacklistedSubreddit getBlacklistedSubredditByName(String name) {
        List<BlacklistedSubreddit> blacklistedSubreddits = getEntityManager()
                .createQuery(SELECT_BY_NAME)
                .setParameter(NAME_PARAM, name)
                .getResultList();

        if (blacklistedSubreddits == null || blacklistedSubreddits.size() == 0) {
            return null;
        }

        return blacklistedSubreddits.get(0);
    }


    @Override
    public boolean isSubredditBlacklisted(String name) {
        return getBlacklistedSubredditByName(name) != null;
    }


    @Override
    public BlacklistedSubreddit findById(BigInteger id) {
        return super.getByKey(id);
    }


    @Override
    public void save(BlacklistedSubreddit blacklistedSubreddit) {
        super.persist(blacklistedSubreddit);
    }


    @Override
    public void delete(BlacklistedSubreddit blacklistedSubreddit) {
        blacklistedSubreddit = getEntityManager().contains(blacklistedSubreddit) ? blacklistedSubreddit : getEntityManager().merge(blacklistedSubreddit);
        super.delete(blacklistedSubreddit);
    }


    @Override
    public void update(BlacklistedSubreddit blacklistedSubreddit) {
        super.update(blacklistedSubreddit);
    }

}
