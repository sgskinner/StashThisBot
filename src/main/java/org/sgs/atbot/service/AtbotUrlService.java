package org.sgs.atbot.service;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.model.AtbotUrl;

public interface AtbotUrlService {
    List<AtbotUrl> findAll();

    AtbotUrl findById(BigInteger id);

    void save(AtbotUrl atbotUrl);

    void delete(AtbotUrl atbotUrl);

    void update(AtbotUrl atbotUrl);
}
