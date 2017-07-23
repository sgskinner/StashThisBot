package org.sgs.atbot;

import java.math.BigInteger;
import java.util.List;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sgs.atbot.url.AtbotUrl;
import org.springframework.util.Assert;

public class PersistenceServiceTest {
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

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


    @Before
    public void testInit() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }


    @After
    public void testTearDown() {
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


}
