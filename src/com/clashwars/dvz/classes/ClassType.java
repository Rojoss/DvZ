package com.clashwars.dvz.classes;

import org.bukkit.ChatColor;

public enum ClassType {
    BASE(ChatColor.GRAY),
    DWARF(ChatColor.GOLD),
    MONSTER(ChatColor.DARK_RED),
    DRAGON(ChatColor.DARK_PURPLE);

    private ChatColor color;

    ClassType(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public static ClassType fromString(String name) {
        for (ClassType c : values()) {
            if (c.toString().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }
}
