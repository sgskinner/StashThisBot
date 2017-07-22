package org.sgs.atbot.url;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.dean.jraw.models.CommentNode;


public class ArchiveResult {

    private final CommentNode parentCommentNode;
    private final CommentNode summoningCommentNode;
    private final List<AtbotUrl> urlsToArchive;
    private Date requestDate;
    private Date archivedDate;


    public ArchiveResult(CommentNode parentCommentNode, CommentNode summoningCommentNode, List<String> urlsToArchive) {
        this.parentCommentNode = parentCommentNode;
        this.summoningCommentNode = summoningCommentNode;
        this.requestDate = Calendar.getInstance().getTime();

        this.urlsToArchive = new ArrayList<>();
        for (String url : urlsToArchive) {
            this.urlsToArchive.add(new AtbotUrl(url));
        }
    }


    public CommentNode getParentCommentNode() {
        return parentCommentNode;
    }


    public CommentNode getSummoningCommentNode() {
        return summoningCommentNode;
    }


    public List<AtbotUrl> getUrlsToArchive() {
        return urlsToArchive;
    }


    public Date getRequestDate() {
        return requestDate;
    }


    public boolean isArchived() {
        return getArchivedDate() != null;
    }


    public Date getArchivedDate() {
        return archivedDate;
    }


    public void setArchivedDate(Date archivedDate) {
        this.archivedDate = archivedDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArchiveResult: ");
        sb.append(System.lineSeparator());
        sb.append("    parentCommentId: " + (getParentCommentNode() == null ? null : (getParentCommentNode().getComment() == null ? null : getParentCommentNode().getComment().getId())));
        sb.append(System.lineSeparator());
        sb.append("    summoningCommentId: " + (getSummoningCommentNode() == null ? null : (getSummoningCommentNode().getComment() == null ? null : getSummoningCommentNode().getComment().getId())));
        sb.append(System.lineSeparator());
        for (AtbotUrl atbotUrl : urlsToArchive) {
            sb.append("    originalUrl: " + atbotUrl.getOriginalUrl());
            sb.append("    archivedUrl: " + atbotUrl.getArchivedUrl());
            sb.append(System.lineSeparator());
        }
        sb.append("requestDate: " + getRequestDate());
        sb.append("archivedDate: " + getArchivedDate());
        sb.append("isArchived: " + isArchived());

        return sb.toString();
    }

}
