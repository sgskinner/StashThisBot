package org.sgs.atbot.model;

import net.dean.jraw.models.Comment;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.Thing;

public class Postable {

    private Thing thing;


    public Postable(Thing thing) {
        this.thing = thing;
    }


    public String getId() {
        return thing.getId();
    }


    public String getAuthor() {
        if (isComment()) {
            return ((Comment) thing).getAuthor();
        } else {
            return ((Submission)thing).getAuthor();
        }
    }


    public String getBody() {
        if (isComment()) {
            return ((Comment) thing).getBody();
        } else {
            return ((Submission)thing).getSelftext();
        }
    }


    public String getUrl() {
        if (isComment()) {
            return ((Comment) thing).getUrl();
        } else {
            return ((Submission)thing).getUrl();
        }
    }


    public Thing getThing() {
        return thing;
    }


    public boolean isSubmission() {
        return thing instanceof Submission;
    }


    public boolean isComment() {
        return thing instanceof Comment;
    }

}
