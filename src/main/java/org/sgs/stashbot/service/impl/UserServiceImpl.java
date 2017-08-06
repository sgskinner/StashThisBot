package org.sgs.stashbot.service.impl;

import java.math.BigInteger;

import org.sgs.stashbot.dao.BlacklistedUserDao;
import org.sgs.stashbot.model.BlacklistedUser;
import org.sgs.stashbot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    final BlacklistedUserDao dao;


    @Autowired
    public UserServiceImpl(BlacklistedUserDao dao) {
        this.dao = dao;
    }


    @Override
    public boolean isUserBlacklisted(String username) {
        return dao.isUserBlacklisted(username);
    }


    @Override
    public BlacklistedUser findById(BigInteger id) {
        return dao.findById(id);
    }


    @Override
    public void save(BlacklistedUser user) {
        dao.save(user);
    }


    @Override
    public void delete(BlacklistedUser user) {
        dao.delete(user);
    }


    @Override
    public void update(BlacklistedUser user) {
        dao.update(user);
    }


    @Override
    public BlacklistedUser getBlackListedUserbyUsername(String username) {
        return dao.getBlackListedUserbyUsername(username);
    }

}
