package com.clashwars.dvz.classes;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.ItemMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClassManager {

    private DvZ dvz;
    private ItemMenu switchOptionsMenu;
    public HashMap<UUID, ItemMenu> switchMenus = new HashMap<UUID, ItemMenu>();

    public ClassManager(DvZ dvz) {
        this.dvz = dvz;
        switchOptionsMenu = new ItemMenu("switch", 9, CWUtil.integrateColor("&6&lSwitch Class"));
    }

    //Get a Class by class type or name.
    public BaseClass getClass(String className) {
        return DvzClass.fromString(className).getClassClass();
    }

    public BaseClass getClass(DvzClass type) {
        return type.getClassClass();
    }

    //Get a map with classes based on classtype.
    public Map<DvzClass, BaseClass> getClasses(ClassType type) {
        Map<DvzClass, BaseClass> classes = new HashMap<DvzClass, BaseClass>();
        for (DvzClass c : DvzClass.values()) {
            if (c.getType() == type) {
                classes.put(c, c.getClassClass());
            }
        }
        return classes;
    }

    //Get a map with semi 'random' classes.
    //It will get classes based on weight.
    //For dwarf classes it will only return the configured amount of classes.
    //It will also calculate extra classes for example if a player completed parkour.
    //For monster classes it will try give each class based on weight. (The zombie class is always given)
    public Map<DvzClass, BaseClass> getRandomClasses(Player player, ClassType type) {
        Map<DvzClass, BaseClass> classes = getClasses(type);
        HashMap<DvzClass, BaseClass> randomclasses = new HashMap<DvzClass, BaseClass>();
        BaseClass c;

        if (type == ClassType.MONSTER) {
            //Loop through all monsters and check weight/chance.
            for (DvzClass dvzClass : classes.keySet()) {
                c = classes.get(dvzClass);
                if (CWUtil.randomFloat() <= c.getWeight()) {
                    randomclasses.put(dvzClass, c);
                }
            }
            //Make sure to always give zombie
            randomclasses.put(DvzClass.ZOMBIE, DvzClass.ZOMBIE.getClassClass());

        } else if (type == ClassType.DWARF) {
            CWPlayer cwp = dvz.getPM().getPlayer(player);
            //Default amount of classes to give.
            int classCount = dvz.getCfg().DWARF_CLASS_COUNT;

            //Add bonus class if parkour is completed.
            if (cwp.hasCompletedParkour()) {
                classCount++;
            }

            //Get bonus classes by permissions for example dvz.extraclasses.2 (Max is 10)
            if (!player.isOp()) {
                for (int i = 10; i > 0; i--) {
                    if (player.hasPermission("dvz.extraclasses." + i)) {
                        classCount += i;
                        break;
                    }
                }
            }

            if (classCount > classes.size()) {
                classCount = classes.size();
            }

            //Get the amount of times each class is picked.
            List<CWPlayer> dwarves = dvz.getPM().getPlayers(ClassType.DWARF, true);
            HashMap<DvzClass, Double> classCounts = new HashMap<DvzClass, Double>();
            for (DvzClass dvzClass : dvz.getCM().getClasses(ClassType.DWARF).keySet()) {
                classCounts.put(dvzClass, 0.0);
            }
            for (CWPlayer dwarf : dwarves) {
                DvzClass dwarfClass = dwarf.getPlayerClass();
                if (dwarfClass != null) {
                    classCounts.put(dwarfClass, classCounts.get(dwarfClass) + 1.0);
                }
            }

            //Add in fake players purely for testing purposes.
            for (DvzClass dwarfClass : dvz.getPM().fakePlayers.keySet()) {
                if (dwarfClass != null) {
                    classCounts.put(dwarfClass, dvz.getPM().fakePlayers.get(dwarfClass).doubleValue());
                }
            }

            //Multiply classes by weight.
            for (DvzClass dwarfClass : classCounts.keySet()) {
                // Picks * (weight * 100) [the *100 is just to make it a little more accurate]
                classCounts.put(dwarfClass, classCounts.get(dwarfClass) / (dwarfClass.getClassClass().getWeight() * 100));
            }

            //Get the 'random' classes with the least players based on weights.
            //So builder might have 10 players while miner has 8 but it would still pick builder if builder has a higher weight.
            Map<DvzClass, Double> sortedClassCounts = CWUtil.sortByValue(classCounts, false);
            for (Map.Entry<DvzClass, Double> entry : sortedClassCounts.entrySet()) {
                randomclasses.put(entry.getKey(), entry.getKey().getClassClass());
                classCount--;
                if (classCount <= 0) {
                    break;
                }
            }

            //Check if all classes have been given.
            //If not just give random classes based on weight only
            if (classCount > 0) {
                //Get total weight of all classes together.
                Double totalWeight = 0.0d;
                for (BaseClass bc : classes.values()) {
                    totalWeight += bc.getWeight();
                }

                DvzClass randomClass = null;
                int attempts = 20;
                for (int i = 0; i < classCount && attempts > 0; i++) {
                    double random = Math.random() * totalWeight;
                    for (DvzClass dvzClass : classes.keySet()) {
                        random -= dvzClass.getClassClass().getWeight();
                        if (random <= 0.0d) {
                            randomClass = dvzClass;
                            break;
                        }
                    }

                    //If this class was already picked then pick a new one to make sure we get 'classCount' classes.
                    if (randomClass == null || randomclasses.containsKey(randomClass)) {
                        i--;
                        attempts--;
                        continue;
                    }
                    attempts = 20;
                    randomclasses.put(randomClass, randomClass.getClassClass());
                }
            }
        }
        return randomclasses;
    }


    public void showSwitchOptionsMenu(Player player) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        switchOptionsMenu.clear(player);
        switchOptionsMenu.setSlot(new CWItem(Material.BOOK).setName("&5&lSWITCH INFO").addLore("&7Click on any of these classes to switch to it.")
                .addLore("&7You will be able to keep some of your items.").addLore("&7But you should only switch if it's really needed."), 0, player);
        int slot = 3;
        for (DvzClass dvzClass : cwp.getClassOptions()) {
            switchOptionsMenu.setSlot(dvzClass.getClassClass().getClassItem(), slot, player);
            slot++;
        }
        switchOptionsMenu.show(player);
    }

    public void showSwitchMenu(Player player, DvzClass dvzClass) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (!switchMenus.containsKey(player.getUniqueId())) {
            switchMenus.put(player.getUniqueId(), new ItemMenu("switch-" + player.getUniqueId(), 56, "XXX"));
        }
        ItemMenu menu = switchMenus.get(player.getUniqueId());
        menu.setData(dvzClass.toString());
        menu.setTitle(CWUtil.integrateColor("&6&lSwitch to &5&l" + dvzClass.getClassClass().getDisplayName()));
        menu.clear(null);

        menu.setSlot(new CWItem(Material.STAINED_GLASS_PANE, 1, (byte)14).setName("&4&lCANCEL").addLore("&7All items in the menu will be given back.").addLore("&7and you keep the same class."), 0, null);
        menu.setSlot(new CWItem(Material.BOOK).setName("&5&lSWITCH INFO").addLore("&7All items in this menu will be kept after switching.").addLore("&7Not all items can be kept though.")
                .addLore("&7Just click on the items in your inventory to add them.").addLore("&7And click on the items in the menu to not keep them.").addLore("&7Armor is kept by default."), 4, null);
        menu.setSlot(new CWItem(Material.STAINED_GLASS_PANE, 1, (byte)5).setName("&a&lSWITCH").addLore("&7You will switch classes.").addLore("&7All items in your inventory will be destroyed!")
                .addLore("&cYour workshop will be deleted as well!").addLore("&4Make sure you want to do this!"), 8, null);

        menu.show(player);

        ItemStack empty = new ItemStack(Material.AIR);
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            if (!Product.canKeep(player.getInventory().getItem(i).getType())) {
                continue;
            }
            menu.setSlot(new CWItem(player.getInventory().getItem(i)), i + 9, null);
            player.getInventory().setItem(i, empty);
        }

        player.getInventory().setHelmet(empty);
        menu.setSlot(new CWItem(player.getInventory().getHelmet()), 45, null);
        player.getInventory().setChestplate(empty);
        menu.setSlot(new CWItem(player.getInventory().getChestplate()), 46, null);
        player.getInventory().setLeggings(empty);
        menu.setSlot(new CWItem(player.getInventory().getLeggings()), 47, null);
        player.getInventory().setBoots(empty);
        menu.setSlot(new CWItem(player.getInventory().getBoots()), 48, null);
    }
}
