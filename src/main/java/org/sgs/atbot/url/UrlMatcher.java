package org.sgs.atbot.url;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UrlMatcher {
    private static final Logger LOG = LogManager.getLogger(UrlMatcher.class);
    private static final Pattern URL_PATTERN = Pattern.compile("((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");


    public static List<String> extractUrls(String comment) {
        List<String> urls = new ArrayList<>();

        if (comment == null) {
            LOG.warn("Null comment passed in, skipping.");
            return urls;
        }

        Matcher matcher = URL_PATTERN.matcher(comment);
        while (matcher.find()) {
            String result = matcher.group(1);
            if (StringUtils.isNotBlank(result)) {
                urls.add(result);
            }
        }

        return urls;
    }

}
