package org.sgs.stashbot.service;

import java.math.BigInteger;

import org.sgs.stashbot.model.BlacklistedUser;

public interface UserService {
    BlacklistedUser getBlackListedUserbyUsername(String username);

    boolean isUserBlacklisted(String username);

    BlacklistedUser findById(BigInteger id);

    void save(BlacklistedUser user);

    void delete(BlacklistedUser user);

    void update(BlacklistedUser user);
}
