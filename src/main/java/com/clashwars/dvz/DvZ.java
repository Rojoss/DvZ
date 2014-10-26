package com.clashwars.dvz;

import com.clashwars.cwcore.CWCore;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassManager;
import com.clashwars.dvz.classes.DvZClass;
import com.clashwars.dvz.classes.dwarves.DwarfClass;
import com.clashwars.dvz.classes.dwarves.Miner;
import com.clashwars.dvz.commands.Commands;
import com.clashwars.dvz.config.ClassesCfg;
import com.clashwars.dvz.config.PluginCfg;
import com.clashwars.dvz.events.MainEvents;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class DvZ extends JavaPlugin {
    private static DvZ instance;
    private CWCore cwcore;

    private PluginCfg cfg;
    private ClassesCfg classesCfg;

    private Commands cmds;

    private ClassManager cm;

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

        cfg = new PluginCfg("plugins/DvZ/DvZ.yml");
        cfg.load();

        classesCfg = new ClassesCfg("plugins/DvZ/Classes.yml");
        classesCfg.load();

        ClassManager cm = new ClassManager(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);

        cmds = new Commands(this);

        log("loaded successfully");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }



    public void log(Object msg) {
        log.info("[DvZ " + getDescription().getVersion() + "] " + msg.toString());
    }

    public static DvZ inst() {
        return instance;
    }


    /* Getters & Setters */
    public PluginCfg getCfg() {
        return cfg;
    }

    public ClassesCfg getClassesCfg() {
        return classesCfg;
    }

    public ClassManager getCM() {
        return cm;
    }
}
