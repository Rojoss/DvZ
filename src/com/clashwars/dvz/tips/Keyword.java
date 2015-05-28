package com.clashwars.dvz.tips;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;

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
