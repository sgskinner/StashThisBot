package org.sgs.atbot;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.CharacterPredicates;
import org.apache.commons.text.RandomStringGenerator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.sgs.atbot.dao.impl.ArchiveResultBoDaoImpl;
import org.sgs.atbot.service.PersistenceService;
import org.sgs.atbot.spring.SpringContext;
import org.sgs.atbot.url.AtbotUrl;
import org.springframework.util.Assert;

public class PersistenceServiceTest {
    private static SessionFactory sessionFactory;
    private static Session session;
    private static Transaction transaction;
    private static RandomStringGenerator stringGenerator;


    @Test
    public void testSaveArchiveResultBo() {
        ArchiveResultBo archiveResultBo = generateDummyArchiveResultBo();
        PersistenceService persistenceService = SpringContext.getBean(PersistenceService.class);
        persistenceService.persistArchiveResultBo(archiveResultBo);
    }


    private ArchiveResultBo generateDummyArchiveResultBo() {

        ArchiveResultBo archiveResultBo = new ArchiveResultBo();
        archiveResultBo.setSubmissionUrl(generateMockUrl());
        archiveResultBo.setParentCommentAuthor("op1");
        archiveResultBo.setParentCommentId(stringGenerator.generate(6));
        archiveResultBo.setParentCommentUrl(generateMockUrl());
        archiveResultBo.setSummoningCommentAuthor("summoner1");
        archiveResultBo.setSummoningCommentId(stringGenerator.generate(6));
        archiveResultBo.setSummoningCommentUrl(generateMockUrl());
        archiveResultBo.setRequestDate(Calendar.getInstance().getTime());
        archiveResultBo.setServicedDate(Calendar.getInstance().getTime());
        archiveResultBo.setArchivedUrls(generateAtbotUrlList(3));

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
        sb.append(generateRandomString(14));
        sb.append(".com/");
        sb.append(generateRandomString(8));
        sb.append(".do");
        return sb.toString();
    }


    private String generateRandomString(int length) {
        return stringGenerator.generate(length);
    }


    @Test
    public void testArchiveResultDao() {
        ArchiveResultDao dao = SpringContext.getBean(ArchiveResultBoDaoImpl.class);
        List<ArchiveResultBo> results = dao.findByParenCommentId("dk9pnws");

        Assert.notNull(results);
        Assert.isTrue(results.size() == 1);

        ArchiveResultBo archiveResultBo = results.get(0);
        Assert.isTrue(archiveResultBo.getArchivedUrls().size() == 2);

    }


    @Test
    public void testFetchOfAtbotUrl() {
        CriteriaBuilder builder = getSession().getCriteriaBuilder();
        CriteriaQuery<AtbotUrl> query = builder.createQuery(AtbotUrl.class);

        Root<AtbotUrl> root = query.from(AtbotUrl.class);
        query.select(root);

        List<AtbotUrl> results = getSession().createQuery(query).list();

        Assert.notNull(results);
        Assert.isTrue(results.size() == 3);
    }


    @Test
    public void testFetchOfArchiveResult() {
        ArchiveResultBo result = getSession().load(ArchiveResultBo.class, new BigInteger("1"));

        Assert.notNull(result);
        Assert.notNull(result.getParentCommentAuthor());
        Assert.notNull(result.getParentCommentId());
        Assert.notNull(result.getParentCommentUrl());
        Assert.notNull(result.getSubmissionUrl());
        Assert.notNull(result.getSummoningCommentAuthor());
        Assert.notNull(result.getSummoningCommentId());
        Assert.notNull(result.getSummoningCommentUrl());
        Assert.notNull(result.getResultId());

    }


    private Session getSession() {
        return session;
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
            if (transaction.getRollbackOnly()) {
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
