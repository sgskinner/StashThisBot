package org.sgs.atbot.url;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class AtbotUrl {
    private final String originalUrl;
    private String archivedUrl;
    private Date lastArchived;


    public AtbotUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }


    public String getArchivedUrl() {
        return archivedUrl;
    }


    public void setArchivedUrl(String archivedUrl) {
        this.archivedUrl = archivedUrl;
    }


    public String getOriginalUrl() {
        return originalUrl;
    }


    public boolean isArchived() {
        return StringUtils.isNotBlank(getArchivedUrl()) && getLastArchived() != null;
    }

    
    public void setLastArchived(Date date) {
        this.lastArchived = date;
    }


    public Date getLastArchived() {
        return lastArchived;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AtbotUrl[");
        sb.append(System.lineSeparator());
        sb.append("    originalUrl: " + getOriginalUrl());
        sb.append(System.lineSeparator());
        sb.append("    archivedUrl: " + getArchivedUrl());
        sb.append(System.lineSeparator());
        sb.append("    lastArchived: " + getLastArchived());
        sb.append(System.lineSeparator());
        sb.append("    isArchived: " + isArchived() + "]");

        return sb.toString();
    }
}
