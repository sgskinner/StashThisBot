package org.sgs.stashbot.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;


@Component
public class SummonTokenMatcher {
    private static final Logger LOG = LogManager.getLogger(SummonTokenMatcher.class);

    @Resource(name = "summonTokens")
    private List<String> summonTokens;
    private Pattern TOKEN_PATTERN;


    public SummonTokenMatcher() {
        // Needed by ORM
    }


    public List<String> extractTokens(String comment) {
        if (TOKEN_PATTERN == null) {
            initPattern();
        }

        List<String> foundTokens = new ArrayList<>();

        if (comment == null) {
            LOG.warn("Null comment passed in, skipping.");
            return foundTokens;
        }

        Matcher matcher = TOKEN_PATTERN.matcher(comment);
        while (matcher.find()) {
            String result = matcher.group(1);
            if (StringUtils.isNotBlank(result)) {
                foundTokens.add(result);
            }
        }

        return foundTokens;
    }


    private void initPattern() {
        StringBuilder regex = new StringBuilder();
        regex.append("(");
        for (String token : summonTokens) {
            regex.append(token);
            regex.append("|");
        }
        regex.deleteCharAt(regex.length() - 1); // fence post on the pipe
        regex.append(")");

        TOKEN_PATTERN = Pattern.compile(regex.toString());
    }


    public List<String> getSummonTokens() {
        return summonTokens;
    }


    public void setSummonTokens(List<String> summonTokens) {
        this.summonTokens = summonTokens;
    }

}
