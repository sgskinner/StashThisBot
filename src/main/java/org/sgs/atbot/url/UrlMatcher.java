package org.sgs.atbot.url;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class UrlMatcher {
    private static final Pattern URL_PATTERN = Pattern.compile("((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");


    public static List<String> extractUrls(String comment) {
        List<String> urls = new ArrayList<>();
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
