package org.sgs.atbot.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;


public abstract class AbstractCustomDaoSupport extends HibernateDaoSupport {

    @Autowired
    public void anyMethodName(SessionFactory sessionFactory) {
        setSessionFactory(sessionFactory);
    }

    public Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

}
