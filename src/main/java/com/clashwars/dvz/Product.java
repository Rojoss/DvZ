package com.clashwars.dvz;

import com.clashwars.cwcore.helpers.CWItem;
import org.bukkit.Material;

public enum Product {

    //TODO: Set amounts, data, names, lore etc..

    CRACKED_STONE(new CWItem(Material.SMOOTH_BRICK)),

    DIAMOND_ORE(new CWItem(Material.DIAMOND_ORE)),
    GOLD_ORE(new CWItem(Material.GOLD_ORE)),
    IRON_ORE(new CWItem(Material.IRON_ORE)),
    DIAMOND(new CWItem(Material.DIAMOND)),
    GOLD_INGOT(new CWItem(Material.GOLD_INGOT)),
    IRON_INGOT(new CWItem(Material.IRON_INGOT)),
    DIAMOND_SWORD(new CWItem(Material.DIAMOND_SWORD)),
    IRON_SWORD(new CWItem(Material.IRON_SWORD)),
    GOLD_SWORD(new CWItem(Material.GOLD_SWORD)),
    STONE(new CWItem(Material.STONE)),
    STONE_BRICK(new CWItem(Material.SMOOTH_BRICK)),

    FLINT(new CWItem(Material.FLINT)),
    FEATHER(new CWItem(Material.FEATHER)),
    RAW_PORK(new CWItem(Material.PORK)),
    COOKED_PORK(new CWItem(Material.GRILLED_PORK)),
    BOW(new CWItem(Material.BOW)),
    ARROW(new CWItem(Material.ARROW)),

    WOOL(new CWItem(Material.WOOL)),
    BONE(new CWItem(Material.BONE)),
    BONEMEAL(new CWItem(Material.INK_SACK)),
    ROSE(new CWItem(Material.RED_ROSE)),
    FLOWER(new CWItem(Material.YELLOW_FLOWER)),
    HELMET(new CWItem(Material.LEATHER_HELMET)),
    CHESTPLATE(new CWItem(Material.LEATHER_CHESTPLATE)),
    LEGGINGS(new CWItem(Material.LEATHER_LEGGINGS)),
    BOOTS(new CWItem(Material.LEATHER_BOOTS)),

    MELON(new CWItem(Material.MELON)),
    SUGAR(new CWItem(Material.SUGAR)),
    HEAL_POTION(new CWItem(Material.POTION)),
    SPEED_POTION(new CWItem(Material.POTION));


    private CWItem item;

    Product(CWItem item) {
        this.item = item;
    }

    public CWItem getItem() {
        return item.clone();
    }
}
