package com.clashwars.dvz.tips;

public class Keyword {

    String[] words;

    public Keyword(String words) {
        this.words = words.toLowerCase().split("\\|");
        for (String word : this.words) {
        }
    }

    public String[] getWords() {
        return words;
    }
}
