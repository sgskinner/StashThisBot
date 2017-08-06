package org.sgs.stashbot.dao;

import java.math.BigInteger;

import org.sgs.stashbot.model.StashResult;

public interface StashResultDao {

    StashResult findById(BigInteger id);

    void save(StashResult stashResult);

    void delete(StashResult stashResult);

    StashResult findByTargetCommentId(String targetCommentId);

    boolean existsByTargetCommentId(String targetCommentId);

}
