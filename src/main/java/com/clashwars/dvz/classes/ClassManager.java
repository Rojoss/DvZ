package com.clashwars.dvz.classes;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.dragons.DragonClass;
import com.clashwars.dvz.classes.dragons.FireDragon;
import com.clashwars.dvz.classes.dwarves.*;
import com.clashwars.dvz.classes.monsters.MobClass;
import com.clashwars.dvz.classes.monsters.Zombie;
import com.clashwars.dvz.player.CWPlayer;

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
    public Map<DvzClass, BaseClass> getRandomClasses(ClassType type) {
        Map<DvzClass, BaseClass> classes = getClasses(type);
        HashMap<DvzClass, BaseClass> randomclasses = new HashMap<DvzClass, BaseClass>();

        //TODO: remake this method to work as described above.

        for (int i = 0; i < 2; i++) {
            DvzClass c = CWUtil.random(new ArrayList<DvzClass>(classes.keySet()));
            if (randomclasses.containsKey(c)) {
                i--;
                continue;
            }
            randomclasses.put(c, classes.get(c));
        }
        return randomclasses;
    }
}
