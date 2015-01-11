package com.clashwars.dvz.classes;

import com.clashwars.dvz.classes.dragons.*;
import com.clashwars.dvz.classes.dwarves.*;
import com.clashwars.dvz.classes.monsters.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DvzClass {
    BASE(ClassType.BASE, new BaseClass(), new String[]{}),
    DWARF(ClassType.BASE, new DwarfClass(), new String[]{"dwarves"}),
    BUILDER(ClassType.DWARF, new Builder(), new String[]{"builders", "constructor", "constructors"}),
    MINER(ClassType.DWARF, new Miner(), new String[]{"miners", "smith", "smiths"}),
    FLETCHER(ClassType.DWARF, new Fletcher(), new String[]{"fletchers", "hunter", "hunters"}),
    TAILOR(ClassType.DWARF, new Tailor(), new String[]{"tailors"}),
    ALCHEMIST(ClassType.DWARF, new Alchemist(), new String[]{"alchemists", "brewer", "brewers"}),

    MONSTER(ClassType.BASE, new MobClass(), new String[]{"monsters", "mob", "mobs"}),
    ZOMBIE(ClassType.MONSTER, new Zombie(), new String[]{"zombies"}),
    SKELETON(ClassType.MONSTER, new Skeleton(), new String[]{"skeletons"}),
    SPIDER(ClassType.MONSTER, new Spider(), new String[]{"spiders"}),
    CREEPER(ClassType.MONSTER, new Creeper(), new String[]{"creepers"}),
    ENDERMAN(ClassType.MONSTER, new Enderman(), new String[]{"endermans"}),
    BLAZE(ClassType.MONSTER, new Blaze(), new String[]{"blazes"}),
    PIG(ClassType.MONSTER, new Pig(), new String[]{"pigs", "babypig", "babypigs", "pigman", "pigmans", "hungrypig", "hungrypigs"}),
    VILLAGER(ClassType.MONSTER, new Villager(), new String[]{"villagers", "villager"}),
    WITCH(ClassType.MONSTER, new Witch(), new String[]{"witches", "witch"}),

    DRAGON(ClassType.BASE, new DragonClass(), new String[]{"dragons"}),
    FIREDRAGON(ClassType.DRAGON, new FireDragon(), new String[]{"firedragons", "firedragon", "fire"}),
    WATERDRAGON(ClassType.DRAGON, new WaterDragon(), new String[]{"waterdragons", "waterdragon", "water"}),
    AIRDRAGON(ClassType.DRAGON, new AirDragon(), new String[]{"airdragons", "airdragon", "air"});

    private ClassType type;
    private BaseClass classClass;
    private List<String> aliases = new ArrayList<String>();

    DvzClass(ClassType type, BaseClass classClass, String[] aliases) {
        this.type = type;
        this.classClass = classClass;
        this.aliases = Arrays.asList(aliases);
    }

    public ClassType getType() {
        return type;
    }

    public BaseClass getClassClass() {
        return classClass;
    }

    public boolean isBaseClass() {
        if (this == DvzClass.DWARF || this == DvzClass.MONSTER || this == DvzClass.DRAGON || this == DvzClass.BASE) {
            return true;
        }
        return false;
    }

    public List<String> getAliases() {
        return aliases;
    }

    //Get class by name or alias and return null if no class was found.
    public static DvzClass fromString(String name) {
        //First check by name.
        for (DvzClass c : values()) {
            if (c.toString().equalsIgnoreCase(name)) {
                return c;
            }
        }

        //Check by alias
        name = name.toLowerCase();
        for (DvzClass c : values()) {
            if (c.getAliases().contains(name)) {
                return c;
            }
        }
        return null;
    }


}
