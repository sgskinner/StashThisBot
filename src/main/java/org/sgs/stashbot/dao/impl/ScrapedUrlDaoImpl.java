package org.sgs.stashbot.dao.impl;

import java.math.BigInteger;

import org.sgs.stashbot.dao.AbstractDao;
import org.sgs.stashbot.dao.ScrapedUrlDao;
import org.sgs.stashbot.model.ScrapedUrl;
import org.springframework.stereotype.Repository;


@Repository
public class ScrapedUrlDaoImpl extends AbstractDao<BigInteger, ScrapedUrl> implements ScrapedUrlDao {
    private static final String SELECT_BY_LOWEST_ID = "SELECT s FROM ScrapedUrl s WHERE s.id = (SELECT MIN(s2.id) FROM ScrapedUrl s2)";


    @Override
    public ScrapedUrl getNextUrl() {
        ScrapedUrl scrapedUrl = (ScrapedUrl) getEntityManager()
                                                .createQuery(SELECT_BY_LOWEST_ID)
                                                .getSingleResult();
        delete(scrapedUrl);

        return scrapedUrl;
    }


    @Override
    public ScrapedUrl findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(ScrapedUrl scrapedUrl) {
        persist(scrapedUrl);
    }


    @Override
    public void delete(ScrapedUrl scrapedUrl) {
        scrapedUrl = getEntityManager().contains(scrapedUrl) ? scrapedUrl : getEntityManager().merge(scrapedUrl);

        super.delete(scrapedUrl);
    }


    @Override
    public void update(ScrapedUrl scrapedUrl) {
        super.update(scrapedUrl);
    }

}
