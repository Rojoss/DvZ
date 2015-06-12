package com.clashwars.dvz.abilities;

import com.clashwars.dvz.abilities.dragons.*;
import com.clashwars.dvz.abilities.dwarves.*;
import com.clashwars.dvz.abilities.monsters.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ability {
    BASE(new BaseAbility(), new String[]{}),
    HEAL_POTION(new HealPotion(), new String[]{}),
    SPEED_POTION(new SpeedPotion(), new String[]{}),
    BUILDING_BRICK(new BuildingBrick(), new String[]{}),
    BUILDING_BLOCK(new BuildingBlock(), new String[]{}),
    REINFORCE(new Reinforce(), new String[]{}),
    SUMMON_STONE(new SummonStone(), new String[]{}),
    SUICIDE(new Suicide(), new String[]{}),
    HAMMER(new Hammer(), new String[]{}),
    TELEPORT_PORTAL(new TeleportPortal(), new String[]{}),
    INFECT(new Infect(), new String[]{}),
    RUSH(new Rush(), new String[]{}),
    RAPIDFIRE(new Rapidfire(), new String[]{}),
    POISON_ATTACK(new PoisonAttack(), new String[]{}),
    POISON(new Poison(), new String[]{}),
    WEB(new Web(), new String[]{}),
    EXPLODE(new Explode(), new String[]{}),
    SHOOT(new Shoot(), new String[]{}),
    GLIDE(new Glide(), new String[]{}),
    BLAST(new Blast(), new String[]{}),
    FIREBALL(new Fireball(), new String[]{}),
    POTION_BOMB(new PotionBomb(), new String[]{}),
    PICKUP(new Pickup(), new String[]{}),
    BLINK(new Blink(), new String[]{}),
    PORTAL(new Portal(), new String[]{}),
    TORNADO(new Tornado(), new String[]{}),
    GEYSER(new Geyser(), new String[]{}),
    BURN(new Burn(), new String[]{}),
    WIND(new Wind(), new String[]{}),
    WINDSTORM(new WindStorm(), new String[]{}),
    TOXIC_RAIN(new ToxicRain(), new String[]{}),
    FIREFLY(new FireFly(), new String[]{}),
    WATER_BUBBLE(new WaterBubble(), new String[]{}),
    FIRE_BREATH(new FireBreath(), new String[]{}),
    AIRSHIELD(new AirShield(), new String[]{}),
    MORPH(new Morph(), new String[]{}),
    BUFF(new Buff(), new String[]{}),
    FLY(new Fly(), new String[]{}),
    EXPLOSIVE_EGG(new ExplosiveEgg(), new String[]{}),
    LAY_EGG(new LayEgg(), new String[]{}),
    DROP_EGG(new DropEgg(), new String[]{}),
    HORN(new Horn(), new String[]{});

    private BaseAbility abilityClass;
    private List<String> aliases = new ArrayList<String>();

    Ability(BaseAbility abilityClass, String[] aliases) {
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.aliases = Arrays.asList(aliases);
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
