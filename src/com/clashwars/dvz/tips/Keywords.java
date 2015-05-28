package com.clashwars.dvz.tips;

import com.clashwars.cwcore.Debug;

import java.util.ArrayList;
import java.util.List;

public class Keywords {

    List<Keyword> keywords = new ArrayList<Keyword>();

    public Keywords(String... strings) {
        for (String str : strings) {
            keywords.add(new Keyword(str));
        }
    }

    public List<Keyword> getKeywords() {
        return keywords;
    }
}
