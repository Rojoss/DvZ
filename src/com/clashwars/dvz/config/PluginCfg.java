package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class PluginCfg extends EasyConfig {

    public String SQL_PASS = "SECRET";

    public int XP_NEEDED_TO_LVL = 500;
    public String DEFAULT_WORD = "world";
    public int DWARF_CLASS_COUNT = 2;
    public int PORTAL_TYPES = 1;
    public Double MONSTER_PERCENTAGE_MIN = 0.05;
    public Double MONSTER_PERCENTAGE_MAX = 0.15;

    public int SHRINE__BLOCK_HP = 1000;
    public int SHRINE__DAMAGE_PER_HIT = 5;

    public int CAMP_DELAY_TICKS = 20;
    public int CAMP_WARN_RANGE = 75;
    public int CAMP_RANGE = 50;

    public PluginCfg(String fileName) {
        this.setFile(fileName);
    }

    public World getDefaultWorld() {
        if (Bukkit.getWorlds().contains(DEFAULT_WORD)) {
            return Bukkit.getWorld(DEFAULT_WORD);
        } else {
            return Bukkit.getWorlds().get(0);
        }
    }
}
