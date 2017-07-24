package org.sgs.atbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.sgs.atbot.ArchiveResultBo;
import org.sgs.atbot.dao.ArchiveResultDao;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;


public class ArchiveResultBoDaoImpl extends HibernateDaoSupport implements ArchiveResultDao {
    private static final String PARENT_COMMENT_ID_KEY = "parentCommentId";


    public ArchiveResultBoDaoImpl(SessionFactory sessionfactory) {
        setSessionFactory(sessionfactory);
    }


    @Override
    public void save(ArchiveResultBo archiveResultBo) {
        getHibernateTemplate().save(archiveResultBo);
    }


    @Override
    public void update(ArchiveResultBo archiveResultBo) {
        getHibernateTemplate().update(archiveResultBo);
    }


    @Override
    public void delete(ArchiveResultBo archiveResultBo) {
        getHibernateTemplate().delete(archiveResultBo);
    }


    @SuppressWarnings("unchecked")//findByCriteria()
    @Override
    public List<ArchiveResultBo> findByParentCommentId(String parentCommentId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(ArchiveResultBo.class).add(Restrictions.eq(PARENT_COMMENT_ID_KEY, parentCommentId));
        return (List<ArchiveResultBo>) getHibernateTemplate().findByCriteria(criteria);
    }


    @Override
    public boolean archiveResultExistsByParentCommentId(String parentCommentId) {
        List<ArchiveResultBo> archiveResultBos = findByParentCommentId(parentCommentId);
        return archiveResultBos != null && archiveResultBos.size() > 0;
    }


    @Override
    public ArchiveResultBo findByResultId(BigInteger resultId) {
        return getHibernateTemplate().get(ArchiveResultBo.class, resultId);
    }

}
