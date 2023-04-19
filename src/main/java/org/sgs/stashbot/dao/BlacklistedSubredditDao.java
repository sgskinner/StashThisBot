package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.BlacklistedSubreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface BlacklistedSubredditDao extends JpaRepository<BlacklistedSubreddit, BigInteger> {
//    private static final String NAME_PARAM = "name";
//    private static final String SELECT_BY_NAME = "SELECT b FROM BlacklistedSubreddit b where name = :" + NAME_PARAM;

    boolean isBlacklisted(String subredditName);

//    public BlacklistedSubreddit getBlacklistedSubredditByName(String name) {
//        List<BlacklistedSubreddit> blacklistedSubreddits = getEntityManager()
//                .createQuery(SELECT_BY_NAME)
//                .setParameter(NAME_PARAM, name)
//                .getResultList();
//
//        if (blacklistedSubreddits == null || blacklistedSubreddits.size() == 0) {
//            return null;
//        }
//
//        return blacklistedSubreddits.get(0);
//    }

}
