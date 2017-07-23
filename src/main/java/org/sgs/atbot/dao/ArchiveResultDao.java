package org.sgs.atbot.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.sgs.atbot.ArchiveResultBo;

public interface ArchiveResultDao {
    SessionFactory getSessionFactory();

    void setSessionFactory(SessionFactory sessionFactory);

    void save(ArchiveResultBo archiveResultBo);

    void update(ArchiveResultBo archiveResultBo);

    void delete(ArchiveResultBo archiveResultBo);

    List<ArchiveResultBo> findByParenCommentId(String parentCommentId);
}
