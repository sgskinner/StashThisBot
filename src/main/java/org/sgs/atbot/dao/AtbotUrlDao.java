package org.sgs.atbot.dao;

import java.math.BigInteger;
import java.util.List;

import org.sgs.atbot.model.AtbotUrl;

public interface AtbotUrlDao {
    List<AtbotUrl> findAll();

    AtbotUrl findById(BigInteger id);

    void save(AtbotUrl atbotUrl);

    void delete(AtbotUrl atbotUrl);

    void update(AtbotUrl atbotUrl);
}
