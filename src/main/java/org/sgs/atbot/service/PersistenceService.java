package org.sgs.atbot.service;

import org.sgs.atbot.ArchiveResultBo;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.url.ArchiveResult;

import net.dean.jraw.models.CommentNode;

public interface PersistenceService extends AtbService {
    void persistArchiveResultBo(ArchiveResultBo archiveResultBo);

    void persistArchiveResult(ArchiveResult archiveResult);

    boolean isAlreadyServiced(CommentNode summoningCommentNode);

    boolean isUserBlacklisted(String author);

    void setArchiveResultDao(ArchiveResultDao archiveResultDao);
}
