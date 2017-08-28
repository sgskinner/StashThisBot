package org.sgs.stashbot.service.impl;

import java.io.Closeable;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.service.ArchiveService;
import org.sgs.stashbot.util.TimeUtils;


public abstract class ArchiveServiceBase implements ArchiveService {
    private static final Logger LOG = LogManager.getLogger(ArchiveService.class);

    protected abstract void executeHttpTransaction(StashUrl stashUrl);


    @Override
    public StashResult archive(StashResult stashResult) {
        for (StashUrl stashUrl : stashResult.getStashUrls()) {
            executeHttpTransaction(stashUrl);
        }

        stashResult.setServicedDate(TimeUtils.getTimeGmt());

        return stashResult;
    }


    protected void closeHttpObjects(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOG.warn("Could not close response/client!: " + e.getMessage());
                }
            }
        }
    }

}
