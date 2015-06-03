package com.clashwars.dvz.util;

import org.bukkit.Material;
import org.bukkit.Sound;

public enum CWSound {
    CAVE(Sound.AMBIENCE_CAVE, SoundCat.AMBIENCE, Material.STONE, 0),
    RAIN(Sound.AMBIENCE_RAIN, SoundCat.AMBIENCE, Material.POTION, 0),
    THUNDER(Sound.AMBIENCE_THUNDER, SoundCat.AMBIENCE, Material.BLAZE_ROD, 0),
    ANVIL_BREAK(Sound.ANVIL_BREAK, SoundCat.MISC, Material.ANVIL, 0),
    ANVIL_LAND(Sound.ANVIL_LAND, SoundCat.MISC, Material.ANVIL, 0),
    ANVIL_USE(Sound.ANVIL_USE, SoundCat.MISC, Material.ANVIL, 0),
    ARROW_HIT(Sound.ARROW_HIT, SoundCat.MISC, Material.ARROW, 0),
    BAT_DEATH(Sound.BAT_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 65),
    BAT_HURT(Sound.BAT_HURT, SoundCat.MOB, Material.MONSTER_EGG, 65),
    BAT_IDLE(Sound.BAT_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 65),
    BAT_LOOP(Sound.BAT_LOOP, SoundCat.MOB, Material.MONSTER_EGG, 65),
    BAT_TAKEOFF(Sound.BAT_TAKEOFF, SoundCat.MOB, Material.MONSTER_EGG, 65),
    BLAZE_BREATH(Sound.BLAZE_BREATH, SoundCat.MOB, Material.MONSTER_EGG, 61),
    BLAZE_DEATH(Sound.BLAZE_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 61),
    BLAZE_HIT(Sound.BLAZE_HIT, SoundCat.MOB, Material.MONSTER_EGG, 61),
    BURP(Sound.BURP, SoundCat.MISC, Material.POTION, 0),
    CAT_HISS(Sound.CAT_HISS, SoundCat.MOB, Material.MONSTER_EGG, 98),
    CAT_HIT(Sound.CAT_HIT, SoundCat.MOB, Material.MONSTER_EGG, 98),
    CAT_MEOW(Sound.CAT_MEOW, SoundCat.MOB, Material.MONSTER_EGG, 98),
    CAT_PURR(Sound.CAT_PURR, SoundCat.MOB, Material.MONSTER_EGG, 98),
    CAT_PURREOW(Sound.CAT_PURREOW, SoundCat.MOB, Material.MONSTER_EGG, 98),
    CHEST_CLOSE(Sound.CHEST_CLOSE, SoundCat.MISC, Material.CHEST, 0),
    CHEST_OPEN(Sound.CHEST_OPEN, SoundCat.MISC, Material.CHEST, 0),
    CHICKEN_EGG_POP(Sound.CHICKEN_EGG_POP, SoundCat.MOB, Material.EGG, 0),
    CHICKEN_HURT(Sound.CHICKEN_HURT, SoundCat.MOB, Material.MONSTER_EGG, 93),
    CHICKEN_IDLE(Sound.CHICKEN_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 93),
    CHICKEN_WALK(Sound.CHICKEN_WALK, SoundCat.MOB, Material.MONSTER_EGG, 93),
    CLICK(Sound.CLICK, SoundCat.MISC, Material.WOOD_BUTTON, 0),
    COW_HURT(Sound.COW_HURT, SoundCat.MOB, Material.MONSTER_EGG, 92),
    COW_IDLE(Sound.COW_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 92),
    COW_WALK(Sound.COW_WALK, SoundCat.MOB, Material.MONSTER_EGG, 92),
    CREEPER_DEATH(Sound.CREEPER_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 50),
    CREEPER_HISS(Sound.CREEPER_HISS, SoundCat.MOB, Material.MONSTER_EGG, 50),
    DIG_GRASS(Sound.DIG_GRASS, SoundCat.DIG, Material.GRASS, 0),
    DIG_GRAVEL(Sound.DIG_GRAVEL, SoundCat.DIG, Material.GRAVEL, 0),
    DIG_SAND(Sound.DIG_SAND, SoundCat.DIG, Material.SAND, 0),
    DIG_SNOW(Sound.DIG_SNOW, SoundCat.DIG, Material.SNOW, 0),
    DIG_STONE(Sound.DIG_STONE, SoundCat.DIG, Material.STONE, 0),
    DIG_WOOD(Sound.DIG_WOOD, SoundCat.DIG, Material.WOOD, 0),
    DIG_WOOL(Sound.DIG_WOOL, SoundCat.DIG, Material.WOOL, 0),
    DONKEY_ANGRY(Sound.DONKEY_ANGRY, SoundCat.MOB, Material.CHEST, 0),
    DONKEY_DEATH(Sound.DONKEY_DEATH, SoundCat.MOB, Material.CHEST, 0),
    DONKEY_HIT(Sound.DONKEY_HIT, SoundCat.MOB, Material.CHEST, 0),
    DONKEY_IDLE(Sound.DONKEY_IDLE, SoundCat.MOB, Material.CHEST, 0),
    DOOR_CLOSE(Sound.DOOR_CLOSE, SoundCat.MISC, Material.WOOD_DOOR, 0),
    DOOR_OPEN(Sound.DOOR_OPEN, SoundCat.MISC, Material.WOOD_DOOR, 0),
    DRINK(Sound.DRINK, SoundCat.MISC, Material.POTION, 0),
    EAT(Sound.EAT, SoundCat.MISC, Material.BREAD, 0),
    ENDERDRAGON_DEATH(Sound.ENDERDRAGON_DEATH, SoundCat.MOB, Material.DRAGON_EGG, 0),
    ENDERDRAGON_GROWL(Sound.ENDERDRAGON_GROWL, SoundCat.MOB, Material.DRAGON_EGG, 0),
    ENDERDRAGON_HIT(Sound.ENDERDRAGON_HIT, SoundCat.MOB, Material.DRAGON_EGG, 0),
    ENDERDRAGON_WINGS(Sound.ENDERDRAGON_WINGS, SoundCat.MOB, Material.DRAGON_EGG, 0),
    ENDERMAN_DEATH(Sound.ENDERMAN_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 58),
    ENDERMAN_HIT(Sound.ENDERMAN_HIT, SoundCat.MOB, Material.MONSTER_EGG, 58),
    ENDERMAN_IDLE(Sound.ENDERMAN_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 58),
    ENDERMAN_SCREAM(Sound.ENDERMAN_SCREAM, SoundCat.MOB, Material.MONSTER_EGG, 58),
    ENDERMAN_STARE(Sound.ENDERMAN_STARE, SoundCat.MOB, Material.MONSTER_EGG, 58),
    ENDERMAN_TELEPORT(Sound.ENDERMAN_TELEPORT, SoundCat.MOB, Material.MONSTER_EGG, 58),
    EXPLODE(Sound.EXPLODE, SoundCat.MISC, Material.TNT, 0),
    FALL_BIG(Sound.FALL_BIG, SoundCat.MISC, Material.SKULL_ITEM, 3),
    FALL_SMALL(Sound.FALL_SMALL, SoundCat.MISC, Material.SKULL_ITEM, 3),
    FIRE(Sound.FIRE, SoundCat.MISC, Material.BLAZE_POWDER, 0),
    FIRE_IGNITE(Sound.FIRE_IGNITE, SoundCat.MISC, Material.FLINT_AND_STEEL, 0),
    FIREWORK_BLAST(Sound.FIREWORK_BLAST, SoundCat.MISC, Material.FIREWORK, 0),
    FIREWORK_BLAST2(Sound.FIREWORK_BLAST2, SoundCat.MISC, Material.FIREWORK, 0),
    FIREWORK_LARGE_BLAST(Sound.FIREWORK_LARGE_BLAST, SoundCat.MISC, Material.FIREWORK, 0),
    FIREWORK_LARGE_BLAST2(Sound.FIREWORK_LARGE_BLAST2, SoundCat.MISC, Material.FIREWORK, 0),
    FIREWORK_LAUNCH(Sound.FIREWORK_LAUNCH, SoundCat.MISC, Material.FIREWORK, 0),
    FIREWORK_TWINKLE(Sound.FIREWORK_TWINKLE, SoundCat.MISC, Material.FIREWORK, 0),
    FIREWORK_TWINKLE2(Sound.FIREWORK_TWINKLE2, SoundCat.MISC, Material.FIREWORK, 0),
    FIZZ(Sound.FIZZ, SoundCat.MISC, Material.BLAZE_POWDER, 0),
    FUSE(Sound.FUSE, SoundCat.MISC, Material.TNT, 0),
    GHAST_CHARGE(Sound.GHAST_CHARGE, SoundCat.MOB, Material.MONSTER_EGG, 56),
    GHAST_DEATH(Sound.GHAST_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 56),
    GHAST_FIREBALL(Sound.GHAST_FIREBALL, SoundCat.MOB, Material.FIREBALL, 0),
    GHAST_MOAN(Sound.GHAST_MOAN, SoundCat.MOB, Material.MONSTER_EGG, 56),
    GHAST_SCREAM(Sound.GHAST_SCREAM, SoundCat.MOB, Material.MONSTER_EGG, 56),
    GHAST_SCREAM2(Sound.GHAST_SCREAM2, SoundCat.MOB, Material.MONSTER_EGG, 56),
    GLASS(Sound.GLASS, SoundCat.MISC, Material.GLASS, 0),
    HORSE_ANGRY(Sound.HORSE_ANGRY, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_ARMOR(Sound.HORSE_ARMOR, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_BREATHE(Sound.HORSE_BREATHE, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_DEATH(Sound.HORSE_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_GALLOP(Sound.HORSE_GALLOP, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_HIT(Sound.HORSE_HIT, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_IDLE(Sound.HORSE_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_JUMP(Sound.HORSE_JUMP, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_LAND(Sound.HORSE_LAND, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_SADDLE(Sound.HORSE_SADDLE, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_SKELETON_DEATH(Sound.HORSE_SKELETON_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_SKELETON_HIT(Sound.HORSE_SKELETON_HIT, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_SKELETON_IDLE(Sound.HORSE_SKELETON_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_SOFT(Sound.HORSE_SOFT, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_WOOD(Sound.HORSE_WOOD, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_ZOMBIE_DEATH(Sound.HORSE_ZOMBIE_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_ZOMBIE_HIT(Sound.HORSE_ZOMBIE_HIT, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HORSE_ZOMBIE_IDLE(Sound.HORSE_ZOMBIE_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 100),
    HURT_FLESH(Sound.HURT_FLESH, SoundCat.MISC, Material.ROTTEN_FLESH, 0),
    IRONGOLEM_DEATH(Sound.IRONGOLEM_DEATH, SoundCat.MOB, Material.IRON_BLOCK, 0),
    IRONGOLEM_HIT(Sound.IRONGOLEM_HIT, SoundCat.MOB, Material.IRON_BLOCK, 0),
    IRONGOLEM_THROW(Sound.IRONGOLEM_THROW, SoundCat.MOB, Material.IRON_BLOCK, 0),
    IRONGOLEM_WALK(Sound.IRONGOLEM_WALK, SoundCat.MOB, Material.IRON_BLOCK, 0),
    ITEM_BREAK(Sound.ITEM_BREAK, SoundCat.MISC, Material.DIAMOND_PICKAXE, 1500),
    ITEM_PICKUP(Sound.ITEM_PICKUP, SoundCat.MISC, Material.STICK, 0),
    LAVA(Sound.LAVA, SoundCat.MISC, Material.LAVA_BUCKET, 0),
    LAVA_POP(Sound.LAVA_POP, SoundCat.MISC, Material.LAVA_BUCKET, 0),
    LEVEL_UP(Sound.LEVEL_UP, SoundCat.MISC, Material.EXP_BOTTLE, 0),
    MAGMACUBE_JUMP(Sound.MAGMACUBE_JUMP, SoundCat.MOB, Material.MONSTER_EGG, 62),
    MAGMACUBE_WALK(Sound.MAGMACUBE_WALK, SoundCat.MOB, Material.MONSTER_EGG, 62),
    MAGMACUBE_WALK2(Sound.MAGMACUBE_WALK2, SoundCat.MOB, Material.MONSTER_EGG, 62),
    MINECART_BASE(Sound.MINECART_BASE, SoundCat.MISC, Material.MINECART, 0),
    MINECART_INSIDE(Sound.MINECART_INSIDE, SoundCat.MISC, Material.MINECART, 0),
    NOTE_BASS(Sound.NOTE_BASS, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    NOTE_BASS_DRUM(Sound.NOTE_BASS_DRUM, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    NOTE_BASS_GUITAR(Sound.NOTE_BASS_GUITAR, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    NOTE_PIANO(Sound.NOTE_PIANO, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    NOTE_PLING(Sound.NOTE_PLING, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    NOTE_SNARE_DRUM(Sound.NOTE_SNARE_DRUM, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    NOTE_STICKS(Sound.NOTE_STICKS, SoundCat.NOTE, Material.NOTE_BLOCK, 0),
    ORB_PICKUP(Sound.ORB_PICKUP, SoundCat.MISC, Material.EXP_BOTTLE, 0),
    PIG_DEATH(Sound.PIG_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 90),
    PIG_IDLE(Sound.PIG_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 90),
    PIG_WALK(Sound.PIG_WALK, SoundCat.MOB, Material.MONSTER_EGG, 90),
    PISTON_EXTEND(Sound.PISTON_EXTEND, SoundCat.MISC, Material.PISTON_BASE, 0),
    PISTON_RETRACT(Sound.PISTON_RETRACT, SoundCat.MISC, Material.PISTON_BASE, 0),
    PORTAL(Sound.PORTAL, SoundCat.MISC, Material.ENDER_PORTAL_FRAME, 0),
    PORTAL_TRAVEL(Sound.PORTAL_TRAVEL, SoundCat.MISC, Material.ENDER_PORTAL_FRAME, 0),
    PORTAL_TRIGGER(Sound.PORTAL_TRIGGER, SoundCat.MISC, Material.ENDER_PORTAL_FRAME, 0),
    SHEEP_IDLE(Sound.SHEEP_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 91),
    SHEEP_SHEAR(Sound.SHEEP_SHEAR, SoundCat.MOB, Material.MONSTER_EGG, 91),
    SHEEP_WALK(Sound.SHEEP_WALK, SoundCat.MOB, Material.MONSTER_EGG, 91),
    SHOOT_ARROW(Sound.SHOOT_ARROW, SoundCat.MISC, Material.ARROW, 0),
    SILVERFISH_HIT(Sound.SILVERFISH_HIT, SoundCat.MOB, Material.MONSTER_EGG, 60),
    SILVERFISH_IDLE(Sound.SILVERFISH_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 60),
    SILVERFISH_KILL(Sound.SILVERFISH_KILL, SoundCat.MOB, Material.MONSTER_EGG, 60),
    SILVERFISH_WALK(Sound.SILVERFISH_WALK, SoundCat.MOB, Material.MONSTER_EGG, 60),
    SKELETON_DEATH(Sound.SKELETON_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 51),
    SKELETON_HURT(Sound.SKELETON_HURT, SoundCat.MOB, Material.MONSTER_EGG, 51),
    SKELETON_IDLE(Sound.SKELETON_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 51),
    SKELETON_WALK(Sound.SKELETON_WALK, SoundCat.MOB, Material.MONSTER_EGG, 51),
    SLIME_ATTACK(Sound.SLIME_ATTACK, SoundCat.MOB, Material.MONSTER_EGG, 55),
    SLIME_WALK(Sound.SLIME_WALK, SoundCat.MOB, Material.MONSTER_EGG, 55),
    SLIME_WALK2(Sound.SLIME_WALK2, SoundCat.MOB, Material.MONSTER_EGG, 55),
    SPIDER_DEATH(Sound.SPIDER_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 52),
    SPIDER_IDLE(Sound.SPIDER_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 52),
    SPIDER_WALK(Sound.SPIDER_WALK, SoundCat.MOB, Material.MONSTER_EGG, 52),
    SPLASH(Sound.SPLASH, SoundCat.MISC, Material.POTION, 16385),
    SPLASH2(Sound.SPLASH2, SoundCat.MISC, Material.POTION, 16386),
    STEP_GRASS(Sound.STEP_GRASS, SoundCat.STEP, Material.GRASS, 0),
    STEP_GRAVEL(Sound.STEP_GRAVEL, SoundCat.STEP, Material.GRAVEL, 0),
    STEP_LADDER(Sound.STEP_LADDER, SoundCat.STEP, Material.LADDER, 0),
    STEP_SAND(Sound.STEP_SAND, SoundCat.STEP, Material.SAND, 0),
    STEP_SNOW(Sound.STEP_SNOW, SoundCat.STEP, Material.SNOW, 0),
    STEP_STONE(Sound.STEP_STONE, SoundCat.STEP, Material.STONE, 0),
    STEP_WOOD(Sound.STEP_WOOD, SoundCat.STEP, Material.WOOD, 0),
    STEP_WOOL(Sound.STEP_WOOL, SoundCat.STEP, Material.WOOL, 0),
    SUCCESSFUL_HIT(Sound.SUCCESSFUL_HIT, SoundCat.MISC, Material.ARROW, 0),
    SWIM(Sound.SWIM, SoundCat.MISC, Material.WATER_BUCKET, 0),
    VILLAGER_DEATH(Sound.VILLAGER_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 120),
    VILLAGER_HAGGLE(Sound.VILLAGER_HAGGLE, SoundCat.MOB, Material.MONSTER_EGG, 120),
    VILLAGER_HIT(Sound.VILLAGER_HIT, SoundCat.MOB, Material.MONSTER_EGG, 120),
    VILLAGER_IDLE(Sound.VILLAGER_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 120),
    VILLAGER_NO(Sound.VILLAGER_NO, SoundCat.MOB, Material.MONSTER_EGG, 120),
    VILLAGER_YES(Sound.VILLAGER_YES, SoundCat.MOB, Material.MONSTER_EGG, 120),
    WATER(Sound.WATER, SoundCat.MISC, Material.WATER_BUCKET, 0),
    WITHER_DEATH(Sound.WITHER_DEATH, SoundCat.MOB, Material.SKULL_ITEM, 1),
    WITHER_HURT(Sound.WITHER_HURT, SoundCat.MOB, Material.SKULL_ITEM, 1),
    WITHER_IDLE(Sound.WITHER_IDLE, SoundCat.MOB, Material.SKULL_ITEM, 1),
    WITHER_SHOOT(Sound.WITHER_SHOOT, SoundCat.MOB, Material.SKULL_ITEM, 1),
    WITHER_SPAWN(Sound.WITHER_SPAWN, SoundCat.MOB, Material.SKULL_ITEM, 1),
    WOLF_BARK(Sound.WOLF_BARK, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_DEATH(Sound.WOLF_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_GROWL(Sound.WOLF_GROWL, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_HOWL(Sound.WOLF_HOWL, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_HURT(Sound.WOLF_HURT, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_PANT(Sound.WOLF_PANT, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_SHAKE(Sound.WOLF_SHAKE, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_WALK(Sound.WOLF_WALK, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOLF_WHINE(Sound.WOLF_WHINE, SoundCat.MOB, Material.MONSTER_EGG, 95),
    WOOD_CLICK(Sound.WOOD_CLICK, SoundCat.MISC, Material.WOOD_BUTTON, 0),
    ZOMBIE_DEATH(Sound.ZOMBIE_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_HURT(Sound.ZOMBIE_HURT, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_IDLE(Sound.ZOMBIE_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_INFECT(Sound.ZOMBIE_INFECT, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_METAL(Sound.ZOMBIE_METAL, SoundCat.MOB, Material.IRON_DOOR, 0),
    ZOMBIE_PIG_ANGRY(Sound.ZOMBIE_PIG_ANGRY, SoundCat.MOB, Material.MONSTER_EGG, 57),
    ZOMBIE_PIG_DEATH(Sound.ZOMBIE_PIG_DEATH, SoundCat.MOB, Material.MONSTER_EGG, 57),
    ZOMBIE_PIG_HURT(Sound.ZOMBIE_PIG_HURT, SoundCat.MOB, Material.MONSTER_EGG, 57),
    ZOMBIE_PIG_IDLE(Sound.ZOMBIE_PIG_IDLE, SoundCat.MOB, Material.MONSTER_EGG, 57),
    ZOMBIE_REMEDY(Sound.ZOMBIE_REMEDY, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_UNFECT(Sound.ZOMBIE_UNFECT, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_WALK(Sound.ZOMBIE_WALK, SoundCat.MOB, Material.MONSTER_EGG, 54),
    ZOMBIE_WOOD(Sound.ZOMBIE_WOOD, SoundCat.MOB, Material.WOOD_DOOR, 0),
    ZOMBIE_WOODBREAK(Sound.ZOMBIE_WOODBREAK, SoundCat.MOB, Material.WOOD_DOOR, 0);

    public Sound sound;
    public SoundCat category;
    public Material icon;
    public byte data;

    CWSound(Sound sound, SoundCat category, Material icon, int data) {
        this.sound = sound;
        this.category = category;
        this.icon = icon;
        this.data = (byte)data;
    }

    public static CWSound fromString(String name) {
        for (CWSound c : values()) {
            if (c.toString().toLowerCase().replace("_","").equalsIgnoreCase(name.toLowerCase().replace("_", ""))) {
                return c;
            }
        }
        return null;
    }

    public enum SoundCat {
        AMBIENCE(Material.STONE),
        MISC(Material.STICK),
        MOB(Material.MONSTER_EGG),
        STEP(Material.WOOD_STEP),
        DIG(Material.DIAMOND_SPADE),
        NOTE(Material.NOTE_BLOCK);

        public Material icon;

        SoundCat(Material icon) {
            this.icon = icon;
        }

        public static SoundCat fromString(String name) {
            for (SoundCat c : values()) {
                if (c.toString().toLowerCase().replace("_","").equalsIgnoreCase(name.toLowerCase().replace("_",""))) {
                    return c;
                }
            }
            return null;
        }
    }
}

