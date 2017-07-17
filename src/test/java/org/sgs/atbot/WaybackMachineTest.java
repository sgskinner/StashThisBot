package org.sgs.atbot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.logging.log4j.core.util.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class WaybackMachineTest {
    private static final String LINK_CHECK_URL_FORMAT = "http://archive.org/wayback/available?url=";

    private static final SimpleDateFormat SDF;
    static {
        SDF = new SimpleDateFormat("yyyyMMddHHmmss");
    }

    @Test
    public void testLinkAvailabilityTest() {
        //String urlString = "http://www.wbir.com/news/crime/woman-arrested-for-towing-kids-in-little-red-wagon/457026125";
        String urlString = "https://github.com/thatJavaNerd/JRAW/issues/29";

        //https://web.archive.org/save/


        URL url;
        String content;
        try {
            String encodedUrl = "https://web.archive.org/save/" + urlString;
            url = new URL(encodedUrl);
            InputStream is = url.openStream();
            content = IOUtils.toString(new InputStreamReader(is));
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            url = getLinkCheckUrl(urlString);
        } catch (Exception e) {
            Assert.fail("URL creation failed!: " + e.getMessage());
            return;
        }

        try {
            InputStream is = url.openStream();
            JsonReader rdr = Json.createReader(is);

            JsonObject obj = rdr.readObject();
            JsonObject snapshots = obj.getJsonObject("archived_snapshots");
            if (snapshots == null) {
                return;
            }

            JsonObject closest = snapshots.getJsonObject("closest");
            if (closest == null) {
                return;
            }

            boolean available = closest.getBoolean("available");
            String archivedUrl = closest.getString("url");
            String timestamp = closest.getString("timestamp");
            String status = closest.getString("status");


            System.out.printf("available: '%s'\n", available);
            System.out.printf("archivedUrl: '%s'\n", archivedUrl);
            System.out.printf("timestamp: '%s'\n", timestamp);
            System.out.printf("status: '%s'\n", status);




        } catch (IOException e) {
            Assert.fail(e.getMessage());
            return;
        }
    }


    private String getTimestamp() {
        String timestamp = SDF.format(Calendar.getInstance().getTime());
        return timestamp;
    }


    private URL getLinkCheckUrl(String urlToCheck) throws MalformedURLException, UnsupportedEncodingException {
        String encodedUrl = LINK_CHECK_URL_FORMAT + URLEncoder.encode(urlToCheck, "UTF-8");
        //String encodedUrl = LINK_CHECK_URL_FORMAT + URLEncoder.encode(urlToCheck, "UTF-8") + "&timestamp=" + getTimestamp();
        return new URL(encodedUrl);
    }
}
