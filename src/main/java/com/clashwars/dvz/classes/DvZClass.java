package com.clashwars.dvz.classes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DvzClass {
    DWARF(ClassType.DWARF, new String[] {"dwarves"}),
    BUILDER(ClassType.DWARF, new String[] {"builders", "constructor", "constructors"}),
    MINER(ClassType.DWARF, new String[] {"miners", "smith", "smiths"}),
    HUNTER(ClassType.DWARF, new String[] {"hunters"}),
    TAILOR(ClassType.DWARF, new String[] {"tailors"}),
    ALCHEMIST(ClassType.DWARF, new String[] {"alchemists", "brewer", "brewers"}),

    MONSTER(ClassType.MONSTER, new String[] {"monsters", "mob", "mobs"}),
    ZOMBIE(ClassType.MONSTER, new String[] {"zombies"}),
    SKELETON(ClassType.MONSTER, new String[] {"skeletons"}),
    SPIDER(ClassType.MONSTER, new String[] {"spiders"}),
    CREEPER(ClassType.MONSTER, new String[] {"creepers"}),
    ENDERMAN(ClassType.MONSTER, new String[] {"endermans"}),
    BLAZE(ClassType.MONSTER, new String[] {"blazes"}),
    PIG(ClassType.MONSTER, new String[] {"pigs", "babypig", "babypigs", "pigman", "pigmans", "hungrypig", "hungrypigs"}),
    //TODO: Change villager to proper name.
    VILLAGER(ClassType.MONSTER, new String[] {"villagers", "witch", "witches"}),

    DRAGON(ClassType.DRAGON, new String[] {"dragons"}),
    FIREDRAGON(ClassType.DRAGON, new String[] {"firedragons", "firedragon", "fire"}),
    WATERDRAGON(ClassType.DRAGON, new String[] {"waterdragons", "waterdragon", "water"}),
    AIRDRAGON(ClassType.DRAGON, new String[] {"airdragons", "airdragon", "air"});

    private ClassType type;
    private List<String> aliases = new ArrayList<String>();

    DvzClass(ClassType type, String[] aliases) {
        this.type = type;
        this.aliases = Arrays.asList(aliases);
    }

    public ClassType getType() {
        return type;
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
