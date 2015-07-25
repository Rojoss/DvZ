package com.clashwars.dvz.abilities;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.SwapType;
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
import com.clashwars.dvz.abilities.monsters.irongolem.FlowerTrail;
import com.clashwars.dvz.abilities.monsters.irongolem.GroundPound;
import com.clashwars.dvz.abilities.monsters.irongolem.Smash;
import com.clashwars.dvz.abilities.monsters.irongolem.Toss;
import com.clashwars.dvz.abilities.monsters.silverfish.InfectStone;
import com.clashwars.dvz.abilities.monsters.silverfish.Infest;
import com.clashwars.dvz.abilities.monsters.silverfish.Roar;
import com.clashwars.dvz.abilities.monsters.skeleton.Mount;
import com.clashwars.dvz.abilities.monsters.skeleton.Rapidfire;
import com.clashwars.dvz.abilities.monsters.spider.Poison;
import com.clashwars.dvz.abilities.monsters.spider.PoisonAttack;
import com.clashwars.dvz.abilities.monsters.spider.Web;
import com.clashwars.dvz.abilities.monsters.witchvillager.Buff;
import com.clashwars.dvz.abilities.monsters.witchvillager.Morph;
import com.clashwars.dvz.abilities.monsters.witchvillager.PotionBomb;
import com.clashwars.dvz.abilities.monsters.zombie.Infect;
import com.clashwars.dvz.abilities.monsters.zombie.Rush;

import java.util.HashMap;
import java.util.LinkedHashMap;

public enum Ability {
    BASE(new BaseAbility(), SwapType.NONE, ""),

    //Dwarf abilities
    HEAL_POTION(new HealPotion(), SwapType.POTION, ""),
    SPEED_POTION(new SpeedPotion(), SwapType.POTION, ""),
    BUILDING_BRICK(new BuildingBrick(), SwapType.NONE, ""),
    BUILDING_BLOCK(new BuildingBlock(), SwapType.NONE, ""),
    REINFORCE(new Reinforce(), SwapType.NONE, ""),
    SUMMON_STONE(new SummonStone(), SwapType.NONE, ""),
    HORN(new Horn(), SwapType.DWARF_ABILITY, ""),

    //Monster abilities
    SUICIDE(new Suicide(), SwapType.NONE, "{0} committed suicide"),
    HAMMER(new Hammer(), SwapType.NONE, ""),
    TELEPORT_PORTAL(new TeleportPortal(), SwapType.NONE, ""),
    INFECT(new Infect(), SwapType.NONE, ""),
    RUSH(new Rush(), SwapType.NONE, ""),
    RAPIDFIRE(new Rapidfire(), SwapType.NONE, ""),
    MOUNT(new Mount(), SwapType.NONE, ""),
    POISON_ATTACK(new PoisonAttack(), SwapType.NONE, "{0} died from {1}'s poison attack"),
    POISON(new Poison(), SwapType.NONE, "{0} died from {1}'s poison cloud"),
    WEB(new Web(), SwapType.NONE, ""),
    EXPLODE(new Explode(), SwapType.NONE, "{0} died from {1}'s creeper explosion"),
    SHOOT(new Shoot(), SwapType.NONE, "{0} burned to death by {1}'s shoot"),
    GLIDE(new Glide(), SwapType.NONE, ""),
    BLAST(new Blast(), SwapType.NONE, "{0} burned to death by {1}'s blast"),
    FIREBALL(new Fireball(), SwapType.NONE, "{0} died from {1}'s fireball"),
    PICKUP(new Pickup(), SwapType.NONE, "{0} died from being picked up by {1}"),
    BLINK(new Blink(), SwapType.NONE, ""),
    PORTAL(new Portal(), SwapType.NONE, ""),
    MORPH(new Morph(), SwapType.NONE, ""),
    POTION_BOMB(new PotionBomb(), SwapType.NONE, "{0} died from {1}'s potionbomb"),
    BUFF(new Buff(), SwapType.NONE, ""),
    FLY(new Fly(), SwapType.NONE, ""),
    EXPLOSIVE_EGG(new ExplosiveEgg(), SwapType.NONE, "{0} exploded by {1}'s explosive egg"),
    LAY_EGG(new LayEgg(), SwapType.NONE, ""),
    DROP_EGG(new DropEgg(), SwapType.NONE, "{0} died from {1}'s rotten egg"),
    INFECT_STONE(new InfectStone(), SwapType.NONE, ""),
    ROAR(new Roar(), SwapType.NONE, ""),
    INFEST(new Infest(), SwapType.NONE, ""),
    TOSS(new Toss(), SwapType.NONE, "{0} was tossed by {1}"),
    SMASH(new Smash(), SwapType.NONE, "{0} died from {1}'s smashed rock"),
    GROUND_POUND(new GroundPound(), SwapType.NONE, "{0} died from {1}'s ground pound"),
    FLOWER_TRAIL(new FlowerTrail(), SwapType.NONE, ""),

    //Dragon abilities
    BURN(new Burn(), SwapType.NONE, "{0} burned to death from the dragons heat"),
    FIREFLY(new FireFly(), SwapType.NONE, "{0} burned to death while the dragon flew by"),
    FIRE_BREATH(new FireBreath(), SwapType.NONE, "{0} burned to death by the dragons fire breath"),
    GEYSER(new Geyser(), SwapType.NONE, "{0} died from a hot geyser"),
    TOXIC_RAIN(new ToxicRain(), SwapType.NONE, "{0} didn't survive the toxic rain"),
    WATER_BUBBLE(new WaterBubble(), SwapType.NONE,"{0} drowned in a water bubble"),
    WIND(new Wind(), SwapType.NONE, "{0} was blown away by wind and died"),
    WINDSTORM(new WindStorm(), SwapType.NONE, "{0} died during a wind storm"),
    AIRSHIELD(new AirShield(), SwapType.NONE, "{0} died from a reflected arrow by the dragon"),

    //Dwarf bonus abilities
    LEAP(new Leap(), SwapType.DWARF_ABILITY, "{0} died while trying to leap away"),
    TORRENT(new Torrent(), SwapType.DWARF_ABILITY, "{0} died from {1}'s water torrent"),
    LAND_MINE(new Landmine(), SwapType.DWARF_ABILITY, "{0} was blown to pieces by {1}'s mine"),
    NET(new Net(), SwapType.DWARF_ABILITY, ""),
    //FIRESTAFF(new FireStaff(), SwapType.DWARF_ABILITY, "{0} burnt to a crisp by {1}'s Firestaff"),
    CAMOUFLAGE(new Camouflage(), SwapType.DWARF_ABILITY, "");



    //------------------------------
    //END OF ABILITIES
    //---


    private BaseAbility abilityClass;
    private SwapType swapType;
    private String deathMsg;
    private static HashMap<SwapType, LinkedHashMap<Ability, CWItem>> swapItems = new HashMap<>();

    Ability(BaseAbility abilityClass, SwapType swapType, String deathMsg) {
        this.abilityClass = abilityClass;
        abilityClass.setAbility(this);
        this.swapType = swapType;
        this.deathMsg = deathMsg;
    }

    public BaseAbility getAbilityClass() {
        return abilityClass;
    }

    public SwapType getSwapType(){
        return swapType;
    }

    public static LinkedHashMap<Ability, CWItem> getSwapItems(SwapType swapType) {
        if (!swapItems.containsKey(swapType)) {
            LinkedHashMap<Ability, CWItem> items = new LinkedHashMap<Ability, CWItem>();
            for (Ability ability : values()) {
                if (ability.swapType == swapType && ability.getAbilityClass().getCastItem() != null) {
                    items.put(ability, ability.getAbilityClass().getCastItem());
                }
            }
            swapItems.put(swapType, items);
        }
        return swapItems.get(swapType);
    }

    public String getDeathMsg() {
        return deathMsg;
    }

    //Get class by name or alias and return null if no class was found.
    public static Ability fromString(String name) {
        name = name.toLowerCase().replace("_","");
        //First check by name.
        for (Ability c : values()) {
            if (c.toString().toLowerCase().replace("_", "").equals(name)) {
                return c;
            }
        }
        return null;
    }

}
