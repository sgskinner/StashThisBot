package org.sgs.stashbot;

import org.junit.jupiter.api.Test;
import org.sgs.stashbot.dao.AuthTimeDao;
import org.sgs.stashbot.dao.BlacklistedUserDao;
import org.sgs.stashbot.dao.RedditTimeServiceDao;
import org.sgs.stashbot.dao.StashResultDao;
import org.sgs.stashbot.model.AuthPollingTime;
import org.sgs.stashbot.model.BlacklistedUser;
import org.sgs.stashbot.model.RedditPollingTime;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.util.PostFormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;

import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;
import static org.springframework.test.util.AssertionErrors.assertTrue;


@SpringBootTest
public class PersistenceServiceTest extends GeneratorTestBase {
    @Autowired
    private AuthTimeDao authTimeDao;
    @Autowired
    private RedditTimeServiceDao redditTimeServiceDao;
    @Autowired
    private BlacklistedUserDao userDao;
    @Autowired
    private StashResultDao stashResultDao;


    @Test
    public void testFormatter() {
        StashResult stashResult = generateDummyStashResult();
        PostFormatterService postFormatterService = new PostFormatterService();
        String output = postFormatterService.format(stashResult);
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

        AuthPollingTime returnedTime = authTimeDao.findFirstBySuccessIsTrueOrderByDateDesc();
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

        RedditPollingTime returnedTime = redditTimeServiceDao.findTopByOrderByDate();
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
        boolean exists = stashResultDao.existsByTargetPostableId("SVdBrk2");// in dummy data
        assertTrue("StashResult should exist in dummy data!", exists);

        exists = stashResultDao.existsByTargetPostableId("lksdjfl;asdjkf");// made up id, should fail
        assertFalse("Made up ID should not pull valid record!", exists);
    }


    @Test
    public void testSaveStashResult() {
        StashResult stashResult = generateDummyStashResult();
        stashResultDao.save(stashResult);

        StashResult stashResult1 = stashResultDao.findByTargetPostableId(stashResult.getTargetPostableId());
        assertNotNull("Should get back one result that we just inserted!", stashResult1);

        BigInteger id = stashResult1.getId();
        assertNotNull("Assigned id should not be null!", id);
//        for (StashUrl stashUrl : stashResult1.getStashUrls()) {
//            assertNotNull("stashUrl should not be null!", stashUrl);
//            assertNotNull("stashUrl id should not be null!", stashUrl.getId()); // set by hibernate save
//        }

        stashResultDao.delete(stashResult1);
        assertFalse("stashResult1 should not exist after deletions!",
                stashResultDao.existsByTargetPostableId(stashResult.getTargetPostableId()));
    }


    @Test
    public void testStashResultDao() {
        StashResult stashResult = stashResultDao.findByTargetPostableId("V1X0rS");// in dummy data file
//        assertTrue("stashResult should have 4 urls!", stashResult.getStashUrls().size() == 4);
    }


    @Test
    public void testFetchOfStashResult() {
        StashResult stashResult = stashResultDao.getById(BigInteger.valueOf(1));

        assertNotNull("stashResult should not be null!", stashResult);
        assertNotNull("stashResult id should not be null", stashResult.getId());
        assertNotNull("Comment author should not be null", stashResult.getTargetPostableAuthor());
        assertNotNull("Comment should not be null", stashResult.getTargetPostableId());
        assertNotNull("Comment url should not be null", stashResult.getTargetPostableUrl());
        assertNotNull("Submission url should not be null", stashResult.getSubmissionUrl());
        assertNotNull("Summoning user should not be null", stashResult.getSummoningCommentAuthor());
        assertNotNull("Summoning comment id should not be null", stashResult.getSummoningCommentId());
        assertNotNull("Summoning comment url should not be null", stashResult.getSummoningCommentUrl());
    }

}
