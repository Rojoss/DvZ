package com.clashwars.dvz;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.effect.EffectManager;
import com.clashwars.cwcore.scoreboard.CWBoard;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassManager;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.dwarves.Builder;
import com.clashwars.dvz.commands.Commands;
import com.clashwars.dvz.config.*;
import com.clashwars.dvz.events.MainEvents;
import com.clashwars.dvz.events.ProtectEvents;
import com.clashwars.dvz.events.WorkShopEvents;
import com.clashwars.dvz.maps.MapManager;
import com.clashwars.dvz.player.PlayerManager;
import com.clashwars.dvz.runnables.AntiCamp;
import com.clashwars.dvz.runnables.GameRunnable;
import com.clashwars.dvz.structures.internal.StructureType;
import com.clashwars.dvz.util.ItemMenu;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.gson.Gson;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.util.logging.Logger;

public class DvZ extends JavaPlugin {

    private static DvZ instance;
    private CWCore cwcore;
    private Permission permission;
    private Gson gson = new Gson();
    private CWBoard cwb;

    private PluginCfg cfg;
    private GameCfg gameCfg;
    private MapCfg mapCfg;
    private StructureCfg strucCfg;
    private ClassesCfg classesCfg;
    private AbilityCfg abilityCfg;
    private PlayerCfg playerCfg;
    private WorkShopCfg wsCfg;

    private Commands cmds;

    private EffectManager em;
    private GameManager gm;
    private MapManager mm;
    private ClassManager cm;
    private PlayerManager pm;

    private final Logger log = Logger.getLogger("Minecraft");

    public Set<UUID> entities = new HashSet<UUID>();
    private List<Material> destroyableBlocks = Arrays.asList(new Material[] {Material.SMOOTH_BRICK, Material.STONE, Material.GRASS, Material.COBBLESTONE,
        Material.QUARTZ_BLOCK, Material.WOOD, Material.LOG, Material.LOG_2, Material.BRICK, Material.DIRT});

    @Override
    public void onDisable() {
        //Kill all mobs
        for (Entity entity : getMM().getActiveMap().getWorld().getEntities()) {
            if (entities.contains(entity.getUniqueId())) {
                entity.remove();
            }
        }

        em.dispose();

        Bukkit.getScheduler().cancelTasks(this);
        getPM().savePlayers();

        abilityCfg.load();
        abilityCfg.save();

        //Clean up holograms
        for (Hologram hologram : HolographicDisplaysAPI.getHolograms(this)) {
            hologram.delete();
        }

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
        cwcore = (CWCore)plugin;

        permission = cwcore.getDM().getPermissions();
        if (permission == null) {
            log("Vault permissions couldn't be loaded. It will still work but certain featured might not work properly.");
        }

        em = new EffectManager(cwcore);

        cfg = new PluginCfg("plugins/DvZ/DvZ.yml");
        cfg.load();

        gameCfg = new GameCfg("plugins/DvZ/data/Game.yml");
        gameCfg.load();

        mapCfg = new MapCfg("plugins/DvZ/maps/Maps.yml");
        mapCfg.load();

        strucCfg = new StructureCfg("plugins/DvZ/Structures.yml");
        strucCfg.load();

        classesCfg = new ClassesCfg("plugins/DvZ/Classes.yml");
        classesCfg.load();

        abilityCfg = new AbilityCfg("plugins/DvZ/Abilities.yml");

        playerCfg = new PlayerCfg("plugins/DvZ/data/Players.yml");
        playerCfg.load();

        wsCfg = new WorkShopCfg("plugins/DvZ/data/Workshops.yml");
        wsCfg.load();

        mm = new MapManager(this);
        gm = new GameManager(this);
        cm = new ClassManager(this);
        pm = new PlayerManager(this);

        registerEvents();

        cmds = new Commands(this);

        startRunnables();
        setupBoard();

        log("loaded successfully");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);
        pm.registerEvents(new WorkShopEvents(this), this);
        for (Ability a : Ability.values()) {
            pm.registerEvents(a.getAbilityClass(), this);
        }
        for (DvzClass c : DvzClass.values()) {
            pm.registerEvents(c.getClassClass(), this);
        }
        for (StructureType s : StructureType.values()) {
            pm.registerEvents(s.getStrucClass(), this);
        }
        pm.registerEvents(new ItemMenu.Events(), this);
        pm.registerEvents(new ProtectEvents(this), this);
    }

    private void startRunnables() {
        new GameRunnable(this).runTaskTimer(this, 0, 2);
        new AntiCamp(this).runTaskTimer(this, 40, getCfg().CAMP_DELAY_TICKS);
    }

    private void setupBoard() {
        cwb = CWBoard.get("dvz");
        cwb.init(true);
        cwb.addTeam("builder", "&e", "", "&eBuilder", true, true, NameTagVisibility.ALWAYS);
        cwb.addTeam("miner", "&8", "", "&8Minrt", true, true, NameTagVisibility.ALWAYS);
        cwb.addTeam("tailor", "&3", "", "&3Tailor", true, true, NameTagVisibility.ALWAYS);
        cwb.addTeam("fletcher", "&2", "", "&2Fletcher", true, true, NameTagVisibility.ALWAYS);
        cwb.addTeam("alchemist", "&5", "", "&5Alchemist", true, true, NameTagVisibility.ALWAYS);
        cwb.addTeam("monster", "&c", "", "&cMonster", false, true, NameTagVisibility.ALWAYS);
        cwb.addTeam("dragonslayer", "&d", "", "&dDragon-Slayer", true, true, NameTagVisibility.ALWAYS);
        cwb.show();
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
    public Gson getGson() {
        return gson;
    }

    public CWBoard getBoard() {
        return cwb;
    }

    public Permission getPerms() {
        return permission;
    }

    public List<Material> getDestroyableBlocks() {
        return destroyableBlocks;
    }


    public PluginCfg getCfg() {
        return cfg;
    }

    public GameCfg getGameCfg() {
        return gameCfg;
    }

    public MapCfg getMapCfg() {
        return mapCfg;
    }

    public StructureCfg getStrucCfg() {
        return strucCfg;
    }

    public ClassesCfg getClassesCfg() {
        return classesCfg;
    }

    public AbilityCfg getAbilityCfg() {
        return abilityCfg;
    }

    public PlayerCfg getPlayerCfg() {
        return playerCfg;
    }

    public WorkShopCfg getWSCfg() {
        return wsCfg;
    }


    public EffectManager getEM() {
        return em;
    }

    public GameManager getGM() {
        return gm;
    }

    public MapManager getMM() {
        return mm;
    }

    public ClassManager getCM() {
        return cm;
    }

    public PlayerManager getPM() {
        return pm;
    }

}
