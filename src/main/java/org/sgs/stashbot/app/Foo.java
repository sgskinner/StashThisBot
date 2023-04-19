package org.sgs.stashbot.app;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Foo extends JpaRepository<String, Bar> {
}
