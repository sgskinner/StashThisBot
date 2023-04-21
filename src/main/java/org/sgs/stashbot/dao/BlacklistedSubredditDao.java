package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.BlacklistedSubreddit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistedSubredditDao extends JpaRepository<BlacklistedSubreddit, Long> {
    boolean existsBlacklistedSubredditsByName(String subredditName);
}
