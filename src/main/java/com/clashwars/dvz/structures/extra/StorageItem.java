package com.clashwars.dvz.structures.extra;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;

public class StorageItem {

    private String name;
    private int slot;
    private CWItem item;
    private DvzClass classType;
    private int limit;
    private int amt = 0;

    public StorageItem(String name, int slot, CWItem item, DvzClass classType, int limit) {
        this.name = name;
        this.slot = slot;
        this.item = item;
        this.classType = classType;
        this.limit = limit;
    }


    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public CWItem getItem() {
        return item;
    }

    public DvzClass getClassType() {
        return classType;
    }

    public int getLimit() {
        return limit;
    }

    public int getAmt() {
        return amt;
    }

    public void setAmt(int amt) {
        this.amt = amt;
    }

    public void changeAmt(int amt) {
        this.amt += amt;
    }
}
