package com.clashwars.dvz.structures.extra;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomEnchant {

    private Enchantment enchant;
    private String name;
    private String[] description;
    private int level;
    private int xpNeeded;
    private List<Material> items = new ArrayList<Material>();

    public CustomEnchant(Enchantment enchant, String name, String[] description, int level, int xpNeeded, Material[] items) {
        this.enchant = enchant;
        this.name = name;
        this.description = description;
        this.level = level;
        this.xpNeeded = xpNeeded;
        this.items = Arrays.asList(items);
    }


    public Enchantment getEnchant() {
        return enchant;
    }

    public String getName() {
        return name;
    }

    public String[] getDesc() {
        return description;
    }

    public int getLevel() {
        return level;
    }

    public int getXpNeeded() {
        return xpNeeded;
    }

    public List<Material> getItems() {
        return items;
    }
}
