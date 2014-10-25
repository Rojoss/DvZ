package com.clashwars.dvz;

import com.clashwars.cwcore.CWCore;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DvZ extends JavaPlugin {
    private static DvZ instance;
    private CWCore cwcore;

    private final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        log("disabled");
    }

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = getServer().getPluginManager().getPlugin("CWCore");
        if (plugin == null || !(plugin instanceof CWCore)) {
            log("CWCore dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        cwcore = (CWCore) plugin;

        //TODO: Configs

        //TODO: Managers

        //TODO: Events

        //TODO: Commands

        log("loaded successfully");
    }



    public void log(Object msg) {
        log.info("[CWBounty " + getDescription().getVersion() + "] " + msg.toString());
    }

    public static DvZ inst() {
        return instance;
    }
}
