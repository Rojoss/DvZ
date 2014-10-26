package com.clashwars.dvz.classes;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class BaseClass {

    protected DvZ dvz;

    protected DvZClass dvzClass = null;
    protected Set<CWItem> equipment = new HashSet<CWItem>();
    //TODO: List of abilities.

    protected double weight = 0.0d;
    protected ChatColor color = ChatColor.WHITE;

    public BaseClass() {
        dvz = DvZ.inst();
        double w = -1;
        if (dvz.getClassesCfg().WEIGHTS.get(getName()) != null) {
            w = dvz.getClassesCfg().WEIGHTS.get(getName());
        }
        if (w > 0) {
            weight = w;
        }
    }


    public void equipItems(Player player) {
        for (CWItem item : equipment) {
            if (item.getType() == Material.LEATHER_HELMET || item.getType() == Material.CHAINMAIL_HELMET || item.getType() == Material.GOLD_HELMET
                    || item.getType() == Material.IRON_HELMET || item.getType() == Material.DIAMOND_HELMET) {
                player.getInventory().setHelmet(item);
            } else if (item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.CHAINMAIL_CHESTPLATE || item.getType() ==  Material.GOLD_CHESTPLATE
                    || item.getType() ==  Material.IRON_CHESTPLATE || item.getType() == Material.DIAMOND_CHESTPLATE) {
                player.getInventory().setChestplate(item);
            } else if (item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.CHAINMAIL_LEGGINGS || item.getType() == Material.GOLD_LEGGINGS
                    || item.getType() == Material.IRON_LEGGINGS || item.getType() == Material.DIAMOND_LEGGINGS) {
                player.getInventory().setLeggings(item);
            } else if (item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.CHAINMAIL_BOOTS || item.getType() == Material.GOLD_BOOTS
                    || item.getType() == Material.IRON_BOOTS || item.getType() == Material.DIAMOND_BOOTS) {
                player.getInventory().setBoots(item);
            } else {
                item.giveToPlayer(player);
            }
        }
    }


    public ChatColor getColor() {
        return color;
    }

    public Double getWeight() {
        return weight;
    }

    public Set<CWItem> getEquipment() {
        return equipment;
    }

    public DvZClass getDvZClass() {
        return dvzClass;
    }

    public ClassType getType() {
        return dvzClass.getType();
    }

    public String getName() {
        if (dvzClass != null) {
            return CWUtil.capitalize(dvzClass.toString().toLowerCase());
        }
        return "";
    }
}
