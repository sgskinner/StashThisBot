package org.sgs.stashbot.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class ArchiveIsServiceImpl extends ArchiveServiceBase {
    private static final Logger LOG = LogManager.getLogger(ArchiveIsServiceImpl.class);
    private static final String ARCHIVE_IS_SAVE_URL = "https://archive.today/?run=1&url=%s";
    private static final String HEADER_CONTENT_LOC_KEY = "Content-Location";


    @Override
    protected String getHeaderContentKey() {
        return HEADER_CONTENT_LOC_KEY;
    }


    @Override
    protected Logger getLog() {
        return LOG;
    }


    @Override
    protected String getSaveUrlFormat() {
        return ARCHIVE_IS_SAVE_URL;
    }


    @Override
    protected String getArchivedUrlFormat() {
        return null;
    }

}
