package org.sgs.atbot.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.sgs.atbot.ArchiveResultBo;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public class ArchiveResultBoDaoImpl implements ArchiveResultDao {

    private SessionFactory sessionFactory;


    @Transactional
    @Override
    public void save(ArchiveResultBo archiveResultBo) {
        getSessionFactory().getCurrentSession().save(archiveResultBo);
    }


    @Transactional
    @Override
    public void update(ArchiveResultBo archiveResultBo) {
        getSessionFactory().getCurrentSession().update(archiveResultBo);
    }


    @Transactional
    @Override
    public void delete(ArchiveResultBo archiveResultBo) {
        getSessionFactory().getCurrentSession().delete(archiveResultBo);
    }


    @SuppressWarnings("unchecked")//findByCriteria()
    @Transactional
    @Override
    public List<ArchiveResultBo> findByParenCommentId(String parentCommentId) {
        CriteriaBuilder criteriaBuilder = getSession().getCriteriaBuilder();
        CriteriaQuery<ArchiveResultBo> query = criteriaBuilder.createQuery(ArchiveResultBo.class);
        Root<ArchiveResultBo> root = query.from(ArchiveResultBo.class);
        Predicate predicate = criteriaBuilder.equal(root.get("parentCommentId"), parentCommentId);
        query.where(predicate);

        return getSession().createQuery(query).list();
    }


    private Session getSession() {
        Session session;
        try {
            session = getSessionFactory().getCurrentSession();
        } catch(HibernateException e) {
            session = getSessionFactory().openSession();
        }

        return session;
    }


    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }


    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
