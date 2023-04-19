package org.sgs.stashbot.app;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Bar {

    @Id
    private String id;
    private String baz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaz() {
        return baz;
    }

    public void setBaz(String baz) {
        this.baz = baz;
    }
}
