package org.sgs.stashbot.dao;

import java.math.BigInteger;

import org.sgs.stashbot.model.BlacklistedUser;

public interface BlacklistedUserDao {
    BlacklistedUser getBlackListedUserbyUsername(String username);

    boolean isUserBlacklisted(String username);

    BlacklistedUser findById(BigInteger id);

    void save(BlacklistedUser user);

    void delete(BlacklistedUser user);

    void update(BlacklistedUser user);
}
