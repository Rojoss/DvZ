package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class PluginCfg extends EasyConfig {

    public int XP_NEEDED_TO_LVL = 500;
    public String DEFAULT_WORD = "world";
    public int DWARF_CLASS_COUNT = 2;
    public Double MONSTER_PERCENTAGE_MIN = 0.05;
    public Double MONSTER_PERCENTAGE_MAX = 0.15;
    public int WEB_REMOVAL_TIME = 100;

    public int SHRINE__BLOCK_HP = 1000;
    public int SHRINE__DAMAGE_PER_HIT = 5;

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
