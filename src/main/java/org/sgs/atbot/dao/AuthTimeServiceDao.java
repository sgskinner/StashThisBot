package org.sgs.atbot.dao;

import java.math.BigInteger;

import org.sgs.atbot.model.AuthPollingTime;

public interface AuthTimeServiceDao {

    AuthPollingTime getLastSuccessfulAuth();

    AuthPollingTime findById(BigInteger id);

    void save(AuthPollingTime authPollingTime);

    void delete(AuthPollingTime authPollingTime);

    void update(AuthPollingTime authPollingTime);

}
