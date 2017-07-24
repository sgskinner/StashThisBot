package org.sgs.atbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.SessionFactory;
import org.sgs.atbot.ArchiveResultBo;

public interface ArchiveResultDao {
    SessionFactory getSessionFactory();

    void setSessionFactory(SessionFactory sessionFactory);

    void save(ArchiveResultBo archiveResultBo);

    void update(ArchiveResultBo archiveResultBo);

    void delete(ArchiveResultBo archiveResultBo);

    List<ArchiveResultBo> findByParentCommentId(String parentCommentId);

    boolean archiveResultExistsByParentCommentId(String parentCommentId);

    ArchiveResultBo findByResultId(BigInteger resultId);
}
