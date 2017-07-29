package org.sgs.atbot.service;

import java.math.BigInteger;

import org.sgs.atbot.model.RedditPollingTime;

public interface RedditTimeService {
    RedditPollingTime getLastPollingTime();

    RedditPollingTime findById(BigInteger id);

    void save(RedditPollingTime redditPollingTime);

    void delete(RedditPollingTime redditPollingTime);

    void update(RedditPollingTime redditPollingTime);
}
