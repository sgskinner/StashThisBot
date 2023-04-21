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
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.util.PostFormatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
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
    @Autowired
    private PostFormatterService postFormatterService;


    @Test
    public void testFormatter() {
        StashResult stashResult = generateStashResult();
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
        assertThat(returnedTime).isNotNull();
        assertThat(returnedTime.isSuccess()).isTrue();
        assertThat(returnedTime.getId()).isEqualTo(time2.getId());
        assertThat(returnedTime.getDate().compareTo(time2.getDate()) == 0).isTrue();

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
        BlacklistedUser generatedUser = generateBlacklistedUser();
        userDao.save(generatedUser);

        BlacklistedUser returnedUser = userDao.findBlacklistedUserByUsername(generatedUser.getUsername());
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser.getUsername()).isEqualTo(generatedUser.getUsername());

        userDao.delete(generatedUser);
        returnedUser = userDao.findBlacklistedUserByUsername(generatedUser.getUsername());
        assertThat(returnedUser).isNull();
    }


    @Test
    public void testSaveStashResult() {
        StashResult generatedStashResult = generateStashResult();
        stashResultDao.save(generatedStashResult);

        StashResult returnedStashResult = stashResultDao.findByTargetId(generatedStashResult.getTargetId());
        assertThat(returnedStashResult).isNotNull();
        assertThat(returnedStashResult.getId()).isNotNull();

        assertThat(returnedStashResult.getStashUrls()).size().isGreaterThan(0);
        for (StashUrl stashUrl : returnedStashResult.getStashUrls()) {
            assertThat(stashUrl).isNotNull();
            assertThat(stashUrl.getId()).isNotNull(); // set by hibernate save
        }

        stashResultDao.delete(returnedStashResult);
        String targetId = generatedStashResult.getTargetId();
        assertThat(stashResultDao.existsByTargetId(targetId)).isFalse();
    }
}
