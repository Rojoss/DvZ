package com.clashwars.dvz;

import org.bukkit.ChatColor;

public enum GameState {
    CLOSED("Closed", ChatColor.RED),
    OPENED("Opened", ChatColor.GREEN),
    DAY_ONE("(Dwarves) First day", ChatColor.DARK_GRAY),
    NIGHT_ONE("(Dwarves) First night", ChatColor.DARK_GRAY),
    DAY_TWO("(Dwarves) Second day", ChatColor.DARK_GRAY),
    DRAGON("Dragon", ChatColor.DARK_PURPLE),
    MONSTERS("Monsters", ChatColor.RED),
    MONSTERS_WALL("Monsters Wall", ChatColor.DARK_RED),
    ENDED("Ended", ChatColor.GRAY);

    private String name;
    private ChatColor color;

    GameState(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

}
