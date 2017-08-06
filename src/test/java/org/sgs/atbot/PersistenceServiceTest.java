package org.sgs.atbot;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sgs.atbot.model.ArchiveResult;
import org.sgs.atbot.model.AtbotUrl;
import org.sgs.atbot.model.AuthPollingTime;
import org.sgs.atbot.model.BlacklistedUser;
import org.sgs.atbot.model.RedditPollingTime;
import org.sgs.atbot.service.ArchiveResultService;
import org.sgs.atbot.service.AuthTimeService;
import org.sgs.atbot.service.RedditTimeService;
import org.sgs.atbot.service.UserService;
import org.sgs.atbot.spring.SpringContext;
import org.sgs.atbot.util.ArchiveResultPostFormatter;
import org.sgs.atbot.util.TimeUtils;


public class PersistenceServiceTest {
    private static final String[] TEST_SUMMONER_USERNAMES = {"test-summoner-0", "test-summoner-1", "test-summoner-2", "test-summoner-3", "test-summoner-4"};
    private static final String[] TEST_TARGET_USERNAMES = {"test-target-0", "test-target-1", "test-target-2", "test-target-3", "test-target-4"};

    private static RandomStringGenerator stringGenerator;


    @Test
    public void testFormatter() {
        ArchiveResult archiveResult = generateDummyArchiveResult();
        String output = ArchiveResultPostFormatter.format(archiveResult);
        System.out.println(output);
    }


    @Test
    public void testAuthTimeService() {
        AuthTimeService service = SpringContext.getBean(AuthTimeService.class);

        AuthPollingTime time1 = new AuthPollingTime();
        time1.setDate(getZeroedMilliDate());
        time1.setSuccess(false);
        service.save(time1);

        AuthPollingTime time2 = new AuthPollingTime();
        time2.setDate(getZeroedMilliDate());
        time2.setSuccess(true);
        service.save(time2);

        AuthPollingTime time3 = new AuthPollingTime();
        time3.setDate(getZeroedMilliDate());
        time3.setSuccess(false);
        service.save(time3);

        AuthPollingTime returnedTime = service.getLastSuccessfulAuth();
        Assert.assertNotNull("Polling time should not be null!", returnedTime);
        Assert.assertTrue("Success attribute should be 'true'!", returnedTime.isSuccess());
        Assert.assertTrue("Returned result is not the last successful auth time!", returnedTime.getId().equals(time2.getId()));
        Assert.assertTrue("Dates for last successful auth time should match!!!", returnedTime.getDate().compareTo(time2.getDate()) == 0);

        service.delete(time1);
        service.delete(time2);
        service.delete(time3);

    }


    @Test
    public void testRedditTimeService() {
        RedditTimeService service = SpringContext.getBean(RedditTimeService.class);

        RedditPollingTime time1 = new RedditPollingTime();
        time1.setDate(getZeroedMilliDate());
        service.save(time1);

        RedditPollingTime time2 = new RedditPollingTime();
        time2.setDate(getZeroedMilliDate());
        service.save(time2);

        RedditPollingTime time3 = new RedditPollingTime();
        time3.setDate(getZeroedMilliDate());
        service.save(time3);

        RedditPollingTime returnedTime = service.getLastPollingTime();
        Assert.assertNotNull("Polling time should not be null!", returnedTime);
        Assert.assertTrue("Returned result is not the latest polling time!", returnedTime.getId().equals(time3.getId()));
        Assert.assertTrue("Dates for latest polling time should match!!!", returnedTime.getDate().compareTo(time3.getDate()) == 0);

        service.delete(time1);
        service.delete(time2);
        service.delete(time3);

    }


    private Date getZeroedMilliDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeUtils.GMT_TIME_ZONE);
        return calendar.getTime();
    }


    @Test
    public void testBlacklistedUser () {
        UserService service = SpringContext.getBean(UserService.class);
        BlacklistedUser user = generateBlacklistedUser();

        service.save(user);

        BlacklistedUser returnedUser = service.getBlackListedUserbyUsername(user.getUsername());
        String username = returnedUser.getUsername();
        Assert.assertTrue("Should return exactly one user!", returnedUser != null);
        Assert.assertTrue("Username should match what was saved!", returnedUser.getUsername().equals(username));

        service.delete(user);

        returnedUser = service.getBlackListedUserbyUsername(username);
        Assert.assertNull("Deleted user should not be returned in search!", returnedUser);
    }


    private BlacklistedUser generateBlacklistedUser() {
        BlacklistedUser user = new BlacklistedUser();
        user.setUsername(getRandomSummonerUsername());
        user.setDateCreated(getZeroedMilliDate());
        user.setReason(generateRandomString(getRandomInt(10, 20)));

        return user;
    }


    @Test
    public void testArchiveResultIsServiced() {
        ArchiveResultService service = SpringContext.getBean(ArchiveResultService.class);

        boolean exists = service.existsByTargetCommentId("SVdBrk2");// in dummy data
        Assert.assertTrue("ArchiveResult should exist in dummy data!", exists);

        exists = service.existsByTargetCommentId("lksdjfl;asdjkf");// made up id, should fail
        Assert.assertFalse("Made up ID should not pull valid record!", exists);
    }


    @Test
    public void testSaveArchiveResult() {
        ArchiveResult archiveResult = generateDummyArchiveResult();
        ArchiveResultService service = SpringContext.getBean(ArchiveResultService.class);
        service.save(archiveResult);

        ArchiveResult returnedBo = service.findByTargetCommentId(archiveResult.getTargetCommentId());
        Assert.assertNotNull("Should get back one result that we just inserted!", returnedBo);

        BigInteger id = returnedBo.getId();
        Assert.assertNotNull(id);
        for (AtbotUrl atbotUrl : returnedBo.getArchivedUrls()) {
            Assert.assertNotNull(atbotUrl);
            Assert.assertTrue(atbotUrl.getId() != null); // set by hibernate save
        }

        service.delete(returnedBo);
        Assert.assertTrue(!service.existsByTargetCommentId(archiveResult.getTargetCommentId()));
    }


    private ArchiveResult generateDummyArchiveResult() {

        ArchiveResult archiveResult = new ArchiveResult();
        archiveResult.setSubmissionUrl(generateMockUrl());
        archiveResult.setTargetCommentAuthor(getRandomTargetUsername());
        archiveResult.setTargetCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        archiveResult.setTargetCommentUrl(generateMockUrl());
        archiveResult.setSummoningCommentAuthor(getRandomSummonerUsername());
        archiveResult.setSummoningCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        archiveResult.setSummoningCommentUrl(generateMockUrl());
        archiveResult.setRequestDate(getZeroedMilliDate());
        archiveResult.setServicedDate(getZeroedMilliDate());
        archiveResult.addAtbotUrls(generateAtbotUrlList(getRandomInt(1, 5)));

        return archiveResult;
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
        atbotUrl.setLastArchived(getZeroedMilliDate());

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


    private String getRandomTargetUsername() {
        return TEST_TARGET_USERNAMES[getRandomInt(0, TEST_TARGET_USERNAMES.length)];
    }


    @Test
    public void testArchiveResultDao() {
        ArchiveResultService service = SpringContext.getBean(ArchiveResultService.class);
        ArchiveResult archiveResult = service.findByTargetCommentId("V1X0rS");// in dummy data file
        Assert.assertTrue(archiveResult.getArchivedUrls().size() == 4);
    }


    @Test
    public void testFetchOfArchiveResult() {
        ArchiveResultService archiveResultDao = SpringContext.getBean(ArchiveResultService.class);
        ArchiveResult archiveResult = archiveResultDao.findById(new BigInteger("1"));

        Assert.assertNotNull(archiveResult);
        Assert.assertNotNull(archiveResult.getTargetCommentAuthor());
        Assert.assertNotNull(archiveResult.getTargetCommentId());
        Assert.assertNotNull(archiveResult.getTargetCommentUrl());
        Assert.assertNotNull(archiveResult.getSubmissionUrl());
        Assert.assertNotNull(archiveResult.getSummoningCommentAuthor());
        Assert.assertNotNull(archiveResult.getSummoningCommentId());
        Assert.assertNotNull(archiveResult.getSummoningCommentUrl());
        Assert.assertNotNull(archiveResult.getId());

    }


    @BeforeClass
    public static void testInit() {
        SecureRandom rand = new SecureRandom();
        stringGenerator = new RandomStringGenerator
                .Builder()
                .usingRandom(rand::nextInt).withinRange(0, 'z')
                .filteredBy(new AlphaNumericPredicate())
                .build();
    }


    static class AlphaNumericPredicate implements CharacterPredicate {

        @Override
        public boolean test(int codePoint) {
            return CharacterPredicates.DIGITS.test(codePoint) ||
                    CharacterPredicates.LETTERS.test(codePoint);

        }
    }

}
