package com.clashwars.dvz.classes;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class BaseClass {
    protected DvZ dvz = DvZ.inst();
    protected DvzClass dvzClass = null;

    protected Set<CWItem> equipment = new HashSet<CWItem>();
    protected Set<Ability> abilities = new HashSet<Ability>();
    protected CWItem classItem = null;

    protected String disguise = "";
    protected String displayName = "&7Unknown class";
    protected String description = "";
    protected String produce = "";
    protected String task = "";

    protected double weight = 0.0d;
    protected int health = 20;
    protected ChatColor color = ChatColor.WHITE;

    public BaseClass() {
        //--
    }


    public String getStrOption(String option) {
        return dvz.getClassesCfg().getOption(dvzClass, option);
    }

    public boolean getBoolOption(String option) {
        return Boolean.valueOf(getStrOption(option));
    }

    public int getIntOption(String option) {
        return Integer.valueOf(getStrOption(option));
    }

    public float getFloatOption(String option) {
        return Float.valueOf(getStrOption(option));
    }

    public double getDoubleOption(String option) {
        return Double.valueOf(getStrOption(option));
    }



    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getDisguise() {
        return disguise;
    }

    public void setDisguise(String disguise) {
        this.disguise = disguise;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        if (description == null || description.isEmpty()) {
            return "&a&lDESC&8&l: &cNo description available.";
        } else {
            return "&a&lDESC&8&l: " + description;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesc() {
        if (description == null || description.isEmpty()) {
            return "&a&lPRODUCES&8&l: &cNo info available.";
        } else {
            return "&a&lPRODUCES&8&l: " + description;
        }
    }

    public void setProduce(String produce) {
        this.produce = produce;
    }

    public String getTask() {
        if (description == null || description.isEmpty()) {
            return "&a&lTASK&8&l: &cNo info available.";
        } else {
            return "&a&lTASK&8&l: " + description;
        }
    }

    public void setTask(String task) {
        this.task = task;
    }

    public CWItem getClassItem() {
        if (classItem != null && classItem.hasItemMeta() && !classItem.getItemMeta().hasLore()) {
            classItem.addLore("&7Click to become a &8" + classItem.getName());
        }
        return classItem;
    }

    public Set<CWItem> getEquipment() {
        return equipment;
    }

    public Set<Ability> getAbilities() {
        return abilities;
    }

    public DvzClass getDvZClass() {
        return dvzClass;
    }

    public ClassType getType() {
        return dvzClass.getType();
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
}
