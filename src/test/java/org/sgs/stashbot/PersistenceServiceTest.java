package org.sgs.stashbot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sgs.stashbot.dao.AuthTimeDao;
import org.sgs.stashbot.dao.BlacklistedUserDao;
import org.sgs.stashbot.dao.RedditTimeServiceDao;
import org.sgs.stashbot.dao.StashResultDao;
import org.sgs.stashbot.model.AuthPollingTime;
import org.sgs.stashbot.model.BlacklistedUser;
import org.sgs.stashbot.model.RedditPollingTime;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.StashResultPostFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;

import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@SpringBootTest
public class PersistenceServiceTest extends GeneratorTestBase {
    private AuthTimeDao authTimeDao;
    private RedditTimeServiceDao redditTimeServiceDao;
    private BlacklistedUserDao userDao;
    private StashResultDao stashResultDao;


    @Test
    public void testFormatter() {
        StashResult stashResult = generateDummyStashResult();
        StashResultPostFormatter stashResultPostFormatter = new StashResultPostFormatter();
        String output = stashResultPostFormatter.format(stashResult);
        System.out.println(output);
    }


    @Test
    public void testAuthTimeDao() {
        AuthPollingTime time1 = new AuthPollingTime();
        time1.setDate(getZeroedMilliDate());
        time1.setSuccess(false);
        authTimeDao.save(time1);

        AuthPollingTime time2 = new AuthPollingTime();
        time2.setDate(getZeroedMilliDate());
        time2.setSuccess(true);
        authTimeDao.save(time2);

        AuthPollingTime time3 = new AuthPollingTime();
        time3.setDate(getZeroedMilliDate());
        time3.setSuccess(false);
        authTimeDao.save(time3);

        AuthPollingTime returnedTime = authTimeDao.getLastSuccessfulAuth();
        assertNotNull("Polling time should not be null!", returnedTime);
        assertTrue("Success attribute should be 'true'!", returnedTime.isSuccess());
        assertTrue("Returned result is not the last successful auth time!", returnedTime.getId().equals(time2.getId()));
        assertTrue("Dates for last successful auth time should match!!!", returnedTime.getDate().compareTo(time2.getDate()) == 0);

        authTimeDao.delete(time1);
        authTimeDao.delete(time2);
        authTimeDao.delete(time3);

    }


    @Test
    public void testRedditTimeService() {
        RedditPollingTime time1 = new RedditPollingTime();
        time1.setDate(getZeroedMilliDate());
        redditTimeServiceDao.save(time1);

        RedditPollingTime time2 = new RedditPollingTime();
        time2.setDate(getZeroedMilliDate());
        redditTimeServiceDao.save(time2);

        RedditPollingTime time3 = new RedditPollingTime();
        time3.setDate(getZeroedMilliDate());
        redditTimeServiceDao.save(time3);

        RedditPollingTime returnedTime = redditTimeServiceDao.getLastPollingTime();
        assertNotNull("Polling time should not be null!", returnedTime);
        assertTrue("Returned result is not the latest polling time!", returnedTime.getId().equals(time3.getId()));
        assertTrue("Dates for latest polling time should match!!!", returnedTime.getDate().compareTo(time3.getDate()) == 0);

        redditTimeServiceDao.delete(time1);
        redditTimeServiceDao.delete(time2);
        redditTimeServiceDao.delete(time3);

    }


    @Test
    public void testBlacklistedUser() {
        BlacklistedUser user = generateBlacklistedUser();

        userDao.save(user);

        BlacklistedUser returnedUser = userDao.findBlacklistedUserByUsername(user.getUsername());
        assertTrue("Should return exactly one user!", returnedUser != null);

        String username = returnedUser.getUsername();
        assertTrue("Username should match what was saved!", returnedUser.getUsername().equals(username));

        userDao.delete(user);

        returnedUser = userDao.findBlacklistedUserByUsername(username);
        assertNull("Deleted user should not be returned in search!", returnedUser);
    }


    @Test
    public void testStashResultIsServiced() {
        boolean exists = stashResultDao.existsByTargetCommentId("SVdBrk2");// in dummy data
        assertTrue("StashResult should exist in dummy data!", exists);

        exists = stashResultDao.existsByTargetCommentId("lksdjfl;asdjkf");// made up id, should fail
        assertFalse("Made up ID should not pull valid record!", exists);
    }


    @Test
    public void testSaveStashResult() {
        StashResult stashResult = generateDummyStashResult();
        stashResultDao.save(stashResult);

        StashResult stashResult1 = stashResultDao.findByTargetCommentId(stashResult.getTargetCommentId());
        assertNotNull("Should get back one result that we just inserted!", stashResult1);

        BigInteger id = stashResult1.getId();
        assertNotNull("Assigned id should not be null!", id);
        for (StashUrl stashUrl : stashResult1.getStashUrls()) {
            assertNotNull("stashUrl should not be null!", stashUrl);
            assertNotNull("stashUrl id should not be null!", stashUrl.getId()); // set by hibernate save
        }

        stashResultDao.delete(stashResult1);
        assertFalse("stashResult1 should not exist after deletions!",
                stashResultDao.existsByTargetCommentId(stashResult.getTargetCommentId()));
    }


    @Test
    public void testStashResultDao() {
        StashResult stashResult = stashResultDao.findByTargetCommentId("V1X0rS");// in dummy data file
        assertTrue("stashResult should have 4 urls!", stashResult.getStashUrls().size() == 4);
    }


    @Test
    public void testFetchOfStashResult() {
        StashResult stashResult = stashResultDao.find(new BigInteger("1"));

        assertNotNull("stashResult should not be null!", stashResult);
        assertNotNull("stashResult id should not be null", stashResult.getId());
        assertNotNull("Comment author should not be null", stashResult.getTargetCommentAuthor());
        assertNotNull("Comment should not be null", stashResult.getTargetCommentId());
        assertNotNull("Comment url should not be null", stashResult.getTargetCommentUrl());
        assertNotNull("Submission url should not be null", stashResult.getSubmissionUrl());
        assertNotNull("Summoning user should not be null", stashResult.getSummoningCommentAuthor());
        assertNotNull("Summoning comment id should not be null", stashResult.getSummoningCommentId());
        assertNotNull("Summoning comment url should not be null", stashResult.getSummoningCommentUrl());
    }

    @Autowired
    public void setAuthTimeDao(AuthTimeDao authTimeDao) {
        this.authTimeDao = authTimeDao;
    }

    @Autowired
    public void setRedditTimeServiceDao(RedditTimeServiceDao redditTimeServiceDao) {
        this.redditTimeServiceDao = redditTimeServiceDao;
    }

    @Autowired
    public void setUserDao(BlacklistedUserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setStashResultDao(StashResultDao stashResultDao) {
        this.stashResultDao = stashResultDao;
    }

}
