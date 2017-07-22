package org.sgs.atbot.service;

import org.sgs.atbot.url.ArchiveResult;

import net.dean.jraw.models.CommentNode;

public interface PersistenceService extends AtbService {
    void persistArchiveResult(ArchiveResult archiveResult);
    boolean isAlreadyServiced(CommentNode commentNode);
    boolean isUserBlacklisted(String author);
}
