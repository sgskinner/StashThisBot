package org.sgs.atbot.service;

import org.sgs.atbot.dao.ArchiveResultBoDao;
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.ArchiveResultBo;

public interface PersistenceService extends AtbService {
    void persistArchiveResultBo(ArchiveResultBo archiveResultBo);

    void persistArchiveResult(ArchiveResult archiveResult);

    ArchiveResultBo findByParenCommentId(String parentCommentId);

    boolean isAlreadyServiced(String parentCommentId);

    boolean isUserBlacklisted(String author);

    void setArchiveResultDao(ArchiveResultBoDao archiveResultDao);

    void deleteArchiveResultBo(ArchiveResultBo archiveResultBo);

    boolean archiveResultExistsByParentCommentId(String parentCommentId);

    ArchiveResultBo findByParentCommentId(String parentCommentId);
}
