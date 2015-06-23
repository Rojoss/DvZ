package com.clashwars.dvz.stats;

public enum StatType {
    GENERAL_TIME_PLAYED(1),
    GENERAL_DEATHS_BY_DRAGON(2),
    GENERAL_TOTAL_DEATHS(5),
    GENERAL_TIMES_TELEPORTED(6),
    GENERAL_TIMES_SWITCHED(7),
    GENERAL_ARROWS_SHOT(8),
    GENERAL_ARROWS_HIT(9),
    GENERAL_BOW_ACCURACY(10),
    GENERAL_CHAT_MESSAGES(11),
    GENERAL_TIMES_COMPLETED_PARKOUR(12),
    GENERAL_TOTAL_KILLS(25),
    GENERAL_KDR(26),
    GENERAL_GAME_TIME(78),

    DWARF_DEATHS(3),
    DWARF_MONSTER_KILLS(13),
    DWARF_DRAGON_KILLS(14),
    DWARF_DRAGON_DAMAGE(15),
    DWARF_DAMAGE_DEALT(16),
    DWARF_DAMAGE_TAKEN(17),
    DWARF_LEVELS_EARNED(18),
    DWARF_XP_EARNED(19),
    DWARF_BREAD_EATEN(20),
    DWARF_HEALTH_POTS_USED(21),
    DWARF_SPEED_POTS_USED(22),
    DWARF_PORTALS_DESTROYED(23),
    DWARF_BOMBS_DESTROYED(24),
    DWARF_MOST_PICKED(77),

    MONSTER_DEATHS(4),
    MONSTER_DWARF_KILLS(27),
    MONSTER_SHRINE_DAMAGE(28),
    MONSTER_TIMES_BUFFED(29),
    MONSTER_PORTALS_CREATED(30),
    MONSTER_BOMBS_PLACED(31),
    ZOMBIE_PICKS(32),
    SKELETON_PICKS(33),
    SPIDER_PICKS(34),
    CREEPER_PICKS(35),
    BLAZE_PICKS(36),
    ENDERMAN_PICKS(37),
    VILLAGER_PICKS(38),
    CHICKEN_PICKS(39),
    MONSTER_MOST_PICKED_CLASS(40),
    MONSTER_TOTAL_PICKS(41),

    BAKER_TIMES_PICKED(42),
    BAKER_BREAD_BAKED(43),
    BAKER_WHEAT_HARVESTED(44),
    BAKER_FLOUR_GRINDED(45),

    ALCHEMIST_TIMES_PICKED(46),
    ALCHEMIST_SPEED_POTS(47),
    ALCHEMIST_HEALTH_POTS(48),
    ALCHEMIST_MELONS_COLLECTED(49),
    ALCHEMIST_SUGAR_COLLECTED(50),
    ALCHEMIST_CAULDRONS_EMPTIED(51),

    MINER_TIMES_PICKED(52),
    MINER_WEAPONS_CRAFTED(53),
    MINER_DIAMONDS_MINED(54),
    MINER_GOLD_MINED(55),
    MINER_IRON_MINED(56),
    MINER_STONE_MINED(57),

    FLETCHER_TIMES_PICKED(58),
    FLETCHER_BOWS_CRAFTED(59),
    FLETCHER_ARROWS_CRAFTED(60),
    FLETCHER_GRAVEL_DUG(61),
    FLETCHER_FLINT_COLLECTED(62),
    FLETCHER_CHICKENS_KILLED(63),
    FLETCHER_FEATHERS_COLLECTED(64),

    TAILOR_TIMES_PICKED(65),
    TAILOR_ARMOR_CRAFTED(66),
    TAILOR_SHEEP_SHEARED(67),
    TAILOR_WHITE_FLOWERS_COLLECTED(68),
    TAILOR_BLUE_FLOWERS_COLLECTED(69),

    BUILDER_TIMES_PICKED(70),
    BUILDER_STONE_PLACED(71),
    BUILDER_STONE_SUMMONED(72),
    BUILDER_BRICK_USED(73),
    BUILDER_BLOCK_USED(74),
    BUILDER_STONE_REINFORCED(75);


    public int id;

    StatType(int id) {
        this.id = id;
    }
}
