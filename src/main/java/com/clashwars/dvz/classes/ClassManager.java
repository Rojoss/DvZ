package com.clashwars.dvz.classes;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.dragons.DragonClass;
import com.clashwars.dvz.classes.dragons.FireDragon;
import com.clashwars.dvz.classes.dwarves.*;
import com.clashwars.dvz.classes.monsters.MobClass;
import com.clashwars.dvz.classes.monsters.Zombie;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
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

            //Get total weight of all classes together.
            Double totalWeight = 0.0d;
            for (BaseClass bc : classes.values()) {
                totalWeight += bc.getWeight();
            }

            //Get the 'classCount' amount of classes based on weight.
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
        return randomclasses;
    }
}
