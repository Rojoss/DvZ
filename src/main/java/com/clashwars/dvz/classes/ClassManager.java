package com.clashwars.dvz.classes;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassManager {

    private DvZ dvz;

    public ClassManager(DvZ dvz) {
        this.dvz = dvz;
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
            List<CWPlayer> dwarves = dvz.getPM().getPlayers(ClassType.DWARF);
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
}
