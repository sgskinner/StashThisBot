package org.sgs.stashbot.dao;

import org.sgs.stashbot.model.ScrapedUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScrapedUrlDao extends JpaRepository<ScrapedUrl, Long> {
    ScrapedUrl deleteScrapedUrlsById(Long id);
    Long getTopIdByOrderById();
}
