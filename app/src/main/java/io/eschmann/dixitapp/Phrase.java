package io.eschmann.dixitapp;

/**
 * Define a Phrase class
 *
 * Created by Simone on 17/09/16.
 */

public class Phrase {
    String text;
    String userId;

    public Phrase() {
    }

    public Phrase(String text, String userId) {
        this.text = text;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public String getUserId() {
        return userId;
    }


}
