package org.sgs.stashbot.dao;

import java.math.BigInteger;

import org.sgs.stashbot.model.BlacklistedSubreddit;

public interface BlacklistedSubredditDao {
    BlacklistedSubreddit getBlacklistedSubredditByName(String username);

    boolean isSubredditBlacklisted(String name);

    BlacklistedSubreddit findById(BigInteger id);

    void save(BlacklistedSubreddit blacklistedSubreddit);

    void delete(BlacklistedSubreddit blacklistedSubreddit);

    void update(BlacklistedSubreddit blacklistedSubreddit);
}
