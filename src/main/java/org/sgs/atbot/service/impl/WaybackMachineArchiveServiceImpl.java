package org.sgs.atbot.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;
import org.sgs.atbot.service.ArchiveService;
import org.sgs.atbot.url.ArchiveResult;
import org.sgs.atbot.url.AtbotUrl;

import net.dean.jraw.models.CommentNode;

public class WaybackMachineArchiveServiceImpl implements ArchiveService {
    private static final Logger LOG = LogManager.getLogger(WaybackMachineArchiveServiceImpl.class);
    private static final String WAYBACK_SAVE_URL = "https://web.archive.org/save/";
    private static final String WAYBACK_ROOT_URL = "https://web.archive.org";
    private static final Pattern RESPONSE_CODE_PATTERN = Pattern.compile("([0-9]{3})");


    @Override
    public ArchiveResult archiveUrls(CommentNode parentCommentNode, CommentNode summoningNode, List<String> extractedUrls) {
        ArchiveResult archiveResult = new ArchiveResult(parentCommentNode, summoningNode, extractedUrls);
        for (AtbotUrl atbotUrl : archiveResult.getUrlsToArchive()) {
            doArchive(atbotUrl);
        }

        return archiveResult;
    }


    private void doArchive(AtbotUrl atbotUrl) {
        String urlString = atbotUrl.getOriginalUrl();
        String encodedUrl = WAYBACK_SAVE_URL + urlString;
        ProcessBuilder pb = new ProcessBuilder("curl", "-Is", encodedUrl);

        String content = null;
        try {
            Process p = pb.start();
            InputStream is = p.getInputStream();
            content = IOUtils.toString(new InputStreamReader(is));
        } catch (IOException e) {
            LOG.warn("Couldn't archive link: " + encodedUrl);
            LOG.warn("Error: " + e.getMessage());
            return;
        }

        Map<String, String> headerMap = parseHeader(content);
        String waybackPath = headerMap.get("Content-Location");

        atbotUrl.setArchivedUrl(WAYBACK_ROOT_URL + waybackPath);
        atbotUrl.setLastArchived(Calendar.getInstance().getTime());
    }


    private Map<String, String> parseHeader(String headerString) {
        String scrubbedHeaderString = headerString.replaceAll("\r", "");
        String[] tokens = scrubbedHeaderString.split("\n");
        List<String> lines = new ArrayList<>();
        Collections.addAll(lines, tokens);

        String responseCode = lines.remove(0);
        Matcher matcher = RESPONSE_CODE_PATTERN.matcher(responseCode);
        matcher.find();
        responseCode = matcher.group(1);

        Map<String, String> headerMap = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length > 1) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                headerMap.put(key, value);
            }
        }

        headerMap.put("responseCode", responseCode);

        return headerMap;
    }

}
