package org.sgs.stashbot;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.sgs.stashbot.model.AuthPollingTime;
import org.sgs.stashbot.model.BlacklistedUser;
import org.sgs.stashbot.model.RedditPollingTime;
import org.sgs.stashbot.model.StashResult;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.service.AuthTimeService;
import org.sgs.stashbot.service.RedditTimeService;
import org.sgs.stashbot.service.StashResultService;
import org.sgs.stashbot.service.UserService;
import org.sgs.stashbot.spring.SpringContext;
import org.sgs.stashbot.util.StashResultPostFormatter;


public class PersistenceServiceTest extends GeneratorTestBase {


    @Test
    public void testFormatter() {
        StashResult stashResult = generateDummyStashResult();
        String output = StashResultPostFormatter.format(stashResult);
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


    @Test
    public void testBlacklistedUser() {
        UserService service = SpringContext.getBean(UserService.class);
        BlacklistedUser user = generateBlacklistedUser();

        service.save(user);

        BlacklistedUser returnedUser = service.getBlackListedUserbyUsername(user.getUsername());
        Assert.assertTrue("Should return exactly one user!", returnedUser != null);

        String username = returnedUser.getUsername();
        Assert.assertTrue("Username should match what was saved!", returnedUser.getUsername().equals(username));

        service.delete(user);

        returnedUser = service.getBlackListedUserbyUsername(username);
        Assert.assertNull("Deleted user should not be returned in search!", returnedUser);
    }


    @Test
    public void testStashResultIsServiced() {
        StashResultService service = SpringContext.getBean(StashResultService.class);

        boolean exists = service.existsByTargetCommentId("SVdBrk2");// in dummy data
        Assert.assertTrue("StashResult should exist in dummy data!", exists);

        exists = service.existsByTargetCommentId("lksdjfl;asdjkf");// made up id, should fail
        Assert.assertFalse("Made up ID should not pull valid record!", exists);
    }


    @Test
    public void testSaveStashResult() {
        StashResult stashResult = generateDummyStashResult();
        StashResultService service = SpringContext.getBean(StashResultService.class);
        service.save(stashResult);

        StashResult returnedBo = service.findByTargetCommentId(stashResult.getTargetPostableId());
        Assert.assertNotNull("Should get back one result that we just inserted!", returnedBo);

        BigInteger id = returnedBo.getId();
        Assert.assertNotNull(id);
        for (StashUrl stashUrl : returnedBo.getStashUrls()) {
            Assert.assertNotNull(stashUrl);
            Assert.assertTrue(stashUrl.getId() != null); // set by hibernate save
        }

        service.delete(returnedBo);
        Assert.assertTrue(!service.existsByTargetCommentId(stashResult.getTargetPostableId()));
    }


    @Test
    public void testStashResultDao() {
        StashResultService service = SpringContext.getBean(StashResultService.class);
        StashResult stashResult = service.findByTargetCommentId("V1X0rS");// in dummy data file
        Assert.assertTrue(stashResult.getStashUrls().size() == 4);
    }


    @Test
    public void testFetchOfStashResult() {
        StashResultService stashResultService = SpringContext.getBean(StashResultService.class);
        StashResult stashResult = stashResultService.findById(new BigInteger("1"));

        Assert.assertNotNull(stashResult);
        Assert.assertNotNull(stashResult.getTargetPostableAuthor());
        Assert.assertNotNull(stashResult.getTargetPostableId());
        Assert.assertNotNull(stashResult.getTargetPostableUrl());
        Assert.assertNotNull(stashResult.getSubmissionUrl());
        Assert.assertNotNull(stashResult.getSummoningCommentAuthor());
        Assert.assertNotNull(stashResult.getSummoningCommentId());
        Assert.assertNotNull(stashResult.getSummoningCommentUrl());
        Assert.assertNotNull(stashResult.getId());

    }


}
