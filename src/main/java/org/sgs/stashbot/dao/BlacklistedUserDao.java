package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.BlacklistedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BlacklistedUserDao extends JpaRepository<BlacklistedUser, Long> {
    boolean existsByUsername(String username);
    BlacklistedUser findBlacklistedUserByUsername(String username);
}
