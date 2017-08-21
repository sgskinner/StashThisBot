package org.sgs.stashbot;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.junit.BeforeClass;
import org.sgs.stashbot.model.BlacklistedUser;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.TimeUtils;

public class GeneratorTestBase {
    private static final String[] TEST_SUMMONER_USERNAMES = {"test-summoner-0", "test-summoner-1", "test-summoner-2", "test-summoner-3", "test-summoner-4"};
    private static final String[] TEST_TARGET_USERNAMES = {"test-target-0", "test-target-1", "test-target-2", "test-target-3", "test-target-4"};

    private static RandomStringGenerator stringGenerator;


    @BeforeClass
    public static void testInit() {
        SecureRandom rand = new SecureRandom();
        stringGenerator = new RandomStringGenerator
                .Builder()
                .usingRandom(rand::nextInt).withinRange(0, 'z')
                .filteredBy(new AlphaNumericPredicate())
                .build();
    }


    protected StashResult generateDummyStashResult() {

        StashResult stashResult = new StashResult();
        stashResult.setSubmissionUrl(generateMockUrl());
        stashResult.setTargetPostableAuthor(getRandomTargetUsername());
        stashResult.setTargetPostableId(stringGenerator.generate(getRandomInt(5, 9)));
        stashResult.setTargetPostableUrl(generateMockUrl());
        stashResult.setSummoningCommentAuthor(getRandomSummonerUsername());
        stashResult.setSummoningCommentId(stringGenerator.generate(getRandomInt(5, 9)));
        stashResult.setSummoningCommentUrl(generateMockUrl());
        stashResult.setRequestDate(getZeroedMilliDate());
        stashResult.setServicedDate(getZeroedMilliDate());
        stashResult.addStashUrls(generateStashUrlList(getRandomInt(1, 5)));

        return stashResult;
    }


    protected List<StashUrl> generateStashUrlList(int howMany) {
        List<StashUrl> stashUrls = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            stashUrls.add(generateStashUrl());
        }

        return stashUrls;
    }


    protected StashUrl generateStashUrl() {
        StashUrl stashUrl = new StashUrl();
        stashUrl.setOriginalUrl(generateMockUrl());
        stashUrl.setStashedUrl(generateMockUrl());
        stashUrl.setLastStashed(getZeroedMilliDate());

        return stashUrl;
    }


    protected String generateMockUrl() {
        StringBuilder sb = new StringBuilder("http://www.");
        sb.append(generateRandomString(getRandomInt(5, 14)));
        sb.append(".com/");
        sb.append(generateRandomString(getRandomInt(6, 10)));
        sb.append(".html");
        return sb.toString();
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
