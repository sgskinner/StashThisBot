package org.sgs.stashbot;

import org.junit.jupiter.api.Test;
import org.sgs.stashbot.util.UrlMatcher;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;


public class MatcherTest {
    private static final Pattern URL_PATTERN = Pattern.compile("((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");


    @Test
    public void testSingleUrlExtraction() {
        String comment =
                """
                Test user comment:
                Test [URL](https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/).
                End text.
                """;
        String url = "https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/";
        Matcher matcher = URL_PATTERN.matcher(comment);
        while (matcher.find()) {
            String result = matcher.group(1);
            assertThat(result.equals(url)).isTrue();
        }
    }


    @Test
    public void testMultipleUrlExtraction() {
        String comment =
                """
                Seed post 0; test, test, test
                Test [URL](http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125), test, test, test.
                End text.
                Test user comment:
                Test [URL](https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/).
                End text.
                """;
        String urlOne = "http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125";
        String urlTwo = "https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/";
        Matcher matcher = URL_PATTERN.matcher(comment);
        while (matcher.find()) {
            String result = matcher.group(1);
            assertThat(result.equals(urlOne) || result.equals(urlTwo)).isTrue();
        }
    }


    @Test
    public void testUrlMatcherClass() {
        String comment =
                """
                Seed post 0; test, test, test
                Test [URL](http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125), test, test, test.
                End text.
                Test user comment:
                Test [URL](https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/).
                End text.
                """;
        String urlOne = "http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125";
        String urlTwo = "https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/";
        List<String> urls = UrlMatcher.extractUrls(comment);

        assertThat(urls.size() == 2).isTrue();
        assertThat(urls.contains(urlOne)).isTrue();
        assertThat(urls.contains(urlTwo)).isTrue();
    }
}
