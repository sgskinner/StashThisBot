package org.sgs.stashbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.sgs.stashbot.model.StashUrl;

public interface StashUrlDao {
    List<StashUrl> findAll();

    StashUrl findById(BigInteger id);

    void save(StashUrl stashUrl);

    void delete(StashUrl stashUrl);

    void update(StashUrl stashUrl);
}
