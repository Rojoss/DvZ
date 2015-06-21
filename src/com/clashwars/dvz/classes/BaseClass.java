package com.clashwars.dvz.classes;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BaseClass implements Listener {

    protected DvZ dvz = DvZ.inst();
    protected DvzClass dvzClass = null;

    protected Set<DvzItem> equipment = new HashSet<DvzItem>();
    protected Set<Ability> abilities = new HashSet<Ability>();
    protected DvzItem classItem = null;

    protected String disguise = "";
    protected String displayName = "&7Unknown";
    protected String produce = "";
    protected String task = "";

    protected double weight = 0.0d;
    protected int health = 20;
    protected float speed = 0.2f;

    protected boolean switchable = false;

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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getDisguise() {
        return disguise;
    }

    public void setDisguise(String disguise) {
        this.disguise = disguise;
    }

    public boolean isSwitchable() {
        return switchable;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProduce() {
        if (produce == null || produce.isEmpty()) {
            return "&8&oNo info available.";
        } else {
            return produce;
        }
    }

    public void setProduce(String produce) {
        this.produce = produce;
    }

    public String getTask() {
        if (task == null || task.isEmpty()) {
            return "&8&oNo info available.";
        } else {
            return task;
        }
    }

    public void setTask(String task) {
        this.task = task;
    }

    public DvzItem getClassItem() {
        if (classItem != null) {
            classItem.setLore(new String[]{}).addLore("&7Click to become a &8" + getDisplayName());
            if (dvzClass != null && dvzClass.getType() == ClassType.DWARF) {
                classItem.addLore("&aTask&8: &7&o" + getTask().replace(". ", ".|&7&o").replace("! ", "!|&7&o")).addLore("&aProduce&8: &7&o" + getProduce().replace(". ", "|&7&o").replace("! ", "|&7&o"));
            }
            classItem.setName(getDisplayName());
            classItem.replaceLoreNewLines();
        }
        return classItem;
    }

    public Set<DvzItem> getEquipment() {
        return equipment;
    }

    public Set<Ability> getAbilities() {
        Set<Ability> abilityList = new HashSet<Ability>();
        abilityList.addAll(abilities);
        if (getDvZClass() != DvzClass.BASE) {
            abilityList.addAll(DvzClass.BASE.getClassClass().getAbilities());
        }
        if (getDvZClass().getType() == ClassType.MONSTER && getDvZClass() != DvzClass.MONSTER) {
            abilityList.addAll(DvzClass.MONSTER.getClassClass().getAbilities());
        }
        if (getDvZClass().getType() == ClassType.DWARF && getDvZClass() != DvzClass.DWARF) {
            abilityList.addAll(DvzClass.DWARF.getClassClass().getAbilities());
        }
        if (getDvZClass().getType() == ClassType.DRAGON && getDvZClass() != DvzClass.DRAGON) {
            abilityList.addAll(DvzClass.DRAGON.getClassClass().getAbilities());
        }
        return abilityList;
    }

    public DvzClass getDvZClass() {
        return dvzClass;
    }

    public void setClass(DvzClass dvzClass) {
        this.dvzClass = dvzClass;
    }

    public ClassType getType() {
        return dvzClass.getType();
    }

    public void equipItems(Player player) {
        Map<DvzItem, Integer> itemMap = new HashMap<DvzItem, Integer>();
        Map<DvzItem, Integer> itemSlotMap = new HashMap<DvzItem, Integer>();
        //Class items
        for (DvzItem item : equipment) {
            if (!item.doGive()) {
                continue;
            }
            if (item.getType() == Material.LEATHER_HELMET || item.getType() == Material.CHAINMAIL_HELMET || item.getType() == Material.GOLD_HELMET
                    || item.getType() == Material.IRON_HELMET || item.getType() == Material.DIAMOND_HELMET) {
                player.getInventory().setHelmet(item);
            } else if (item.getType() == Material.LEATHER_CHESTPLATE || item.getType() == Material.CHAINMAIL_CHESTPLATE || item.getType() == Material.GOLD_CHESTPLATE
                    || item.getType() == Material.IRON_CHESTPLATE || item.getType() == Material.DIAMOND_CHESTPLATE) {
                player.getInventory().setChestplate(item);
            } else if (item.getType() == Material.LEATHER_LEGGINGS || item.getType() == Material.CHAINMAIL_LEGGINGS || item.getType() == Material.GOLD_LEGGINGS
                    || item.getType() == Material.IRON_LEGGINGS || item.getType() == Material.DIAMOND_LEGGINGS) {
                player.getInventory().setLeggings(item);
            } else if (item.getType() == Material.LEATHER_BOOTS || item.getType() == Material.CHAINMAIL_BOOTS || item.getType() == Material.GOLD_BOOTS
                    || item.getType() == Material.IRON_BOOTS || item.getType() == Material.DIAMOND_BOOTS) {
                player.getInventory().setBoots(item);
            } else if (item.hasSlot()) {
                itemSlotMap.put(item, item.getPriority());
            } else {
                itemMap.put(item, item.getPriority());
            }
        }
        //Ability items
        DvzItem castItem;
        Set<Ability> abilities = getAbilities();
        for (Ability ability : abilities) {
            castItem = ability.getAbilityClass().getCastItem();
            if (castItem != null) {
                ability.getAbilityClass().onCastItemGiven(player);
            }
            if (castItem != null && (castItem.doGive())) {
                if (castItem.hasSlot()) {
                    itemSlotMap.put(castItem, castItem.getPriority());
                } else {
                    itemMap.put(castItem, castItem.getPriority());
                }
            }
        }

        //Sort items by priority
        itemMap = CWUtil.sortByValue(itemMap, true);
        itemSlotMap = CWUtil.sortByValue(itemSlotMap, true);

        //Give items
        for (DvzItem item : itemSlotMap.keySet()) {
            player.getInventory().setItem(item.getSlot(), item);
        }
        for (DvzItem item : itemMap.keySet()) {
            item.giveToPlayer(player);
        }
        player.updateInventory();
    }

    //Called when player gets the class equiped.
    //Override this method in other classes for example if you want to add a potion effect or set movement speed etc.
    public void onEquipClass(Player player) {
        //--
    }
}
