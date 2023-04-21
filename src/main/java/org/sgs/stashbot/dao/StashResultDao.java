package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.StashResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface StashResultDao extends JpaRepository<StashResult, Long> {
    boolean existsByTargetId(String postableId);
    StashResult findByTargetId(String postableId);
}
