package com.clashwars.dvz.structures.extra;

import com.clashwars.cwcore.helpers.CWItem;

public class FurnaceItem {

    private CWItem original;
    private CWItem result;
    private int cookDuration;
    private int xp;

    public FurnaceItem(CWItem original, CWItem result, int cookDuration, int xp) {
        this.original = original;
        this.result = result;
        this.cookDuration = cookDuration;
        this.xp = xp;
    }


    public CWItem getOriginal() {
        return original;
    }

    public CWItem getResult() {
        return result;
    }

    public int getCookDuration() {
        return cookDuration;
    }

    public int getXP() {
        return xp;
    }

}
