package com.clashwars.dvz.abilities;

import com.clashwars.dvz.abilities.dragons.air.AirShield;
import com.clashwars.dvz.abilities.dragons.air.Wind;
import com.clashwars.dvz.abilities.dragons.air.WindStorm;
import com.clashwars.dvz.abilities.dragons.fire.Burn;
import com.clashwars.dvz.abilities.dragons.fire.FireBreath;
import com.clashwars.dvz.abilities.dragons.fire.FireFly;
import com.clashwars.dvz.abilities.dragons.water.Geyser;
import com.clashwars.dvz.abilities.dragons.water.ToxicRain;
import com.clashwars.dvz.abilities.dragons.water.WaterBubble;
import com.clashwars.dvz.abilities.dwarves.HealPotion;
import com.clashwars.dvz.abilities.dwarves.SpeedPotion;
import com.clashwars.dvz.abilities.dwarves.bonus.Leap;
import com.clashwars.dvz.abilities.dwarves.bonus.Torrent;
import com.clashwars.dvz.abilities.dwarves.builder.BuildingBlock;
import com.clashwars.dvz.abilities.dwarves.builder.BuildingBrick;
import com.clashwars.dvz.abilities.dwarves.builder.Reinforce;
import com.clashwars.dvz.abilities.dwarves.builder.SummonStone;
import com.clashwars.dvz.abilities.dwarves.dragonslayer.Horn;
import com.clashwars.dvz.abilities.monsters.Hammer;
import com.clashwars.dvz.abilities.monsters.Suicide;
import com.clashwars.dvz.abilities.monsters.TeleportPortal;
import com.clashwars.dvz.abilities.monsters.blaze.Blast;
import com.clashwars.dvz.abilities.monsters.blaze.Fireball;
import com.clashwars.dvz.abilities.monsters.blaze.Glide;
import com.clashwars.dvz.abilities.monsters.blaze.Shoot;
import com.clashwars.dvz.abilities.monsters.chicken.DropEgg;
import com.clashwars.dvz.abilities.monsters.chicken.ExplosiveEgg;
import com.clashwars.dvz.abilities.monsters.chicken.Fly;
import com.clashwars.dvz.abilities.monsters.chicken.LayEgg;
import com.clashwars.dvz.abilities.monsters.creeper.Explode;
import com.clashwars.dvz.abilities.monsters.enderman.Blink;
import com.clashwars.dvz.abilities.monsters.enderman.Pickup;
import com.clashwars.dvz.abilities.monsters.enderman.Portal;
import com.clashwars.dvz.abilities.monsters.skeleton.Rapidfire;
import com.clashwars.dvz.abilities.monsters.spider.Poison;
import com.clashwars.dvz.abilities.monsters.spider.PoisonAttack;
import com.clashwars.dvz.abilities.monsters.spider.Web;
import com.clashwars.dvz.abilities.monsters.witchvillager.Buff;
import com.clashwars.dvz.abilities.monsters.witchvillager.Morph;
import com.clashwars.dvz.abilities.monsters.witchvillager.PotionBomb;
import com.clashwars.dvz.abilities.monsters.zombie.Infect;
import com.clashwars.dvz.abilities.monsters.zombie.Rush;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Ability {
    BASE(new BaseAbility(), false, new String[]{}),

    //Dwarf abilities
    HEAL_POTION(new HealPotion(), false, new String[]{}),
    SPEED_POTION(new SpeedPotion(), false, new String[]{}),
    BUILDING_BRICK(new BuildingBrick(), false, new String[]{}),
    BUILDING_BLOCK(new BuildingBlock(), false, new String[]{}),
    REINFORCE(new Reinforce(), false, new String[]{}),
    SUMMON_STONE(new SummonStone(), false, new String[]{}),
    HORN(new Horn(), false, new String[]{}),

    //Monster abilities
    SUICIDE(new Suicide(), false, new String[]{}),
    HAMMER(new Hammer(), false, new String[]{}),
    TELEPORT_PORTAL(new TeleportPortal(), false, new String[]{}),
    INFECT(new Infect(), false, new String[]{}),
    RUSH(new Rush(), false, new String[]{}),
    RAPIDFIRE(new Rapidfire(), false, new String[]{}),
    POISON_ATTACK(new PoisonAttack(), false, new String[]{}),
    POISON(new Poison(), false, new String[]{}),
    WEB(new Web(), false, new String[]{}),
    EXPLODE(new Explode(), false, new String[]{}),
    SHOOT(new Shoot(), false, new String[]{}),
    GLIDE(new Glide(), false, new String[]{}),
    BLAST(new Blast(), false, new String[]{}),
    FIREBALL(new Fireball(), false, new String[]{}),
    PICKUP(new Pickup(), false, new String[]{}),
    BLINK(new Blink(), false, new String[]{}),
    PORTAL(new Portal(), false, new String[]{}),
    MORPH(new Morph(), false, new String[]{}),
    POTION_BOMB(new PotionBomb(), false, new String[]{}),
    BUFF(new Buff(), false, new String[]{}),
    FLY(new Fly(), false, new String[]{}),
    EXPLOSIVE_EGG(new ExplosiveEgg(), false, new String[]{}),
    LAY_EGG(new LayEgg(), false, new String[]{}),
    DROP_EGG(new DropEgg(), false, new String[]{}),

    //Dragon abilities
    BURN(new Burn(), false, new String[]{}),
    FIREFLY(new FireFly(), false, new String[]{}),
    FIRE_BREATH(new FireBreath(), false, new String[]{}),
    GEYSER(new Geyser(), false, new String[]{}),
    TOXIC_RAIN(new ToxicRain(), false, new String[]{}),
    WATER_BUBBLE(new WaterBubble(), false, new String[]{}),
    WIND(new Wind(), false, new String[]{}),
    WINDSTORM(new WindStorm(), false, new String[]{}),
    AIRSHIELD(new AirShield(), false, new String[]{}),

    //Dwarf bonus abilities
    LEAP(new Leap(), true, new String[] {}),
    TORRENT(new Torrent(), true, new String[] {});

    //------------------------------
    //END OF ABILITIES
    //---


    private BaseAbility abilityClass;
    private List<String> aliases = new ArrayList<String>();
    private boolean dwarfBonus = false;

    Ability(BaseAbility abilityClass, boolean dwarfBonus, String[] aliases) {
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.aliases = Arrays.asList(aliases);
        this.dwarfBonus = dwarfBonus;
    }

    public BaseAbility getAbilityClass() {
        return abilityClass;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public boolean isDwarfBonus() {
        return dwarfBonus;
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
