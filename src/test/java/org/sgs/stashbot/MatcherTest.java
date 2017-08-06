package org.sgs.stashbot;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;
import org.sgs.stashbot.spring.SpringContext;
import org.sgs.stashbot.util.SummonTokenMatcher;
import org.sgs.stashbot.util.UrlMatcher;

public class MatcherTest {
    private static final Pattern URL_PATTERN = Pattern.compile("((https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])");


    @Test
    public void testSummonTokenMatcher() {
        String testString = "This is some text !ArchiveThis, and boom that just happend!";
        SummonTokenMatcher matcher = SpringContext.getBean(SummonTokenMatcher.class);
        List<String> hits = matcher.extractTokens(testString);
        Assert.assertTrue("Should get exactly one hit!", hits != null && hits.size() == 1);

        testString = "This is some text, nothing here, cya!";
        hits = matcher.extractTokens(testString);
        Assert.assertTrue("Should get exactly one hit!", hits == null || hits.size() == 0);

        testString = "Lets see all !ArchiveThis the tokens at !Archive This once jsut to see it Archive This! happen again and again foo and done.";
        hits = matcher.extractTokens(testString);
        Assert.assertTrue("Should get exactly one hit!", hits != null && hits.size() == 3);
    }



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
