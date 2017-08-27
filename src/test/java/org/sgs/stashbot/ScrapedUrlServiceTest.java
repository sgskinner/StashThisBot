package org.sgs.stashbot;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.sgs.stashbot.model.ScrapedUrl;
import org.sgs.stashbot.service.ScrapedUrlService;
import org.sgs.stashbot.spring.SpringContext;

public class ScrapedUrlServiceTest extends GeneratorTestBase {

    @Test
    public void testGetNextUrl() {
        ScrapedUrlService scrapedUrlService = SpringContext.getBean(ScrapedUrlService.class);

        int howMany = getRandomInt(1, 4);
        Set<ScrapedUrl> urls = new HashSet<>();
        for (int i = 0; i < howMany; i++) {
            urls.add(scrapedUrlService.getNextUrl());
        }

        Assert.assertTrue("Did not get number of expected unique results!", urls.size() == howMany);
        for (ScrapedUrl url : urls) {
            Assert.assertTrue("URL field should never be blank!", StringUtils.isNotBlank(url.getUrl()));
        }

    }
}
