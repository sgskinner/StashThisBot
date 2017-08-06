package org.sgs.atbot.dao;

import java.math.BigInteger;

import org.sgs.atbot.model.ArchiveResult;

public interface ArchiveResultDao {

    ArchiveResult findById(BigInteger id);

    void save(ArchiveResult archiveResult);

    void delete(ArchiveResult archiveResult);

    ArchiveResult findByTargetCommentId(String targetCommentId);

    boolean existsByTargetCommentId(String targetCommentId);

}
