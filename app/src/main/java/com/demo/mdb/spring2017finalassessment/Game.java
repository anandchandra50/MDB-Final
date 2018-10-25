package com.demo.mdb.spring2017finalassessment;

/**
 * Created by hp on 3/14/2017.
 */

public class Game {
    public String phrase;
    public String typedString;
    public int accuracyPercentage;
    public int wpm;

    public Game() {}

    public Game(String phrase, String typedString, int accuracyPercentage, int wpm) {
        this.phrase = phrase;
        this.typedString = typedString;
        this.accuracyPercentage = accuracyPercentage;
        this.wpm = wpm;
    }
}
