package org.sgs.stashbot.model;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;

public record Postable(Thing thing) {


    public String getId() {
        return thing.getId();
    }


    public String getAuthor() {
        if (isComment()) {
            return ((Comment) thing).getAuthor();
        } else {
            return ((Submission) thing).getAuthor();
        }
    }


    public String getBody() {
        if (isComment()) {
            return ((Comment) thing).getBody();
        } else {
            // We are a Submission, but we need to figure out if we're a self post,
            // or link submission, and then handle it
            Submission submission = (Submission) thing;
            if (submission.isSelfPost()) {
                return submission.getSelftext();
            } else {
                return submission.getUrl();
            }
        }
    }


    public String getUrl() {
        if (isComment()) {
            return ((Comment) thing).getUrl();
        } else {
            return ((Submission) thing).getUrl();
        }
    }


    public boolean isSubmission() {
        return thing instanceof Submission;
    }


    public boolean isComment() {
        return thing instanceof Comment;
    }

}
