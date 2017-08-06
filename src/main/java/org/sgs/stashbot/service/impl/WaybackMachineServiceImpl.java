package org.sgs.stashbot.service.impl;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.service.ArchiveService;
import org.sgs.stashbot.util.TimeUtils;
import org.springframework.stereotype.Service;


@Service
public class WaybackMachineServiceImpl implements ArchiveService {
    private static final Logger LOG = LogManager.getLogger(WaybackMachineServiceImpl.class);
    private static final String WAYBACK_SAVE_URL = "https://web.archive.org/save/";
    private static final String WAYBACK_ROOT_URL = "https://web.archive.org";
    private static final String HEADER_CONTENT_LOC_KEY = "Content-Location";


    @Override
    public StashResult archive(StashResult stashResult) {
        for (StashUrl stashUrl : stashResult.getStashUrls()) {
            doArchive(stashUrl);
        }

        stashResult.setServicedDate(TimeUtils.getTimeGmt());

        return stashResult;
    }


    private void doArchive(StashUrl stashUrl) {
        LOG.info("Attempting to archive link: %s", stashUrl.getOriginalUrl());

        String urlString = stashUrl.getOriginalUrl();
        String saveRequestUrl = WAYBACK_SAVE_URL + urlString;

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpHead headMethod = new HttpHead(saveRequestUrl);
        String archivePath = null;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(headMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                Header[] headers = response.getHeaders(HEADER_CONTENT_LOC_KEY);
                if (headers != null && headers.length > 0) {
                    Header archivePathHeader = headers[0];
                    if (archivePathHeader != null) {
                        archivePath = archivePathHeader.getValue();
                    }
                }
            }

        } catch (IOException e) {
            LOG.warn("Error encountered when trying to archive link!: " + e.getMessage());
        } finally {
            closeHttpObjects(response, client);
        }

        if (StringUtils.isNoneBlank(archivePath)) {
            stashUrl.setStashedUrl(WAYBACK_ROOT_URL + archivePath);
            stashUrl.setLastStashed(TimeUtils.getTimeGmt());
            LOG.info("Archive link successful: " + archivePath);
        } else {
            // Set the attempted save url, which we can then later use as hyperlink in
            // failure message; we WON'T set lastArchived, which is how we detect if save
            // worked or not
            stashUrl.setStashedUrl(saveRequestUrl);
            LOG.warn("Couldn't obtain archive for URL: " + urlString);
        }

    }


    private void closeHttpObjects(CloseableHttpResponse response, CloseableHttpClient client) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                LOG.warn("Could not close HTTP Response!: " + e.getMessage());
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                LOG.warn("Could not close HTTP Client!: " + e.getMessage());
            }
        }
    }

}
