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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.service.PersistenceService;
import org.sgs.atbot.spring.SpringContext;
import org.sgs.atbot.url.AtbotUrl;
import org.springframework.util.Assert;

public class PersistenceServiceTest {
    private static final String[] TEST_SUMMONER_USERNAMES = {"test-summoner-0", "test-summoner-1", "test-summoner-2", "test-summoner-3", "test-summoner-4"};
    private static final String[] TEST_PARENT_USERNAMES = {"test-parent-0", "test-parent-1", "test-parent-2", "test-parent-3", "test-parent-4"};


    private static SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;
    private static RandomStringGenerator stringGenerator;


    @Test
    public void testArchiveResultIsServiced() {
        PersistenceService persistenceService = SpringContext.getBean(PersistenceService.class);

        boolean exists = persistenceService.isAlreadyServiced("SVdBrk2");
        Assert.isTrue(exists);

        exists = persistenceService.isAlreadyServiced("lksdjfl;asdjkf");
        Assert.isTrue(!exists);
    }


    @Test
    public void testSaveArchiveResultBo() {
        ArchiveResultBo archiveResultBo = generateDummyArchiveResultBo();
        PersistenceService persistenceService = SpringContext.getBean(PersistenceService.class);
        persistenceService.persistArchiveResultBo(archiveResultBo);

        List<ArchiveResultBo> returnedBos = persistenceService.findByParenCommentId(archiveResultBo.getParentCommentId());
        Assert.notNull(returnedBos);
        Assert.isTrue(returnedBos.size() == 1);

        ArchiveResultBo returnedBo = returnedBos.get(0);
        Assert.notNull(returnedBo);

        BigInteger id = returnedBo.getResultId();
        Assert.notNull(id);
        for (AtbotUrl atbotUrl : returnedBo.getArchivedUrls()) {
            Assert.notNull(atbotUrl);
            Assert.isTrue(atbotUrl.getUrlId() != null); // set by hibernate save
        }

        persistenceService.deleteArchiveResultBo(archiveResultBo);
        Assert.isTrue(!persistenceService.archiveResultExistsByParentCommentId(archiveResultBo.getParentCommentId()));
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
        archiveResultBo.setArchivedUrls(generateAtbotUrlList(getRandomInt(1, 5)));

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
        PersistenceService persistenceService = SpringContext.getBean(PersistenceService.class);
        List<ArchiveResultBo> results = persistenceService.findByParentCommentId("V1X0rS");// in dummy data file

        Assert.notNull(results);
        Assert.isTrue(results.size() == 1);

        ArchiveResultBo archiveResultBo = results.get(0);
        Assert.isTrue(archiveResultBo.getArchivedUrls().size() == 4);

    }


    @Test
    public void testFetchOfArchiveResult() {
        ArchiveResultDao archiveResultDao = SpringContext.getBean(ArchiveResultDao.class);
        ArchiveResultBo archiveResultBo = archiveResultDao.findByResultId(new BigInteger("1"));

        Assert.notNull(archiveResultBo);
        Assert.notNull(archiveResultBo.getParentCommentAuthor());
        Assert.notNull(archiveResultBo.getParentCommentId());
        Assert.notNull(archiveResultBo.getParentCommentUrl());
        Assert.notNull(archiveResultBo.getSubmissionUrl());
        Assert.notNull(archiveResultBo.getSummoningCommentAuthor());
        Assert.notNull(archiveResultBo.getSummoningCommentId());
        Assert.notNull(archiveResultBo.getSummoningCommentUrl());
        Assert.notNull(archiveResultBo.getResultId());

    }


    @BeforeClass
    public static void testInit() {
        SecureRandom rand = new SecureRandom();
        stringGenerator = new RandomStringGenerator.Builder().usingRandom(rand::nextInt).withinRange(0, 'z').filteredBy(new AlphaNumericPredicate()).build();


        sessionFactory = SpringContext.getBeanById("sessionFactory");
        try {
            session = sessionFactory.getCurrentSession();
        } catch (HibernateException e) {
            session = sessionFactory.openSession();
        }

        transaction = session.beginTransaction();
    }


    @AfterClass
    public static void testTearDown() {
        if (transaction != null) {
            if (transaction.getStatus().isOneOf(TransactionStatus.MARKED_ROLLBACK)) {
                transaction.rollback();
            } else {
                transaction.commit();
            }
        }
        if (session != null) {
            session.close();
        }
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }


    static class AlphaNumericPredicate implements CharacterPredicate {

        @Override
        public boolean test(int codePoint) {
            return CharacterPredicates.DIGITS.test(codePoint) ||
                    CharacterPredicates.LETTERS.test(codePoint);

        }
    }

}
