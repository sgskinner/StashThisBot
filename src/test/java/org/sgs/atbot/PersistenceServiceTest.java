package org.sgs.atbot;

import java.math.BigInteger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Test;
import org.springframework.util.Assert;

public class PersistenceServiceTest {

    @Test
    public void testFetchOfArchiveResult() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

        Session session = sessionFactory.openSession();
        session.beginTransaction();

        ArchiveResultBo result = session.load(ArchiveResultBo.class, new BigInteger("1"));

        Assert.notNull(result);
        Assert.notNull(result.getParentCommentAuthor());
        Assert.notNull(result.getParentCommentId());
        Assert.notNull(result.getParentCommentUrl());
        Assert.notNull(result.getSubmissionUrl());
        Assert.notNull(result.getSummoningCommentAuthor());
        Assert.notNull(result.getSummoningCommentId());
        Assert.notNull(result.getSummoningCommentUrl());
        Assert.notNull(result.getResultId());

        session.close();
        sessionFactory.close();
    }

}
