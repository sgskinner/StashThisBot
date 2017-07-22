package org.sgs.atbot.service.impl;

import org.sgs.atbot.ArchiveResultBo;
import org.sgs.atbot.service.PersistenceService;
import org.sgs.atbot.url.ArchiveResult;

import net.dean.jraw.models.CommentNode;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class MySqlPersistenceServiceImpl implements PersistenceService {


    @Override
    public void persistArchiveResult(ArchiveResult archiveResult) {

        ArchiveResultBo archiveResultBo = new ArchiveResultBo(archiveResult);

        //TODO: implement
        throw new NotImplementedException();
    }


    @Override
    public boolean isAlreadyServiced(CommentNode commentNode) {
        //TODO: implement
        throw new NotImplementedException();
    }


    @Override
    public boolean isUserBlacklisted(String author) {
        //TODO: implement
        throw new NotImplementedException();
    }

}
