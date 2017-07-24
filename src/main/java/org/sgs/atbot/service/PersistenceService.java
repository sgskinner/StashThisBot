package org.sgs.atbot.service;

import java.util.List;

import org.sgs.atbot.ArchiveResultBo;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.url.ArchiveResult;

public interface PersistenceService extends AtbService {
    void persistArchiveResultBo(ArchiveResultBo archiveResultBo);

    void persistArchiveResult(ArchiveResult archiveResult);

    List<ArchiveResultBo> findByParenCommentId(String parentCommentId);

    boolean isAlreadyServiced(String parentCommentId);

    boolean isUserBlacklisted(String author);

    void setArchiveResultDao(ArchiveResultDao archiveResultDao);

    void deleteArchiveResultBo(ArchiveResultBo archiveResultBo);

    boolean archiveResultExistsByParentCommentId(String parentCommentId);

    List<ArchiveResultBo> findByParentCommentId(String parentCommentId);
}
