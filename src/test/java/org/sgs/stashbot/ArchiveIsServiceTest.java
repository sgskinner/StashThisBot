/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * StashThisBot - Summon this bot to archive URLs in an archive service.
 * Copyright (C) 2017  S.G. Skinner
 */

package org.sgs.stashbot;

import static org.sgs.stashbot.service.impl.ArchiveIsServiceImpl.FAILURE_STAMP;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.junit.Assert;
import org.junit.Test;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.service.ArchiveService;
import org.sgs.stashbot.spring.SpringContext;
import org.w3c.dom.Document;

public class ArchiveIsServiceTest extends GeneratorTestBase {

    @Test
    public void testArchive() {
        ArchiveService archiveService = SpringContext.getBean(ArchiveService.class);
        Assert.assertTrue("ArchiveService could not initialize.", archiveService != null);

        StashResult stashResult  = generateDummyStashResult(true);
        archiveService.archive(stashResult);
        Assert.assertTrue("Not marked as serviced!", stashResult.getServicedDate() != null);

        List<StashUrl> stashUrlList = stashResult.getStashUrls();
        for (StashUrl stashUrl : stashUrlList) {
            String archivedUrl = stashUrl.getOriginalUrl();
            Assert.assertTrue("Archive URL not acceptable!", !archivedUrl.contains("archive.is") && !archivedUrl.equals(FAILURE_STAMP));
        }
    }



    @Test
    public void testXpathExtraction() throws IOException, ParserConfigurationException, XPathExpressionException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/raw_data/archive.is.html"));
        String htmlContents = new String(encoded, StandardCharsets.UTF_8);

        TagNode tagNode = new HtmlCleaner().clean(htmlContents);
        Document doc = new DomSerializer(new CleanerProperties()).createDOM(tagNode);

        XPath xpath = XPathFactory.newInstance().newXPath();
        String str = (String) xpath.evaluate("//*[@id=\"submiturl\"]/input/@value", doc, XPathConstants.STRING);

        String actualValue = "YHuwL/nTgL370PMDM2G2vkuvMg3kmNqk/y/i7NRSaLyf2JSIU+/now+AYw+X0nX8";
        Assert.assertTrue("Did not extract expected value!", str.equals(actualValue));
    }


}
