package org.sgs.atbot.service.impl;

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
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.AtbotUrl;
import org.sgs.atbot.service.ArchiveService;
import org.sgs.atbot.util.TimeUtils;
import org.springframework.stereotype.Service;


@Service
public class WaybackMachineArchiveServiceImpl implements ArchiveService {
    private static final Logger LOG = LogManager.getLogger(WaybackMachineArchiveServiceImpl.class);
    private static final String WAYBACK_SAVE_URL = "https://web.archive.org/save/";
    private static final String WAYBACK_ROOT_URL = "https://web.archive.org";
    private static final String HEADER_CONTENT_LOC_KEY = "Content-Location";


    @Override
    public ArchiveResult archive(ArchiveResult archiveResult) {
        for (AtbotUrl atbotUrl : archiveResult.getArchivedUrls()) {
            doArchive(atbotUrl);
        }

        archiveResult.setServicedDate(TimeUtils.getTimeGmt());

        return archiveResult;
    }


    private void doArchive(AtbotUrl atbotUrl) {
        LOG.info("About to archive link: ");
        LOG.info(atbotUrl);

        String urlString = atbotUrl.getOriginalUrl();
        String encodedUrl = WAYBACK_SAVE_URL + urlString;

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpHead headMethod = new HttpHead(encodedUrl);
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
            atbotUrl.setArchivedUrl(WAYBACK_ROOT_URL + archivePath);
            atbotUrl.setLastArchived(TimeUtils.getTimeGmt());
            LOG.info("Archive link successful: " + archivePath);
        } else {
            LOG.warn("Couldn't set archived URL: " + urlString);
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
