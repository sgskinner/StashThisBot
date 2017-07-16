package org.sgs.atbot.url;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dean.jraw.models.CommentNode;

public class ArchiveResonse {
    private final CommentNode parentCommentNode;
    private final CommentNode summoningCommentNode;
    private final List<ArchivedUrl> urlsToArchive;
    private boolean isArchived;
    private Date requestDate;
    private Date archivedDate;


    public ArchiveResonse(CommentNode parentCommentNode, CommentNode summoningCommentNode, List<String> urlsToArchive) {
        this.parentCommentNode = parentCommentNode;
        this.summoningCommentNode = summoningCommentNode;
        this.isArchived = false;
        this.requestDate = Calendar.getInstance().getTime();

        this.urlsToArchive = new ArrayList<>();
        for (String url : urlsToArchive) {
            this.urlsToArchive.add(new ArchivedUrl(url));
        }
    }


    public CommentNode getParentCommentNode() {
        return parentCommentNode;
    }


    public CommentNode getSummoningCommentNode() {
        return summoningCommentNode;
    }


    public List<ArchivedUrl> getUrlsToArchive() {
        return urlsToArchive;
    }


    public Date getRequestDate() {
        return requestDate;
    }


    public boolean isArchived() {
        return isArchived;
    }


    public void setIsArchived(boolean archived) {
        isArchived = archived;
    }


    public Date getArchivedDate() {
        return archivedDate;
    }


    public void setArchivedDate(Date archivedDate) {
        this.archivedDate = archivedDate;
    }
}
