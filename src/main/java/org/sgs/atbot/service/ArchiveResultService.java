package org.sgs.atbot.service;

import java.math.BigInteger;

import org.sgs.atbot.model.ArchiveResult;

public interface ArchiveResultService {

    ArchiveResult findById(BigInteger id);

    void save(ArchiveResult archiveResult);

    void delete(ArchiveResult archiveResult);

    ArchiveResult findByParentCommentId(String parentCommentId);

    boolean existsByParentCommentId(String parentCommentId);
}
