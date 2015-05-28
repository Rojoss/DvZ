package com.clashwars.dvz.tips;

import com.clashwars.dvz.classes.DvzClass;

import java.util.List;

public class Tip {

    private String msg;
    private DvzClass dvzClass;
    private Keywords keywords;

    public Tip(String msg, DvzClass dvzClass, String[] strings) {
        this.msg = msg;
        this.dvzClass = dvzClass;
        this.keywords = new Keywords(strings);
    }

    public String getTip() {
        return msg;
    }

    public DvzClass getDvzClass() {
        return dvzClass;
    }

    public List<Keyword> getKeywords() {
        return keywords.getKeywords();
    }

}
