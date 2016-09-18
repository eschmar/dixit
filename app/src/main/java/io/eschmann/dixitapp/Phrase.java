package io.eschmann.dixitapp;

/**
 * Define a Phrase class
 *
 * Created by Simone on 17/09/16.
 */

public class Phrase {
    private String text;
    private String translation;
    private Integer type;

    public Phrase() {
    }

    public Phrase(String text) {
        this.text = text;
        this.type = 0;
    }

    @Override
    public String toString() {
        return getText();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
