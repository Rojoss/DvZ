package com.clashwars.dvz.util;

import com.clashwars.cwcore.helpers.CWItem;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class DvzItem extends CWItem {

    private int priority = 0;
    private int slot = -1;
    private boolean give = true;

    public DvzItem(CWItem item) {
        super(item);
        init(priority, slot, true);
    }

    public DvzItem(CWItem item, int priority) {
        super(item);
        init(priority, slot, true);
    }

    public DvzItem(CWItem item, int priority, int slot) {
        super(item);
        init(priority, slot, true);
    }

    public DvzItem(CWItem item, int priority, int slot, boolean give) {
        super(item);
        init(priority, slot, give);
    }

    public DvzItem(Material mat, int priority, int slot) {
        super(mat);
        init(priority, slot, true);
    }

    public DvzItem(Material mat, int priority, int slot, boolean give) {
        super(mat);
        init(priority, slot, give);
    }

    public DvzItem(Material mat, int amt, int priority, int slot) {
        super(mat, amt);
        init(priority, slot, true);
    }

    public DvzItem(Material mat, int amt, int priority, int slot, boolean give) {
        super(mat, amt);
        init(priority, slot, give);
    }

    public DvzItem(Material mat, int amt, short durability, int priority, int slot) {
        super(mat, amt, durability);
        init(priority, slot, true);
    }

    public DvzItem(Material mat, int amt, short durability, int priority, int slot, boolean give) {
        super(mat, amt, durability);
        init(priority, slot, give);
    }

    public DvzItem(Material mat, int amt, short durability, String name, int priority, int slot) {
        super(mat, amt, durability, name);
        init(priority, slot, true);
    }

    public DvzItem(Material mat, int amt, short durability, String name, int priority, int slot, boolean give) {
        super(mat, amt, durability, name);
        init(priority, slot, give);
    }

    public DvzItem(Material mat, int amt, short durability, String name, String[] lore, int priority, int slot) {
        super(mat, amt, durability, name, lore);
        init(priority, slot, true);
    }

    public DvzItem(Material mat, int amt, short durability, String name, String[] lore, int priority, int slot, boolean give) {
        super(mat, amt, durability, name, lore);
        init(priority, slot, give);
    }

    public DvzItem(PotionType potion, boolean splash, int amt, int priority, int slot, boolean give) {
        super(potion, splash, amt);
        init(priority, slot, give);
    }


    private void init(int priority, int slot, boolean give) {
        this.priority = priority;
        if (slot >= 0) {
            this.slot = slot;
        }
        this.give = give;
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

    public boolean doGive() {
        return give;
    }

    public void setGive(boolean give) {
        this.give = give;
    }

    public DvzItem addPotionEffect(PotionEffectType effect, int amplifier, int duration) {
        if (getType() == Material.POTION) {
            PotionMeta pmeta = (PotionMeta)getItemMeta();
            pmeta.addCustomEffect(new PotionEffect(effect, duration, amplifier), true);
            setItemMeta(pmeta);
        }
        return this;
    }

}
