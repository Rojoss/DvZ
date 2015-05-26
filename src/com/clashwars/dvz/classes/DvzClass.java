package com.clashwars.dvz.classes;

import com.clashwars.dvz.classes.dragons.AirDragon;
import com.clashwars.dvz.classes.dragons.DragonClass;
import com.clashwars.dvz.classes.dragons.FireDragon;
import com.clashwars.dvz.classes.dragons.WaterDragon;
import com.clashwars.dvz.classes.dwarves.*;
import com.clashwars.dvz.classes.monsters.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DvzClass {
    BASE(ClassType.BASE, null, new BaseClass(), new String[]{}),
    DWARF(ClassType.BASE, "dwarf", new DwarfClass(), new String[]{"dwarves"}),
    BUILDER(ClassType.DWARF, "builder", new Builder(), new String[]{"builders", "constructor", "constructors"}),
    MINER(ClassType.DWARF, "miner", new Miner(), new String[]{"miners", "smith", "smiths"}),
    FLETCHER(ClassType.DWARF, "fletcher", new Fletcher(), new String[]{"fletchers", "hunter", "hunters"}),
    TAILOR(ClassType.DWARF, "tailor", new Tailor(), new String[]{"tailors"}),
    ALCHEMIST(ClassType.DWARF, "alchemist", new Alchemist(), new String[]{"alchemists", "brewer", "brewers"}),

    MONSTER(ClassType.BASE, "monster", new MobClass(), new String[]{"monsters", "mob", "mobs"}),
    ZOMBIE(ClassType.MONSTER, "monster", new Zombie(), new String[]{"zombies"}),
    SKELETON(ClassType.MONSTER, "monster", new Skeleton(), new String[]{"skeletons"}),
    SPIDER(ClassType.MONSTER, "monster", new Spider(), new String[]{"spiders"}),
    CREEPER(ClassType.MONSTER, "monster", new Creeper(), new String[]{"creepers"}),
    ENDERMAN(ClassType.MONSTER, "monster", new Enderman(), new String[]{"endermans"}),
    BLAZE(ClassType.MONSTER, "monster", new Blaze(), new String[]{"blazes"}),
    VILLAGER(ClassType.MONSTER, "monster", new Villager(), new String[]{"villagers", "villager"}),
    WITCH(ClassType.MONSTER, "monster", new Witch(), new String[]{"witches", "witch"}),

    DRAGON(ClassType.BASE, null, new DragonClass(), new String[]{"dragons"}),
    FIREDRAGON(ClassType.DRAGON, null, new FireDragon(), new String[]{"firedragons", "firedragon", "fire"}),
    WATERDRAGON(ClassType.DRAGON, null, new WaterDragon(), new String[]{"waterdragons", "waterdragon", "water"}),
    AIRDRAGON(ClassType.DRAGON, null, new AirDragon(), new String[]{"airdragons", "airdragon", "air"});

    private ClassType type;
    private String team;
    private BaseClass classClass;
    private List<String> aliases = new ArrayList<String>();

    DvzClass(ClassType type, String team, BaseClass classClass, String[] aliases) {
        this.type = type;
        this.team = team;
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

    public String getTeam() {
        return team;
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
