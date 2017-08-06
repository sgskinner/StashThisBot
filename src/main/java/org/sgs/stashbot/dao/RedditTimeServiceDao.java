package org.sgs.stashbot.dao;

import java.math.BigInteger;

import org.sgs.stashbot.model.RedditPollingTime;

public interface RedditTimeServiceDao {
    RedditPollingTime getLastRedditPollingTime();

    RedditPollingTime findById(BigInteger id);

    void save(RedditPollingTime redditPollingTime);

    void delete(RedditPollingTime redditPollingTime);

    void update(RedditPollingTime redditPollingTime);
}
