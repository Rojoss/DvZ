package com.clashwars.dvz;

import com.clashwars.cwcore.helpers.CWItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public enum Product {

    //TODO: Set amounts, data, names, lore etc..

    CRACKED_STONE(new CWItem(Material.SMOOTH_BRICK), false),

    DIAMOND_ORE(new CWItem(Material.DIAMOND_ORE), false),
    GOLD_ORE(new CWItem(Material.GOLD_ORE), false),
    IRON_ORE(new CWItem(Material.IRON_ORE), false),
    DIAMOND(new CWItem(Material.DIAMOND), false),
    GOLD_INGOT(new CWItem(Material.GOLD_INGOT), false),
    IRON_INGOT(new CWItem(Material.IRON_INGOT), false),
    DIAMOND_SWORD(new CWItem(Material.DIAMOND_SWORD), true),
    IRON_SWORD(new CWItem(Material.IRON_SWORD), true),
    GOLD_SWORD(new CWItem(Material.GOLD_SWORD), true),
    STONE(new CWItem(Material.STONE), false),
    STONE_BRICK(new CWItem(Material.SMOOTH_BRICK), false),

    FLINT(new CWItem(Material.FLINT), false),
    FEATHER(new CWItem(Material.FEATHER), false),
    RAW_PORK(new CWItem(Material.PORK), true),
    COOKED_PORK(new CWItem(Material.GRILLED_PORK), true),
    BOW(new CWItem(Material.BOW), true),
    ARROW(new CWItem(Material.ARROW), true),

    WOOL(new CWItem(Material.WOOL), false),
    DYE_1(new CWItem(Material.INK_SACK, 1, (byte)7), false),
    DYE_2(new CWItem(Material.INK_SACK, 1, (byte)12), false),
    HELMET(new CWItem(Material.LEATHER_HELMET), true),
    CHESTPLATE(new CWItem(Material.LEATHER_CHESTPLATE), true),
    LEGGINGS(new CWItem(Material.LEATHER_LEGGINGS), true),
    BOOTS(new CWItem(Material.LEATHER_BOOTS), true),

    MELON(new CWItem(Material.MELON), false),
    SUGAR(new CWItem(Material.SUGAR), false),
    HEAL_POTION(new CWItem(Material.POTION, 1, (byte)8197), true),
    SPEED_POTION(new CWItem(Material.POTION, 1, (byte)8194), true),

    WHEAT(new CWItem(Material.WHEAT), false),
    FLOUR(new CWItem(Material.SUGAR, 1, (byte)0, "&eFlour"), false),
    BREAD(new CWItem(Material.BREAD), true),
    SEED(new CWItem(Material.SEEDS), false);


    private CWItem item;
    private boolean canKeep;

    Product(CWItem item, boolean canKeep) {
        this.item = item;
        this.canKeep = canKeep;
    }

    public CWItem getItem() {
        return item.clone();
    }

    public boolean canKeep() {
        return canKeep;
    }

    public static boolean canKeep(Material mat) {
        ItemStack productItem;
        for (Product product : values()) {
            productItem = product.getItem();
            if (productItem.getType() == mat) {
                if (product.canKeep()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public CWItem getItem(int amount) {
        CWItem i = item.clone();
        i.setAmount(amount);
        return i;
    }
}
