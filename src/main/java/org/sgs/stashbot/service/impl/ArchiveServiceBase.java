package org.sgs.stashbot.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.service.ArchiveService;
import org.sgs.stashbot.util.TimeUtils;


public abstract class ArchiveServiceBase implements ArchiveService {

    protected abstract String getHeaderContentKey();

    protected abstract Logger getLog();

    protected abstract String getSaveUrlFormat();

    protected abstract String getArchivedUrlFormat();


    @Override
    public StashResult archive(StashResult stashResult) {
        for (StashUrl stashUrl : stashResult.getStashUrls()) {
            executeHttpTransaction(stashUrl);
        }

        stashResult.setServicedDate(TimeUtils.getTimeGmt());

        return stashResult;
    }


    private void executeHttpTransaction(StashUrl stashUrl) {

        String urlString = stashUrl.getOriginalUrl();
        try {
            URLEncoder.encode(urlString, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            getLog().error("Could not encode URL: %s", e.getMessage());
            throw new RuntimeException(e);
        }
        String saveRequestUrl = String.format(getSaveUrlFormat(), urlString);

        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpHead headMethod = new HttpHead(saveRequestUrl);
        String archivePath = null;
        CloseableHttpResponse response = null;
        try {
            response = client.execute(headMethod);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                Header[] headers = response.getHeaders(getHeaderContentKey());
                if (headers != null && headers.length > 0) {
                    Header archivePathHeader = headers[0];
                    if (archivePathHeader != null) {
                        archivePath = archivePathHeader.getValue();
                    }
                }
            }

        } catch (IOException e) {
            getLog().warn("Error encountered when trying to archive link!: " + e.getMessage());
        } finally {
            closeHttpObjects(response, client);
        }

        if (StringUtils.isNoneBlank(archivePath)) {
            stashUrl.setStashedUrl(String.format(getArchivedUrlFormat(), archivePath));
            stashUrl.setLastStashed(TimeUtils.getTimeGmt());
            getLog().info("Archive link successful: " + stashUrl.getStashedUrl());
        } else {
            // Set the attempted save url, which we can then later use as hyperlink in
            // failure message; we WON'T set lastArchived, which is how we detect if save
            // worked or not
            stashUrl.setStashedUrl(saveRequestUrl);
            getLog().warn("Couldn't obtain archive for URL: " + urlString);
        }
    }


    private void closeHttpObjects(CloseableHttpResponse response, CloseableHttpClient client) {
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                getLog().warn("Could not close HTTP Response!: " + e.getMessage());
            }
        }
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                getLog().warn("Could not close HTTP Client!: " + e.getMessage());
            }
        }
    }

}
