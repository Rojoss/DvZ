package com.clashwars.dvz.abilities;

import com.clashwars.dvz.abilities.dragons.*;
import com.clashwars.dvz.abilities.dwarves.*;
import com.clashwars.dvz.abilities.monsters.*;
import com.clashwars.dvz.classes.DvzClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ability {
    BASE("DWARF", new BaseAbility(), new String[]{}),
    HEAL_POTION("DWARF", new HealPotion(), new String[]{}),
    SPEED_POTION("DWARF", new SpeedPotion(), new String[]{}),
    BUILDING_BRICK("BUILDER", new BuildingBrick(), new String[]{}),
    BUILDING_BLOCK("BUILDER", new BuildingBlock(), new String[]{}),
    REINFORCE("BUILDER", new Reinforce(), new String[]{}),
    SUMMON_STONE("BUILDER", new SummonStone(), new String[]{}),
    SUICIDE("MONSTER", new Suicide(), new String[]{}),
    HAMMER("MONSTER", new Hammer(), new String[]{}),
    TELEPORT_PORTAL("MONSTER", new TeleportPortal(), new String[]{}),
    INFECT("ZOMBIE", new Infect(), new String[]{}),
    RUSH("ZOMBIE", new Rush(), new String[]{}),
    RAPIDFIRE("SKELETON", new Rapidfire(), new String[]{}),
    POISON_ATTACK("SPIDER", new PoisonAttack(), new String[]{}),
    POISON("SPIDER", new Poison(), new String[]{}),
    WEB("SPIDER", new Web(), new String[]{}),
    EXPLODE("CREEPER", new Explode(), new String[]{}),
    SHOOT("BLAZE", new Shoot(), new String[]{}),
    GLIDE("BLAZE", new Glide(), new String[]{}),
    BLAST("BLAZE", new Blast(), new String[]{}),
    FIREBALL("BLAZE", new Fireball(), new String[]{}),
    POTION_BOMB("WITCH", new PotionBomb(), new String[]{}),
    PICKUP("ENDERMAN", new Pickup(), new String[]{}),
    BLINK("ENDERMAN", new Blink(), new String[]{}),
    PORTAL("ENDERMAN", new Portal(), new String[]{}),
    TORNADO("AIRDRAGON", new Tornado(), new String[]{}),
    GEYSER("WATERDRAGON", new Geyser(), new String[]{}),
    BURN("FIREDRAGON", new Burn(), new String[]{}),
    WIND("AIRDRAGON", new Wind(), new String[]{}),
    WINDSTORM("AIRDRAGON", new WindStorm(), new String[]{}),
    TOXIC_RAIN("WATERDRAGON", new ToxicRain(), new String[]{});

    private String className;
    private BaseAbility abilityClass;
    private List<String> aliases = new ArrayList<String>();

    Ability(String className, BaseAbility abilityClass, String[] aliases) {
        this.className = className;
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.aliases = Arrays.asList(aliases);
    }

    public DvzClass getDvzClass() {
        return DvzClass.fromString(className);
    }

    public BaseAbility getAbilityClass() {
        return abilityClass;
    }

    public List<String> getAliases() {
        return aliases;
    }

    //Get class by name or alias and return null if no class was found.
    public static Ability fromString(String name) {
        //First check by name.
        for (Ability c : values()) {
            if (c.toString().equalsIgnoreCase(name)) {
                return c;
            }
        }

        //Check by alias
        name = name.toLowerCase();
        for (Ability c : values()) {
            if (c.getAliases().contains(name)) {
                return c;
            }
        }
        return null;
    }

}
