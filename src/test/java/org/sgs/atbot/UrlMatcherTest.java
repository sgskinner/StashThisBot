package org.sgs.atbot;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.sgs.atbot.url.UrlMatcher;

public class UrlMatcherTest {
    private static final Pattern URL_PATTERN = Pattern.compile("((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");


    @Test
    public void testSingleUrlExtraction() {
        String comment = "Test user comment:\n" +
                "\n" +
                "Test [URL](https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/).\n" +
                "\n" +
                "End text.";
        String url = "https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/";
        Matcher matcher = URL_PATTERN.matcher(comment);
        while (matcher.find()) {
            String result = matcher.group(1);
            Assert.assertTrue("Result should not be empty of null!", result.equals(url));
        }
    }


    @Test
    public void testMultipleUrlExtraction() {
        String comment = "Seed post 0; test, test, test\n" +
                "\n" +
                "Test [URL](http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125), test, test, test.\n" +
                "\n" +
                "End text." +
                "\n" +
                "Test user comment:\n" +
                "\n" +
                "Test [URL](https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/).\n" +
                "\n" +
                "End text.";
        String urlOne = "http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125";
        String urlTwo = "https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/";
        Matcher matcher = URL_PATTERN.matcher(comment);
        while (matcher.find()) {
            String result = matcher.group(1);
            Assert.assertTrue("Result should not be empty of null!", result.equals(urlOne) || result.equals(urlTwo));
        }
    }


    @Test
    public void testUrlMatcherClass() {
        String comment = "Seed post 0; test, test, test\n" +
                "\n" +
                "Test [URL](http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125), test, test, test.\n" +
                "\n" +
                "End text." +
                "\n" +
                "Test user comment:\n" +
                "\n" +
                "Test [URL](https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/).\n" +
                "\n" +
                "End text.";
        String urlOne = "http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125";
        String urlTwo = "https://www.usatoday.com/story/news/nation-now/2017/07/15/southern-california-fire-evacuates-thousands/481873001/";
        List<String> urls = UrlMatcher.extractUrls(comment);

        Assert.assertTrue("Did not extract 2 URLs!", urls.size() == 2);
        Assert.assertTrue("Did not get the first URL correctly!", urls.contains(urlOne));
        Assert.assertTrue("Did not get the second URL correctly!", urls.contains(urlTwo));
    }
}
