package com.clashwars.dvz.classes;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.dragons.DragonClass;
import com.clashwars.dvz.classes.dragons.FireDragon;
import com.clashwars.dvz.classes.dwarves.*;
import com.clashwars.dvz.classes.monsters.MobClass;
import com.clashwars.dvz.classes.monsters.Zombie;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassManager {

    private DvZ dvz;

    private HashMap<DvzClass, BaseClass> baseClasses = new HashMap<DvzClass, BaseClass>();
    private HashMap<DvzClass, BaseClass> dwarfClasses = new HashMap<DvzClass, BaseClass>();
    private HashMap<DvzClass, BaseClass> monsterClasses = new HashMap<DvzClass, BaseClass>();
    private HashMap<DvzClass, BaseClass> dragonClasses = new HashMap<DvzClass, BaseClass>();
    private HashMap<DvzClass, BaseClass> allClasses = new HashMap<DvzClass, BaseClass>();

    public ClassManager(DvZ dvz) {
        this.dvz = dvz;
        populate();
    }

    private void populate() {
        baseClasses.put(DvzClass.DWARF, new DwarfClass());
        dwarfClasses.put(DvzClass.MINER, new Miner());
        dwarfClasses.put(DvzClass.BUILDER, new Builder());
        dwarfClasses.put(DvzClass.HUNTER, new Hunter());
        dwarfClasses.put(DvzClass.TAILOR, new Tailor());
        dwarfClasses.put(DvzClass.ALCHEMIST, new Alchemist());

        baseClasses.put(DvzClass.MONSTER, new MobClass());
        monsterClasses.put(DvzClass.ZOMBIE, new Zombie());
//        monsterClasses.put(DvZClass.SKELETON, new Skeleton());
//        monsterClasses.put(DvZClass.SPIDER, new Spider());
//        monsterClasses.put(DvZClass.CREEPER, new Creeper());
//        monsterClasses.put(DvZClass.ENDERMAN, new Enderman());
//        monsterClasses.put(DvZClass.BLAZE, new Blaze());
//        monsterClasses.put(DvZClass.PIG, new Pig());
//        monsterClasses.put(DvZClass.VILLAGER, new Villager());

        baseClasses.put(DvzClass.DRAGON, new DragonClass());
        dragonClasses.put(DvzClass.FIREDRAGON, new FireDragon());
//        dragonClasses.put(DvZClass.WATERDRAGON, new WaterDragon());
//        dragonClasses.put(DvZClass.AIRDRAGON, new AirDragon());

        allClasses.putAll(dwarfClasses);
        allClasses.putAll(monsterClasses);
        allClasses.putAll(dragonClasses);
    }

    //Get a Class by class type or name.
    public BaseClass getClass(String className) {
        return allClasses.get(DvzClass.fromString(className));
    }

    public BaseClass getClass(DvzClass type) {
        return allClasses.get(type);
    }

    //Get a map with classes based on classtype.
    //If classtype is null it will return all classes from all types.
    public HashMap<DvzClass, BaseClass> getClasses(ClassType type) {
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

    //Same as getClasses but return the specified amount of classes randomly picked from the type specified.
    public HashMap<DvzClass, BaseClass> getRandomClasses(ClassType type, int amount) {
        HashMap<DvzClass, BaseClass> classes = new HashMap<DvzClass, BaseClass>();
        HashMap<DvzClass, BaseClass> randomclasses = new HashMap<DvzClass, BaseClass>();
        if (type == null) {
            classes = allClasses;
        } else if (type == ClassType.DWARF) {
            classes = dwarfClasses;
        } else if (type == ClassType.MONSTER) {
            classes = monsterClasses;
        } else if (type == ClassType.DRAGON) {
            classes = dragonClasses;
        }
        //TODO: Get clases based on weight.
        for (int i = 0; i < amount; i++) {
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
