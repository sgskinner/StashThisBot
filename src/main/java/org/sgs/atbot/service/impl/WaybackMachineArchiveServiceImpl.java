package org.sgs.atbot.service.impl;

import java.io.IOException;
import java.util.List;

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

import net.dean.jraw.models.CommentNode;


@Service
public class WaybackMachineArchiveServiceImpl implements ArchiveService {
    private static final Logger LOG = LogManager.getLogger(WaybackMachineArchiveServiceImpl.class);
    private static final String WAYBACK_SAVE_URL = "https://web.archive.org/save/";
    private static final String WAYBACK_ROOT_URL = "https://web.archive.org";
    private static final String HEADER_CONTENT_LOC_KEY = "Content-Location";


    @Override
    public ArchiveResult archiveUrls(CommentNode parentCommentNode, CommentNode summoningNode, List<String> extractedUrls) {
        ArchiveResult archiveResult = new ArchiveResult(parentCommentNode, summoningNode, extractedUrls);
        for (AtbotUrl atbotUrl : archiveResult.getUrlsToArchive()) {
            doArchive(atbotUrl);
        }

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

        if (archivePath != null && archivePath.length() > 0) {
            atbotUrl.setArchivedUrl(WAYBACK_ROOT_URL + archivePath);
            atbotUrl.setLastArchived(TimeUtils.getTimeGmt());
            LOG.info("Archive link successful: ");
            LOG.info(atbotUrl);
        } else {
            LOG.warn("Couldn't set archivedLink, header returned: " + archivePath);
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
