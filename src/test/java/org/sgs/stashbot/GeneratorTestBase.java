package org.sgs.stashbot;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.sgs.stashbot.app.Foo;
import org.sgs.stashbot.dao.ScrapedUrlDao;
import org.sgs.stashbot.model.BlacklistedUser;
import org.sgs.stashbot.model.ScrapedUrl;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@SpringBootTest
public class GeneratorTestBase {
    private static final String[] TEST_SUMMONER_USERNAMES = {"test-summoner-0", "test-summoner-1", "test-summoner-2", "test-summoner-3", "test-summoner-4"};
    private static final String[] TEST_TARGET_USERNAMES = {"test-target-0", "test-target-1", "test-target-2", "test-target-3", "test-target-4"};

    private static RandomStringGenerator stringGenerator;

    @Autowired
    private ScrapedUrlDao scrapedUrlDao;


    @BeforeAll
    public static void testInit() {
        SecureRandom rand = new SecureRandom();
        stringGenerator = new RandomStringGenerator
                .Builder()
                .usingRandom(rand::nextInt).withinRange(0, 'z')
                .filteredBy(new AlphaNumericPredicate())
                .build();
    }


    protected StashResult generateDummyStashResult() {
        return generateDummyStashResult(false);
    }


    protected StashResult generateDummyStashResult(boolean withValidStashUrls) {

        StashResult stashResult = new StashResult();
        stashResult.setSubmissionUrl(generateMockUrl());
        stashResult.setTargetCommentAuthor(getRandomTargetUsername());
        stashResult.setTargetCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        stashResult.setTargetCommentUrl(generateMockUrl());
        stashResult.setSummoningCommentAuthor(getRandomSummonerUsername());
        stashResult.setSummoningCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        stashResult.setSummoningCommentUrl(generateMockUrl());
        stashResult.setRequestDate(getZeroedMilliDate());
        stashResult.setServicedDate(getZeroedMilliDate());

        if (withValidStashUrls) {
            stashResult.setStashUrls(generateValidUrlList(getRandomInt(1, 5)));
        } else {
            stashResult.setStashUrls(generateMockUrlList(getRandomInt(1, 5)));
        }


        return stashResult;
    }


    protected List<StashUrl> generateMockUrlList(int howMany) {
        List<StashUrl> stashUrls = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            stashUrls.add(generateMockStashUrl());
        }

        return stashUrls;
    }


    protected StashUrl generateMockStashUrl() {
        StashUrl stashUrl = new StashUrl();
        stashUrl.setOriginalUrl(generateMockUrl());
        stashUrl.setStashedUrl(generateMockUrl());
        stashUrl.setLastStashed(getZeroedMilliDate());

        return stashUrl;
    }


    protected List<StashUrl> generateValidUrlList(int howMany) {
        List<StashUrl> stashUrls = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            stashUrls.add(generateValidUrl());
        }

        return stashUrls;
    }


    protected StashUrl generateValidUrl() {
        ScrapedUrl scrapedUrl = scrapedUrlDao.getNextScrapedUrl();
        StashUrl stashUrl = new StashUrl();
        stashUrl.setOriginalUrl(scrapedUrl.getUrl());

        return stashUrl;
    }

    protected String generateMockUrl() {
        return "http://www." + generateRandomString(getRandomInt(5, 14)) +
                ".com/" +
                generateRandomString(getRandomInt(6, 10)) +
                ".html";
    }


    protected String generateRandomString(int length) {
        return stringGenerator.generate(length);
    }


    protected int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }


    protected String getRandomSummonerUsername() {
        return TEST_SUMMONER_USERNAMES[getRandomInt(0, TEST_SUMMONER_USERNAMES.length)];
    }


    protected String getRandomTargetUsername() {
        return TEST_TARGET_USERNAMES[getRandomInt(0, TEST_TARGET_USERNAMES.length)];
    }


    protected Date getZeroedMilliDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.setTimeZone(TimeUtils.GMT_TIME_ZONE);
        return calendar.getTime();
    }


    protected BlacklistedUser generateBlacklistedUser() {
        BlacklistedUser user = new BlacklistedUser();
        user.setUsername(getRandomSummonerUsername());
        user.setDateCreated(getZeroedMilliDate());
        user.setReason(generateRandomString(getRandomInt(10, 20)));

        return user;
    }


    static class AlphaNumericPredicate implements CharacterPredicate {

        @Override
        public boolean test(int codePoint) {
            return CharacterPredicates.DIGITS.test(codePoint) ||
                    CharacterPredicates.LETTERS.test(codePoint);

        }
    }

}
