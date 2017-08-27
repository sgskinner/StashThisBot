package org.sgs.stashbot;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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


/**
 * A class to scrape real-world and recent URL links, and then convert those
 * links into an SQL file suitable to load directly into the DB. These new
 * records can then be used by other test classes to flex archive service impls
 * with real cases.
 */
public class WebsiteUrlScraper {
    private static final Logger LOG = LogManager.getLogger(WebsiteUrlScraper.class);
    private static final String RANDOM_URL_SITE_FORMAT = "http://belong.io/?when=%s"; // '%s' is yyyy-MM-dd
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int SLEEP_INTERVAL = 10*1000;// 10 seconds
    private static final String URL_INSERT_FORMAT = "insert into scraped_url_t (date, url) values (now(), '%s');";

    private LocalDate dayToScrape;

    public WebsiteUrlScraper() {
        dayToScrape = LocalDate.now();
    }


    /**
     * Fetch URLs scraped from RANDOM_URL_SITE_FORMAT, scraping howManyToFetch URLs,
     * by pulling each days worth of links until number of links are gathered.
     *
     * @param howManyToFetch method will continue scraping until this many links are gathered
     * @throws IOException thrown if input or output files are not accessable
     * @throws InterruptedException thrown if woken up while sleeping between each scrape
     */
    public void fetchUrls(int howManyToFetch) throws IOException, InterruptedException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        PrintWriter writer = getWriter("src/main/resources/raw_data/random_urls.txt", true);

        Set<String> knownUrlSet = new HashSet<>();
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

            Iterator<String> urlIter = urls.iterator();
            while (urlIter.hasNext()) {
                String url = urlIter.next();
                if (url.length() > 16 && !url.contains(";")) {
                    // Three things happened here:
                    // 1) we're discarding short urls which were malformed
                    // 2) we're dropping urls that contain ";" which messes up sql
                    //    parsing in DbDataLoader (6 out of 110k dropped)
                    // 3) we're deduplicating by adding to a set
                    knownUrlSet.add(url);
                } else {
                    // Discard into the ether
                    urlIter.remove();
                }
            }

            for (String url : knownUrlSet) {
                writer.println(url);
            }

            numFetched += urls.size();

            LOG.info("Wrote %d urls to file.", urls.size());

            LOG.info("%d/%d fetched so far", numFetched, howManyToFetch);

            LOG.info("Sleeping for %d seconds.", SLEEP_INTERVAL);
            Thread.sleep(SLEEP_INTERVAL);
        }

        IOUtils.closeQuietly(writer);
        closeHttpObjects(response, client);

    }


    /**
     * Use the output file from fetchUrls() to build one insert statement per url found,
     * output file that can directly be loaded into DB.
     *
     * @throws IOException thrown if input/output files are not accessable
     */
    public void generateUrlInsertFile() throws IOException {
        Scanner scanner = new Scanner(new FileReader("src/main/resources/raw_data/random_urls.txt"));
        PrintWriter writer = getWriter("src/main/resources/sql/scraped_url_t.sql", false);

        while (scanner.hasNext()) {
            String url = scanner.nextLine();
            String insertStatment = String.format(URL_INSERT_FORMAT, url);
            writer.println(insertStatment);
        }

        writer.flush();
        IOUtils.closeQuietly(writer);
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
     * Did this more than once, so extract to own method
     */
    private PrintWriter getWriter(String filename, boolean append) throws IOException {
        FileWriter fileWriter = new FileWriter(filename, append);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        return new PrintWriter(bufferedWriter);
    }


    /*
     * Cleanup after ourselves
     */
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


    /*
     * First pull URLs and write them raw to file, then take those raw
     * URLs and generate one SQL insert for each URL, finally, write all
     * insert statements to file.
     *
     * These URLs will be used by the testing framework in order to flex
     * the archive service impls with real URLs.
     */
    public static void main(String... sgs) {
        WebsiteUrlScraper urlFetcher = new WebsiteUrlScraper();
        try {
            int howMany = ThreadLocalRandom.current().nextInt(300, 500);
            urlFetcher.fetchUrls(howMany);
            urlFetcher.generateUrlInsertFile();
        } catch (Exception e) {
            LOG.fatal("Could not process, caught exception: '%s'", e.getMessage());
        }

        LOG.info("Completed, exiting.");
    }

}
