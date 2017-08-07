package org.sgs.stashbot.service;

import java.math.BigInteger;

import org.sgs.stashbot.model.BlacklistedSubreddit;

public interface BlacklistedSubredditService {
    BlacklistedSubreddit getBlacklistedSubredditByName(String name);

    boolean isSubredditBlacklisted(String name);

    BlacklistedSubreddit findById(BigInteger id);

    void save(BlacklistedSubreddit blacklistedSubreddit);

    void delete(BlacklistedSubreddit blacklistedSubreddit);

    void update(BlacklistedSubreddit blacklistedSubreddit);
}
