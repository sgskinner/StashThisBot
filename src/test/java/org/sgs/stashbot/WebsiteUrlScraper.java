package org.sgs.stashbot;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sgs.stashbot.util.UrlMatcher;

public class WebsiteUrlScraper {
    private static final Logger LOG = LogManager.getLogger(WebsiteUrlScraper.class);
    private static final String RANDOM_URL_SITE_FORMAT = "http://belong.io/?when=%s";
    private static final int SLEEP_INTERVAL = 3000;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private LocalDate dayToScrape;

    public WebsiteUrlScraper() {
        dayToScrape = LocalDate.now();
    }


    public void fetchUrls(int howManyToFetch) throws IOException, InterruptedException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        PrintWriter writer = getWriter();

        CloseableHttpResponse response = null;
        int numFetched = 0;
        while (numFetched <= howManyToFetch) {
            LOG.info("About to fetch new round of urls...");
            HttpGet getMethod = new HttpGet(getNextUrlToScrape());
            response = client.execute(getMethod);

            int statusCode = response.getStatusLine().getStatusCode();
            LOG.info("Received %d status code.", statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException(String.format("Bad HTTP status code returned: %s", statusCode));
            }

            LOG.info("Pulling html...");
            String html = EntityUtils.toString(response.getEntity());
            List<String> urls = UrlMatcher.extractUrls(html);
            LOG.info("Extracted %d urls", urls.size());

            Map<String, Integer> knownUrlMap = new HashMap<>();
            Iterator<String> urlIter = urls.iterator();
            while (urlIter.hasNext()) {
                String url = urlIter.next();
                if (url.length() > 16 && !knownUrlMap.containsKey(url)) {
                    knownUrlMap.put(url, 1);
                    writer.println(url);
                } else {
                    urlIter.remove();
                }
            }

            numFetched += urls.size();

            LOG.info("Wrote %d urls to file.", urls.size());

            LOG.info("%d/%d fetched so far", numFetched, howManyToFetch);

            LOG.info("Sleeping for %d seconds.", SLEEP_INTERVAL);
            //Thread.sleep(SLEEP_INTERVAL);
        }

        IOUtils.closeQuietly(writer);
        closeHttpObjects(response, client);

    }


    /*
     * Returns URL appropriate for scraping, and each subsequent call will represent
     * a day earlier in history (first call is current day)
     */
    private String getNextUrlToScrape() {
        return String.format(RANDOM_URL_SITE_FORMAT, getNextDayToScrape());
    }


    /*
     * Returns "yyyy-MM-dd" and then decrements the day so that each call will
     * return one day previous from the last
     */
    private String getNextDayToScrape() {
        String result = DATE_FORMATTER.format(dayToScrape);
        dayToScrape = dayToScrape.minusDays(1);
        LOG.info("About to scrape for %s", result);

        return result;
    }


    /*
     * This assumes the file already exists, and will append all new content
     */
    private PrintWriter getWriter() throws IOException {
        FileWriter fileWriter = new FileWriter("random_urls.txt", true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        return new PrintWriter(bufferedWriter);
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


    public void randomizeFileOrder() throws IOException {
        FileReader reader = new FileReader("unique_urls.txt");
        Scanner scanner = new Scanner(reader);

        List<String> lines = new ArrayList<>();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
        }

        Collections.shuffle(lines);
        Collections.shuffle(lines);
        Collections.shuffle(lines);
        Collections.shuffle(lines);
        Collections.shuffle(lines);

        FileWriter fileWriter = new FileWriter("randomized_results.txt", false);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter writer = new PrintWriter(bufferedWriter);

        for (String line : lines) {
            writer.println(line);
        }

        writer.flush();
        writer.close();

    }


    public static void main(String... sgs) {
        WebsiteUrlScraper urlFetcher = new WebsiteUrlScraper();
        try {
            //urlFetcher.fetchUrls(120000);
            urlFetcher.randomizeFileOrder();
        } catch (Exception e) {
            LOG.fatal("Could not process, caught exception: '%s'", e.getMessage());
        }
    }
}
