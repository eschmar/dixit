package io.eschmann.dixitapp;

import java.util.ArrayList;

/** Define Node class
 *
 * Created by Simone on 18/09/16.
 */

public class Node {
    /**
     * The list of phrases in a specific node
     */
    private ArrayList<Phrase> phrases;

    /**
     * Constructor for a Node object
     */
    public Node() {
        this.phrases = new ArrayList<>();
    }

    /**
     * Return the list of phrases in a specific node
     * @return phrases
     */
    public ArrayList<Phrase> getPhrases() {
        return phrases;
    }

    /**
     * Add a phrase to the list of phrases in a specific node
     * @param phrase, the new phrase to add
     */
    public void addPhrase(Phrase phrase) {
        this.phrases.add(phrase);
    }
}
