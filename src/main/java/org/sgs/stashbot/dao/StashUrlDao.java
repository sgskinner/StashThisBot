package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.StashUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StashUrlDao extends JpaRepository<StashUrl, Long> {
}
