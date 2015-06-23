package com.clashwars.dvz.classes;

import com.clashwars.dvz.classes.dragons.AirDragon;
import com.clashwars.dvz.classes.dragons.DragonClass;
import com.clashwars.dvz.classes.dragons.FireDragon;
import com.clashwars.dvz.classes.dragons.WaterDragon;
import com.clashwars.dvz.classes.dwarves.*;
import com.clashwars.dvz.classes.monsters.*;
import com.clashwars.dvz.stats.internal.StatType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DvzClass {
    BASE(ClassType.BASE, null, new BaseClass(), null, new String[]{}),
    DWARF(ClassType.BASE, "dwarf", new DwarfClass(), null, new String[]{"dwarves"}),
    BUILDER(ClassType.DWARF, "builder", new Builder(), StatType.BUILDER_TIMES_PICKED, new String[]{"builders", "constructor", "constructors"}),
    MINER(ClassType.DWARF, "miner", new Miner(), StatType.MINER_TIMES_PICKED, new String[]{"miners", "smith", "smiths"}),
    FLETCHER(ClassType.DWARF, "fletcher", new Fletcher(), StatType.FLETCHER_TIMES_PICKED, new String[]{"fletchers", "hunter", "hunters"}),
    TAILOR(ClassType.DWARF, "tailor", new Tailor(), StatType.TAILOR_TIMES_PICKED, new String[]{"tailors"}),
    ALCHEMIST(ClassType.DWARF, "alchemist", new Alchemist(), StatType.ALCHEMIST_TIMES_PICKED, new String[]{"alchemists", "brewer", "brewers"}),
    BAKER(ClassType.DWARF, "baker", new Baker(), StatType.BAKER_TIMES_PICKED, new String[]{"bakers"}),

    MONSTER(ClassType.BASE, "monster", new MobClass(), null, new String[]{"monsters", "mob", "mobs"}),
    ZOMBIE(ClassType.MONSTER, "monster", new Zombie(), StatType.ZOMBIE_PICKS, new String[]{"zombies"}),
    SKELETON(ClassType.MONSTER, "monster", new Skeleton(), StatType.SKELETON_PICKS, new String[]{"skeletons"}),
    SPIDER(ClassType.MONSTER, "monster", new Spider(), StatType.SPIDER_PICKS, new String[]{"spiders"}),
    CREEPER(ClassType.MONSTER, "monster", new Creeper(), StatType.CREEPER_PICKS, new String[]{"creepers"}),
    ENDERMAN(ClassType.MONSTER, "monster", new Enderman(), StatType.ENDERMAN_PICKS, new String[]{"endermans"}),
    BLAZE(ClassType.MONSTER, "monster", new Blaze(), StatType.BLAZE_PICKS, new String[]{"blazes"}),
    VILLAGER(ClassType.MONSTER, "monster", new Villager(), StatType.VILLAGER_PICKS, new String[]{"villagers", "villager"}),
    WITCH(ClassType.MONSTER, "monster", new Witch(), StatType.VILLAGER_PICKS, new String[]{"witches", "witch"}),
    CHICKEN(ClassType.MONSTER, "monster", new Chicken(), StatType.CHICKEN_PICKS, new String[]{"chickens"}),

    DRAGON(ClassType.BASE, null, new DragonClass(), null, new String[]{"dragons"}),
    FIREDRAGON(ClassType.DRAGON, null, new FireDragon(), null, new String[]{"firedragons", "firedragon", "fire"}),
    WATERDRAGON(ClassType.DRAGON, null, new WaterDragon(), null, new String[]{"waterdragons", "waterdragon", "water"}),
    AIRDRAGON(ClassType.DRAGON, null, new AirDragon(), null, new String[]{"airdragons", "airdragon", "air"});

    private ClassType type;
    private String team;
    private BaseClass classClass;
    private StatType pickStat;
    private List<String> aliases = new ArrayList<String>();

    DvzClass(ClassType type, String team, BaseClass classClass, StatType pickStat, String[] aliases) {
        this.type = type;
        this.team = team;
        this.classClass = classClass;
        this.pickStat = pickStat;
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

    public StatType getPickStat() {
        return pickStat;
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
