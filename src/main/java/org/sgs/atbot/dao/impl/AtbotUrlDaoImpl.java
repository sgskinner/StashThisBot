package org.sgs.atbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.dao.AbstractDao;
import org.sgs.atbot.dao.AtbotUrlDao;
import org.sgs.atbot.model.AtbotUrl;
import org.springframework.stereotype.Repository;


@Repository
public class AtbotUrlDaoImpl extends AbstractDao<BigInteger, AtbotUrl> implements AtbotUrlDao {
    private static final String SELECT_ALL_ATBOT_URL_QUERY = "SELECT a FROM AtbotUrl a";


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public List<AtbotUrl> findAll() {
        return getEntityManager()
                .createQuery(SELECT_ALL_ATBOT_URL_QUERY)
                .getResultList();
    }


    @Override
    public AtbotUrl findById(BigInteger id) {
        return getByKey(id);
    }


    @Override
    public void save(AtbotUrl atbotUrl) {
        persist(atbotUrl);
    }


    @Override
    public void delete(AtbotUrl atbotUrl) {
        super.delete(atbotUrl);
    }


    @Override
    public void update(AtbotUrl atbotUrl) {
        super.update(atbotUrl);
    }

}
