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
import com.clashwars.dvz.abilities.dwarves.bonus.*;
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
    BASE(new BaseAbility(), false, ""),

    //Dwarf abilities
    HEAL_POTION(new HealPotion(), false, ""),
    SPEED_POTION(new SpeedPotion(), false, ""),
    BUILDING_BRICK(new BuildingBrick(), false, ""),
    BUILDING_BLOCK(new BuildingBlock(), false, ""),
    REINFORCE(new Reinforce(), false, ""),
    SUMMON_STONE(new SummonStone(), false, ""),
    HORN(new Horn(), false, ""),

    //Monster abilities
    SUICIDE(new Suicide(), false, "{0} committed suicide"),
    HAMMER(new Hammer(), false, ""),
    TELEPORT_PORTAL(new TeleportPortal(), false, ""),
    INFECT(new Infect(), false, ""),
    RUSH(new Rush(), false, ""),
    RAPIDFIRE(new Rapidfire(), false, ""),
    POISON_ATTACK(new PoisonAttack(), false, "{0} died from {1}'s poison attack"),
    POISON(new Poison(), false, "{0} died from {1}'s poison cloud"),
    WEB(new Web(), false, ""),
    EXPLODE(new Explode(), false, "{0} died from {1}'s creeper explosion"),
    SHOOT(new Shoot(), false, "{0} burned to death by {1}'s shoot"),
    GLIDE(new Glide(), false, ""),
    BLAST(new Blast(), false, "{0} burned to death by {1}'s blast"),
    FIREBALL(new Fireball(), false, "{0} died from {1}'s fireball"),
    PICKUP(new Pickup(), false, "{0} died from being picked up by {1}"),
    BLINK(new Blink(), false, ""),
    PORTAL(new Portal(), false, ""),
    MORPH(new Morph(), false, ""),
    POTION_BOMB(new PotionBomb(), false, "{0} died from {1}'s potionbomb"),
    BUFF(new Buff(), false, ""),
    FLY(new Fly(), false, ""),
    EXPLOSIVE_EGG(new ExplosiveEgg(), false, "{0} exploded by {1}'s explosive egg"),
    LAY_EGG(new LayEgg(), false, ""),
    DROP_EGG(new DropEgg(), false, "{0} died from {1}'s rotten egg"),

    //Dragon abilities
    BURN(new Burn(), false, "{0} burned to death from the dragons heat"),
    FIREFLY(new FireFly(), false, "{0} burned to death while the dragon flew by"),
    FIRE_BREATH(new FireBreath(), false, "{0} burned to death by the dragons fire breath"),
    GEYSER(new Geyser(), false, "{0} died from a hot geyser"),
    TOXIC_RAIN(new ToxicRain(), false, "{0} didn't survive the toxic rain"),
    WATER_BUBBLE(new WaterBubble(), false,"{0} drowned in a water bubble"),
    WIND(new Wind(), false, "{0} was blown away by wind and died"),
    WINDSTORM(new WindStorm(), false, "{0} died during a wind storm"),
    AIRSHIELD(new AirShield(), false, "{0} died from a reflected arrow by the dragon"),

    //Dwarf bonus abilities
    LEAP(new Leap(), true, "{0} died while trying to leap away"),
    TORRENT(new Torrent(), true, "{0} died from {1}'s water torrent"),
    LAND_MINE(new Landmine(), true, "{0} was blown to pieces by {1}'s mine"),
    NET(new Net(), true, ""),
    CAMOUFLAGE(new Camouflage(), true, "");


    //------------------------------
    //END OF ABILITIES
    //---


    private BaseAbility abilityClass;
    private boolean dwarfBonus = false;
    private String deathMsg;

    Ability(BaseAbility abilityClass, boolean dwarfBonus, String deathMsg) {
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.dwarfBonus = dwarfBonus;
        this.deathMsg = deathMsg;
    }

    public BaseAbility getAbilityClass() {
        return abilityClass;
    }

    public boolean isDwarfBonus() {
        return dwarfBonus;
    }

    public String getDeathMsg() {
        return deathMsg;
    }

    //Get class by name or alias and return null if no class was found.
    public static Ability fromString(String name) {
        //First check by name.
        for (Ability c : values()) {
            if (c.toString().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

}
