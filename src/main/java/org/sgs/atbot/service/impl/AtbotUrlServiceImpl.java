package org.sgs.atbot.service.impl;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.dao.AtbotUrlDao;
import org.sgs.atbot.model.AtbotUrl;
import org.sgs.atbot.service.AtbotUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service("atbotUrlService")
@Transactional
public class AtbotUrlServiceImpl implements AtbotUrlService {


    final AtbotUrlDao dao;


    @Autowired
    public AtbotUrlServiceImpl(AtbotUrlDao dao) {
        this.dao = dao;
    }


    @Override
    public List<AtbotUrl> findAll() {
        return null;
    }


    @Override
    public AtbotUrl findById(BigInteger id) {
        return null;
    }


    @Override
    public void save(AtbotUrl atbotUrl) {

    }


    @Override
    public void delete(AtbotUrl atbotUrl) {

    }


    @Override
    public void update(AtbotUrl atbotUrl) {

    }
}
