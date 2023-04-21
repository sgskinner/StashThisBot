package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.RedditPollingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface RedditTimeServiceDao extends JpaRepository<RedditPollingTime, BigInteger> {
    //private static final String SELECT_BY_MAX_ID = "SELECT t FROM RedditPollingTime t where id in ( select max(id) from RedditPollingTime)";

    RedditPollingTime findTopByOrderByDate();

//    public RedditPollingTime getLastRedditPollingTime() {
//        List<RedditPollingTime> pollingTimes = getEntityManager()
//                .createQuery(SELECT_BY_MAX_ID)
//                .getResultList();
//
//        if (pollingTimes == null || pollingTimes.size() == 0) {
//            return null;
//        }
//
//        return pollingTimes.get(0);
//    }

}
