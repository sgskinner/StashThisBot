package org.sgs.stashbot.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.TimeUtils;
import org.sgs.stashbot.util.UrlMatcher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

@Primary
@Service
public class ArchiveIsServiceImpl extends ArchiveServiceBase {
    private static final Logger LOG = LogManager.getLogger(ArchiveIsServiceImpl.class);
    private static final String XPATH_TO_SUBMIT_ID = "//*[@id=\"submiturl\"]/input/@value";
    private static final String HEADER_LOCATION_KEY = "Location";
    private static final String HEADER_REFRESH_KEY = "Refresh";
    private static final String GET_REQUEST_URL = "https://archive.is";
    private static final String POST_REQUEST_URL = "http://archive.is/submit/";
    private static final String FAIL_LINK_FORMAT = "https://archive.today/?run=1&url=%s";


    @Override
    protected void executeHttpTransaction(StashUrl stashUrl) {
        LOG.info("Attempting to archive link: %s", stashUrl.getOriginalUrl());
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
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                LOG.warn("Could not get submit token, got response code: %d!", statusCode);
                return null;
            }

            String htmlContents = EntityUtils.toString(response.getEntity());
            TagNode tagNode = new HtmlCleaner().clean(htmlContents);
            Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

            XPath xpath = XPathFactory.newInstance().newXPath();
            submitId = (String) xpath.evaluate(XPATH_TO_SUBMIT_ID, doc, XPathConstants.STRING);

        } catch (IOException e) {
            LOG.warn("Error encountered when trying to archive link!: %s", e.getMessage());
        } catch (ParserConfigurationException e) {
            LOG.error("Could not parse html for submitId token: %s", e.getMessage());
        } catch (XPathExpressionException e) {
            LOG.error("Xpath failed when trying to extract submitId token: %s", e.getMessage());
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
        CloseableHttpResponse response;
        response = null;
        try {
            response = httpClient.execute(postMethod);
            int statusCode = response.getStatusLine().getStatusCode();

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
                LOG.error("Unknown response code: %d", statusCode);
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

        } catch (IOException e) {
            LOG.warn("Error encountered when trying to archive link!: " + e.getMessage());
        } finally {
            closeHttpObjects(response);
        }

        return archivedLink;
    }


    /*
     * Separated out, since we have two spots in code of where we need to
     * set this: if the submitId token fails, we need to set the 'fail'
     * message, or after the post method regardless if it's successful.
     */
    private void setArchivedLink(String archivedLink, StashUrl stashUrl) {
        if (StringUtils.isNotBlank(archivedLink)) {
            stashUrl.setStashedUrl(archivedLink);
            stashUrl.setLastStashed(TimeUtils.getTimeGmt());
            LOG.info("Archive link successful: " + stashUrl.getStashedUrl());
        } else {
            // Set message that this failed; we WON'T set lastArchived, which is how we detect if save
            // worked or not
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
        HttpPost httpPost = new HttpPost(POST_REQUEST_URL);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("url", urlToSave));
        params.add(new BasicNameValuePair("submitid", submitId));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return httpPost;
    }

}
