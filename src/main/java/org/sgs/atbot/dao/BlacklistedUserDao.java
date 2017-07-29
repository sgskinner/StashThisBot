package org.sgs.atbot.dao;

import java.math.BigInteger;

import org.sgs.atbot.model.BlacklistedUser;

public interface BlacklistedUserDao {
    BlacklistedUser getBlackListedUserbyUsername(String username);

    boolean isUserBlacklisted(String username);

    BlacklistedUser findById(BigInteger id);

    void save(BlacklistedUser user);

    void delete(BlacklistedUser atbotUrl);

    void update(BlacklistedUser atbotUrl);
}
