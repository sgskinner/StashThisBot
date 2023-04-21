package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.AuthPollingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuthTimeDao extends JpaRepository<AuthPollingTime, Long> {
    // Get most recent successfult auth time
    AuthPollingTime findFirstBySuccessIsTrueOrderByDateDesc();
}
