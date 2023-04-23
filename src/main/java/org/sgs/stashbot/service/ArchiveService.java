package org.sgs.stashbot.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.TimeUtils;
import org.sgs.stashbot.util.UrlMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A class that handles posting links to the archive.is website.
 */
@Primary
@Service
public class ArchiveService {
    private static final Logger LOG = LoggerFactory.getLogger(ArchiveService.class);
    private static final String XPATH_TO_SUBMIT_ID = "//*[@id=\"submiturl\"]/input/@value";
    private static final String HEADER_LOCATION_KEY = "Location";
    private static final String HEADER_REFRESH_KEY = "Refresh";
    private static final String GET_REQUEST_URL = "https://archive.today";
    private static final String POST_REQUEST_URL = "http://archive.today/submit/";
    private static final String FAIL_LINK_FORMAT = "https://archive.today/?run=1&url={}";
    private static final String URL_FORM_KEY = "url";
    private static final String SUBMIT_ID_FORM_KEY = "submitid";


    /**
     *  This method attempts to:
     *  1. Get a token from archive.is within a hidden form field
     *  2. Use the token to submit StashUrl for archiving
     *
     * @param stashUrl the container object whose summoner's URL should be archived
     */
    protected void executeHttpTransactions(StashUrl stashUrl) {
        LOG.info("Attempting to archive link: {}", stashUrl.getOriginalUrl());
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String submitId = getSubmitIdToken(client);

        if (StringUtils.isBlank(submitId)) {
            setArchivedLink(null, stashUrl);
            return;
        }

        String archivedLink = getArchivedLink(client, stashUrl, submitId);
        setArchivedLink(archivedLink, stashUrl);

        closeHttpObjects(client);
    }


    /*
     * The post method on archive.is needs a hidden form field, which is
     * on the landing page of the site. This method goes and pulls the
     * page, and extracts/returns this token
     */
    private String getSubmitIdToken(CloseableHttpClient httpClient) {
        HttpGet getMethod = new HttpGet(GET_REQUEST_URL);

        CloseableHttpResponse response = null;
        String submitId = null;
        try {
            response = httpClient.execute(getMethod);
            int statusCode = response.getCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOG.warn("Could not get submit token, got response code: {}!", statusCode);
                return null;
            }

            String htmlContents = EntityUtils.toString(response.getEntity());
            TagNode tagNode = new HtmlCleaner().clean(htmlContents);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPath xpath = XPathFactory.newInstance().newXPath();
            submitId = (String) xpath.evaluate(XPATH_TO_SUBMIT_ID, doc, XPathConstants.STRING);

        } catch (IOException e) {
            LOG.error("Error encountered when trying to archive link!: {}", e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.error("Could not parse html for submitId token: {}", e.getMessage());
        } catch (XPathExpressionException e) {
            LOG.error("Xpath failed when trying to extract submitId token: {}", e.getMessage());
        } catch (Exception e) {
            LOG.error("Unkown exception when trying to extract submitId token: {}", e.getMessage());
        } finally {
            closeHttpObjects(response);
        }

        return submitId;
    }


    /*
     * This method executes the post method, which is the actual archive request. The
     * response is actaully a 302 redirect, but the header contains the link to the
     * archived url. It's this header link that this method returns.
     */
    private String getArchivedLink(CloseableHttpClient httpClient, StashUrl stashUrl, String submitId) {
        HttpPost postMethod = getPostMethod(stashUrl.getOriginalUrl(), submitId);

        String archivedLink = null;
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(postMethod);
            int statusCode = response.getCode();

            String headerKey;
            if (statusCode == HttpStatus.SC_OK) {
                // For links that are saved already (and haven't changed?)
                headerKey = HEADER_REFRESH_KEY;
                LOG.info("Remote service detected this has been saved recently.");
            } else if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                // For links the service regards as never saved before
                headerKey = HEADER_LOCATION_KEY;
                LOG.info("Remote service detected this has never been saved.");
            } else {
                LOG.error("Unknown response code: {}", statusCode);
                return null;
            }

            Header[] headers = response.getHeaders(headerKey);
            if (headers != null && headers.length > 0) {
                Header archivePathHeader = headers[0];
                if (archivePathHeader != null) {
                    // The 'Refresh' header has extra cruft, but there will only
                    // ever be one url. Match w/ regex groups, and use it
                    String headerValue = archivePathHeader.getValue();
                    List<String> urls = UrlMatcher.extractUrls(headerValue);
                    archivedLink = urls.get(0);
                }
            }

        } catch (Exception e) {
            LOG.error("Error encountered when trying to archive link!: " + e.getMessage());
        } finally {
            closeHttpObjects(response);
        }

        return archivedLink;
    }


    public boolean isHealthy() {
        HttpGet getMethod = new HttpGet(GET_REQUEST_URL);

        CloseableHttpResponse response = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            response = httpClient.execute(getMethod);
            int statusCode = response.getCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOG.info("Health check failed, got response code: {}", statusCode);
                return false;
            }

            String htmlContents = EntityUtils.toString(response.getEntity());
            TagNode tagNode = new HtmlCleaner().clean(htmlContents);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPath xpath = XPathFactory.newInstance().newXPath();
            String submitId = (String) xpath.evaluate(XPATH_TO_SUBMIT_ID, doc, XPathConstants.STRING);

            if (StringUtils.isBlank(submitId)) {
                LOG.info("Health check failed, submitId token was null or empty.");
                return false;
            }

        } catch (Throwable t) {
            LOG.info("Health check failed, exception thrown: {}", t.getMessage());
        } finally {
            closeHttpObjects(response, httpClient);
        }

        return true;
    }


    /*
     * Separated out, since we have two spots where we might need to set this:
     *     1) If the submitId token fails, we need to set the 'fail' link, OR
     *     2) After the post method to archive.is is made
     */
    private void setArchivedLink(String archivedLink, StashUrl stashUrl) {
        if (StringUtils.isNotBlank(archivedLink)) {
            stashUrl.setStashedUrl(archivedLink);
            stashUrl.setLastStashed(TimeUtils.getTimeGmt());
            LOG.info("Archive link successful: " + stashUrl.getStashedUrl());
        } else {
            // Set clickable archive.is submission link that failed; we WON'T set
            // lastArchived, which is how we detect if save worked or not
            String failLink = String.format(FAIL_LINK_FORMAT, stashUrl.getOriginalUrl());
            stashUrl.setStashedUrl(failLink);
            LOG.warn("Couldn't obtain archive for URL: " + stashUrl.getOriginalUrl());
        }
    }


    /*
     * Currently the archive.is form for submitting has one visible form field,
     * and one hidden. We need to submit both, which we build here.
     */
    private HttpPost getPostMethod(String urlToSave, String submitId) {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(URL_FORM_KEY, urlToSave));
        params.add(new BasicNameValuePair(SUBMIT_ID_FORM_KEY, submitId));

        HttpPost httpPost = new HttpPost(POST_REQUEST_URL);
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        return httpPost;
    }


    public void archive(StashResult stashResult) {
        for (StashUrl stashUrl : stashResult.getStashUrls()) {
            executeHttpTransactions(stashUrl);
        }

        stashResult.setProcessedDate(TimeUtils.getTimeGmt());
    }


    private void closeHttpObjects(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    LOG.warn("Could not close response/client!: " + e.getMessage());
                }
            }
        }
    }

}
