package org.sgs.stashbot.service;

import java.math.BigInteger;
import java.util.List;

import org.sgs.stashbot.model.StashUrl;

public interface StashUrlService {
    List<StashUrl> findAll();

    StashUrl findById(BigInteger id);

    void save(StashUrl stashUrl);

    void delete(StashUrl stashUrl);

    void update(StashUrl stashUrl);
}
