package org.sgs.atbot.service;

import java.math.BigInteger;

import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.ArchiveResultBo;

public interface ArchiveResultBoService {

    ArchiveResultBo findById(BigInteger id);

    void save(ArchiveResult archiveResult);

    void save(ArchiveResultBo archiveResultBo);

    void delete(ArchiveResult archiveResult);

    void delete(ArchiveResultBo archiveResultBo);

    ArchiveResultBo findByParentCommentId(String parentCommentId);

    boolean existsByParentCommentId(String parentCommentId);
}
