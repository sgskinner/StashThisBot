package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.RedditPollingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RedditTimeServiceDao extends JpaRepository<RedditPollingTime, Long> {
    RedditPollingTime findTopByOrderByDate();
}
