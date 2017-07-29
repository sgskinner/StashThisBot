package org.sgs.atbot.service;

import java.math.BigInteger;

import org.sgs.atbot.model.AuthPollingTime;

public interface AuthTimeService {

    AuthPollingTime getLastSuccessfulAuth();

    AuthPollingTime findById(BigInteger id);

    void save(AuthPollingTime authPollingTime);

    void delete(AuthPollingTime authPollingTime);

    void update(AuthPollingTime authPollingTime);

}
