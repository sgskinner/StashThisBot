package org.sgs.stashbot.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.stashbot.dao.StashUrlDao;
import org.sgs.stashbot.model.StashUrl;
import org.sgs.stashbot.service.StashUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class StashUrlServiceImpl implements StashUrlService {


    final StashUrlDao dao;


    @Autowired
    public StashUrlServiceImpl(StashUrlDao dao) {
        this.dao = dao;
    }


    @Override
    public List<StashUrl> findAll() {
        return null;
    }


    @Override
    public StashUrl findById(BigInteger id) {
        return null;
    }


    @Override
    public void save(StashUrl stashUrl) {

    }


    @Override
    public void delete(StashUrl stashUrl) {

    }


    @Override
    public void update(StashUrl stashUrl) {

    }

}
