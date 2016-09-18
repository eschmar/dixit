package io.eschmann.dixitapp;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

/** Define Node class.
 *
 * Created by Simone on 18/09/16.
 */

public class Node {
    private ArrayList <Phrase> phrases;

    public Node() {
        this.phrases = new ArrayList<Phrase>();
    }

    public ArrayList<Phrase> getPhrases() {
        return phrases;
    }

    public void addPhrase(Phrase phrase) {
        this.phrases.add(phrase);
    }
}
