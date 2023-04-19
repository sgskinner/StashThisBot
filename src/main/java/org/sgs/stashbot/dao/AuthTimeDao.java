package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.AuthPollingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;


@Repository
public interface AuthTimeDao extends JpaRepository<AuthPollingTime, BigInteger> {
    AuthPollingTime getLastSuccessfulAuth();

//    private static final String SELECT_BY_LAST_SUCCESS = "select t from AuthPollingTime t where id = (select max(id) from AuthPollingTime where success = true)";
//
//    @Override
//    public AuthPollingTime getLastSuccessfulAuth() {
//        return (AuthPollingTime) getEntityManager()
//                .createQuery(SELECT_BY_LAST_SUCCESS)
//                .getSingleResult();
//    }
}
