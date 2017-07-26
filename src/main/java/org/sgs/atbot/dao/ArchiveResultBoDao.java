package org.sgs.atbot.dao;

import java.math.BigInteger;

import org.sgs.atbot.model.ArchiveResultBo;

public interface ArchiveResultBoDao {

    ArchiveResultBo findById(BigInteger id);

    void save(ArchiveResultBo archiveResultBo);

    void delete(ArchiveResultBo archiveResultBo);

    ArchiveResultBo findByParentCommentId(String parentCommentId);

    boolean existsByParentCommentId(String parentCommentId);

}
