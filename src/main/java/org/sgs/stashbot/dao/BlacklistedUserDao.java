package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.BlacklistedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface BlacklistedUserDao extends JpaRepository<BlacklistedUser, BigInteger> {
    //private static final String SELECT_BY_USERNAME = "SELECT u FROM BlacklistedUser u where username = :username";
    boolean isUserBlacklisted(String subredditName);
    BlacklistedUser findBlacklistedUserByUsername(String username);
}
