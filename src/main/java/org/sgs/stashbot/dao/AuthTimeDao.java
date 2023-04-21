package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.AuthPollingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface AuthTimeDao extends JpaRepository<AuthPollingTime, BigInteger> {
    // Get most recent successfult auth time
    AuthPollingTime findFirstBySuccessIsTrueOrderByDateDesc();
}
