package org.sgs.atbot.dao.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.dao.AbstractDao;
import org.sgs.atbot.dao.BlacklistedUserDao;
import org.sgs.atbot.model.BlacklistedUser;
import org.springframework.stereotype.Repository;


@Repository
public class BlacklistedUserDaoImpl extends AbstractDao<BigInteger, BlacklistedUser> implements BlacklistedUserDao {
    private static final String SELECT_BY_USERNAME = "SELECT u FROM BlacklistedUser u where username = :username";


    @Override
    public boolean isUserBlacklisted(String username) {
        return getBlackListedUserbyUsername(username) != null;
    }


    @Override
    public BlacklistedUser findById(BigInteger id) {
        return super.getByKey(id);
    }


    @Override
    public void save(BlacklistedUser user) {
        super.persist(user);
    }


    @Override
    public void delete(BlacklistedUser user) {
        user = getEntityManager().contains(user) ? user : getEntityManager().merge(user);
        super.delete(user);
    }


    @Override
    public void update(BlacklistedUser user) {
        super.update(user);
    }


    @SuppressWarnings("unchecked")//getResultList()
    @Override
    public BlacklistedUser getBlackListedUserbyUsername(String username) {
        List<BlacklistedUser> blacklistedUsers = getEntityManager()
                .createQuery(SELECT_BY_USERNAME)
                .setParameter("username", username)
                .getResultList();

        if (blacklistedUsers == null || blacklistedUsers.size() == 0) {
            return null;
        }

        return blacklistedUsers.get(0);
    }

}
