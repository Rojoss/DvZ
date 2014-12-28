package com.clashwars.dvz.abilities;

import com.clashwars.dvz.abilities.dragons.Geyser;
import com.clashwars.dvz.abilities.dragons.Tornado;
import com.clashwars.dvz.abilities.dwarves.*;
import com.clashwars.dvz.abilities.monsters.*;
import com.clashwars.dvz.classes.DvzClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ability {
    BASE(DvzClass.DWARF, new BaseAbility(), new String[]{}),
    HEAL_POTION(DvzClass.DWARF, new HealPotion(), new String[]{}),
    SPEED_POTION(DvzClass.DWARF, new SpeedPotion(), new String[]{}),
    BUILDING_BRICK(DvzClass.BUILDER, new BuildingBrick(), new String[]{}),
    BUILDING_BLOCK(DvzClass.BUILDER, new BuildingBlock(), new String[]{}),
    SUICIDE(DvzClass.MONSTER, new Suicide(), new String[]{}),
    HAMMER(DvzClass.MONSTER, new Hammer(), new String[]{}),
    TELEPORT_PORTAL(DvzClass.MONSTER, new TeleportPortal(), new String[]{}),
    INFECT(DvzClass.ZOMBIE, new Infect(), new String[]{}),
    RUSH(DvzClass.ZOMBIE, new Rush(), new String[]{}),
    RAPIDFIRE(DvzClass.SKELETON, new Rapidfire(), new String[]{}),
    POISON_ATTACK(DvzClass.SPIDER, new PoisonAttack(), new String[]{}),
    POISON(DvzClass.SPIDER, new Poison(), new String[]{}),
    WEB(DvzClass.SPIDER, new Web(), new String[]{}),
    EXPLODE(DvzClass.CREEPER, new Explode(), new String[]{}),
    SHOOT(DvzClass.BLAZE, new Shoot(), new String[]{}),
    GLIDE(DvzClass.BLAZE, new Glide(), new String[]{}),
    BLAST(DvzClass.BLAZE, new Blast(), new String[]{}),
    FIREBALL(DvzClass.BLAZE, new Fireball(), new String[]{}),
    POTION_BOMB(DvzClass.VILLAGER, new PotionBomb(), new String[]{}),
    PICKUP(DvzClass.ENDERMAN, new Pickup(), new String[]{}),
    BLINK(DvzClass.ENDERMAN, new Blink(), new String[]{}),
    PORTAL(DvzClass.ENDERMAN, new Portal(), new String[]{}),
    TORNADO(DvzClass.AIRDRAGON, new Tornado(), new String[]{}),
    GEYSER(DvzClass.WATERDRAGON, new Geyser(), new String[]{});

    private DvzClass dvzClass;
    private BaseAbility abilityClass;
    private List<String> aliases = new ArrayList<String>();

    Ability(DvzClass dvzClass, BaseAbility abilityClass, String[] aliases) {
        this.dvzClass = dvzClass;
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.aliases = Arrays.asList(aliases);
    }

    public DvzClass getDvzClass() {
        return dvzClass;
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
