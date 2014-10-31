package com.clashwars.dvz.util;

import com.clashwars.cwcore.helpers.CWItem;
import org.bukkit.Material;

public class DvzItem extends CWItem {

    private int priority = 0;
    private int slot = -1;

    public DvzItem(Material mat, int priority, int slot) {
        super(mat);
        self(priority, slot);
    }

    public DvzItem(Material mat, int amt, int priority, int slot) {
        super(mat, amt);
        self(priority, slot);
    }

    public DvzItem(Material mat, int amt, short durability, int priority, int slot) {
        super(mat, amt, durability);
        self(priority, slot);
    }

    public DvzItem(Material mat, int amt, short durability, String name, int priority, int slot) {
        super(mat, amt, durability, name);
        self(priority, slot);
    }

    public DvzItem(Material mat, int amt, short durability, String name, String[] lore, int priority, int slot) {
        super(mat, amt, durability, name, lore);
        self(priority, slot);
    }

    private void self(int priority, int slot) {
        this.priority = priority;
        if (slot >= 0) {
            this.slot = slot;
        }
    }


    public int getPriority() {
        return priority;
    }

    public DvzItem setPriority(int priority) {
        this.priority = priority;
        return this;
    }


    public boolean hasSlot() {
        return slot >= 0;
    }

    public int getSlot() {
        return slot;
    }

    public DvzItem setSlot(int slot) {
        this.slot = slot;
        return this;
    }


}
