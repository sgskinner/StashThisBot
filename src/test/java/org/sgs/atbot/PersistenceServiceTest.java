package org.sgs.atbot;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sgs.atbot.model.ArchiveResultBo;
import org.sgs.atbot.model.AtbotUrl;
import org.sgs.atbot.service.ArchiveResultBoService;
import org.sgs.atbot.spring.SpringContext;


public class PersistenceServiceTest {
    private static final String[] TEST_SUMMONER_USERNAMES = {"test-summoner-0", "test-summoner-1", "test-summoner-2", "test-summoner-3", "test-summoner-4"};
    private static final String[] TEST_PARENT_USERNAMES = {"test-parent-0", "test-parent-1", "test-parent-2", "test-parent-3", "test-parent-4"};

    private static RandomStringGenerator stringGenerator;


    @Test
    public void testArchiveResultIsServiced() {
        ArchiveResultBoService service = SpringContext.getBean(ArchiveResultBoService.class);

        boolean exists = service.existsByParentCommentId("SVdBrk2");// in dummy data
        Assert.assertTrue("ArchiveResultBo should exist in dummy data!", exists);

        exists = service.existsByParentCommentId("lksdjfl;asdjkf");// made up id, should fail
        Assert.assertFalse("Made up ID should not pull valid record!", exists);
    }


    @Test
    public void testSaveArchiveResultBo() {
        ArchiveResultBo archiveResultBo = generateDummyArchiveResultBo();
        ArchiveResultBoService service = SpringContext.getBean(ArchiveResultBoService.class);
        service.save(archiveResultBo);

        ArchiveResultBo returnedBo = service.findByParentCommentId(archiveResultBo.getParentCommentId());
        Assert.assertNotNull("Should get back one result that we just inserted!", returnedBo);

        BigInteger id = returnedBo.getResultId();
        Assert.assertNotNull(id);
        for (AtbotUrl atbotUrl : returnedBo.getArchivedUrls()) {
            Assert.assertNotNull(atbotUrl);
            Assert.assertTrue(atbotUrl.getUrlId() != null); // set by hibernate save
        }

        service.delete(returnedBo);
        Assert.assertTrue(!service.existsByParentCommentId(archiveResultBo.getParentCommentId()));
    }


    private ArchiveResultBo generateDummyArchiveResultBo() {

        ArchiveResultBo archiveResultBo = new ArchiveResultBo();
        archiveResultBo.setSubmissionUrl(generateMockUrl());
        archiveResultBo.setParentCommentAuthor(getRandomParentUsername());
        archiveResultBo.setParentCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        archiveResultBo.setParentCommentUrl(generateMockUrl());
        archiveResultBo.setSummoningCommentAuthor(getRandomSummonerUsername());
        archiveResultBo.setSummoningCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        archiveResultBo.setSummoningCommentUrl(generateMockUrl());
        archiveResultBo.setRequestDate(Calendar.getInstance().getTime());
        archiveResultBo.setServicedDate(Calendar.getInstance().getTime());
        archiveResultBo.addAtbotUrls(generateAtbotUrlList(getRandomInt(1, 5)));

        return archiveResultBo;
    }


    private List<AtbotUrl> generateAtbotUrlList(int howMany) {
        List<AtbotUrl> atbotUrls = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            atbotUrls.add(generateAtBotUrl());
        }

        return atbotUrls;
    }


    private AtbotUrl generateAtBotUrl() {
        AtbotUrl atbotUrl = new AtbotUrl();
        atbotUrl.setOriginalUrl(generateMockUrl());
        atbotUrl.setArchivedUrl(generateMockUrl());
        atbotUrl.setLastArchived(Calendar.getInstance().getTime());

        return atbotUrl;
    }


    private String generateMockUrl() {
        StringBuilder sb = new StringBuilder("http://www.");
        sb.append(generateRandomString(getRandomInt(5, 14)));
        sb.append(".com/");
        sb.append(generateRandomString(getRandomInt(6, 10)));
        sb.append(".html");
        return sb.toString();
    }


    private String generateRandomString(int length) {
        return stringGenerator.generate(length);
    }


    private int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }


    private String getRandomSummonerUsername() {
        return TEST_SUMMONER_USERNAMES[getRandomInt(0, TEST_SUMMONER_USERNAMES.length)];
    }


    private String getRandomParentUsername() {
        return TEST_PARENT_USERNAMES[getRandomInt(0, TEST_SUMMONER_USERNAMES.length)];
    }


    @Test
    public void testArchiveResultDao() {
        ArchiveResultBoService service = SpringContext.getBean(ArchiveResultBoService.class);
        ArchiveResultBo archiveResultBo = service.findByParentCommentId("V1X0rS");// in dummy data file
        Assert.assertTrue(archiveResultBo.getArchivedUrls().size() == 4);
    }


    @Test
    public void testFetchOfArchiveResult() {
        ArchiveResultBoService archiveResultDao = SpringContext.getBean(ArchiveResultBoService.class);
        ArchiveResultBo archiveResultBo = archiveResultDao.findById(new BigInteger("1"));

        Assert.assertNotNull(archiveResultBo);
        Assert.assertNotNull(archiveResultBo.getParentCommentAuthor());
        Assert.assertNotNull(archiveResultBo.getParentCommentId());
        Assert.assertNotNull(archiveResultBo.getParentCommentUrl());
        Assert.assertNotNull(archiveResultBo.getSubmissionUrl());
        Assert.assertNotNull(archiveResultBo.getSummoningCommentAuthor());
        Assert.assertNotNull(archiveResultBo.getSummoningCommentId());
        Assert.assertNotNull(archiveResultBo.getSummoningCommentUrl());
        Assert.assertNotNull(archiveResultBo.getResultId());

    }


    @BeforeClass
    public static void testInit() {
        SecureRandom rand = new SecureRandom();
        stringGenerator = new RandomStringGenerator.Builder().usingRandom(rand::nextInt).withinRange(0, 'z').filteredBy(new AlphaNumericPredicate()).build();
    }


    static class AlphaNumericPredicate implements CharacterPredicate {

        @Override
        public boolean test(int codePoint) {
            return CharacterPredicates.DIGITS.test(codePoint) ||
                    CharacterPredicates.LETTERS.test(codePoint);

        }
    }

}
