package com.clashwars.dvz.classes;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.dragons.DragonClass;
import com.clashwars.dvz.classes.dragons.FireDragon;
import com.clashwars.dvz.classes.dwarves.DwarfClass;
import com.clashwars.dvz.classes.dwarves.Miner;
import com.clashwars.dvz.classes.monsters.MobClass;
import com.clashwars.dvz.classes.monsters.Zombie;

import java.util.HashMap;

public class ClassManager {

    private DvZ dvz;

    private HashMap<DvZClass, BaseClass> dwarfClasses = new HashMap<DvZClass, BaseClass>();
    private HashMap<DvZClass, BaseClass> monsterClasses = new HashMap<DvZClass, BaseClass>();
    private HashMap<DvZClass, BaseClass> dragonClasses = new HashMap<DvZClass, BaseClass>();
    private HashMap<DvZClass, BaseClass> allClasses = new HashMap<DvZClass, BaseClass>();

    public ClassManager(DvZ dvz) {
        this.dvz = dvz;
        populate();
    }

    private void populate() {
        dwarfClasses.put(DvZClass.DWARF, new DwarfClass());
        dwarfClasses.put(DvZClass.MINER, new Miner());
        //TODO: Fill up dwarf classes

        monsterClasses.put(DvZClass.MONSTER, new MobClass());
        monsterClasses.put(DvZClass.ZOMBIE, new Zombie());
        //TODO: Fill up monster classes

        dragonClasses.put(DvZClass.DRAGON, new DragonClass());
        dragonClasses.put(DvZClass.FIREDRAGON, new FireDragon());
        //TODO: Fill up dragon classes

        allClasses.putAll(dwarfClasses);
        allClasses.putAll(monsterClasses);
        allClasses.putAll(dragonClasses);
    }

    //Get a Class by class type or name.
    public BaseClass getClass(String className) {
        return allClasses.get(DvZClass.fromString(className));
    }

    public BaseClass getClass(DvZClass type) {
        return allClasses.get(type);
    }

    //Get a map with classes based on classtype.
    //If classtype is null it will return all classes from all types.
    public HashMap<DvZClass, BaseClass> getClasses(ClassType type) {
        if (type == null) {
            return allClasses;
        } else if (type == ClassType.DWARF) {
            return dwarfClasses;
        } else if (type == ClassType.MONSTER) {
            return monsterClasses;
        } else if (type == ClassType.DRAGON) {
            return dragonClasses;
        }
        return null;
    }


}
