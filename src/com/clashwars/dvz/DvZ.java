package com.clashwars.dvz;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.effect.EffectManager;
import com.clashwars.cwcore.scoreboard.CWBoard;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.VIP.*;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassManager;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.commands.Commands;
import com.clashwars.dvz.config.*;
import com.clashwars.dvz.events.*;
import com.clashwars.dvz.maps.DvzMap;
import com.clashwars.dvz.maps.MapManager;
import com.clashwars.dvz.player.PlayerManager;
import com.clashwars.dvz.runnables.AntiCamp;
import com.clashwars.dvz.runnables.GameRunnable;
import com.clashwars.dvz.structures.internal.StructureType;
import com.clashwars.dvz.tips.TipManager;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.SoundMenu;
import com.clashwars.dvz.workshop.WorkShop;
import com.clashwars.dvz.workshop.WorkshopManager;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.gson.Gson;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.util.*;

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
    private ArmorPresetsCfg presetCfg;
    private BannerCfg bannerCfg;

    private Commands cmds;

    private EffectManager em;
    private GameManager gm;
    private MapManager mm;
    private ClassManager cm;
    private PlayerManager pm;
    private WorkshopManager wm;
    private TipManager tm;

    private ArmorMenu armorMenu;
    private ColorMenu colorMenu;
    private SoundMenu soundMenu;
    private BannerMenu bannerMenu;
    private PatternMenu patternMenu;

    private final Logger log = Logger.getLogger("Minecraft");

    public GameRunnable gameRunnable;

    private List<Material> undestroyableBlocks = Arrays.asList(new Material[] {Material.BEDROCK, Material.OBSIDIAN, Material.ENDER_STONE, Material.ENDER_PORTAL_FRAME,
        Material.WEB, Material.STANDING_BANNER, Material.WALL_BANNER, Material.DRAGON_EGG, Material.BARRIER, Material.REDSTONE_BLOCK});

    @Override
    public void onDisable() {
        //Save all player data.
        getPM().savePlayers();

        //Destroy all workshops but keep them in config. (they will be build again on load)
        for (WorkShop ws : getWM().getWorkShops().values()) {
            ws.destroy();
        }
        getWM().removeWorkshops(false);

        //Clean up effect stuff.
        em.dispose();

        //Clean up holograms
        for (Hologram hologram : HolographicDisplaysAPI.getHolograms(this)) {
            hologram.delete();
        }

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
        cwcore = (CWCore)plugin;

        permission = cwcore.getDM().getPermissions();
        if (permission == null) {
            log("Vault permissions couldn't be loaded.");
            setEnabled(false);
            return;
        }

        em = new EffectManager(cwcore);

        setupBoard();

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
        abilityCfg.load();

        playerCfg = new PlayerCfg("plugins/DvZ/data/Players.yml");
        playerCfg.load();

        wsCfg = new WorkShopCfg("plugins/DvZ/data/Workshops.yml");
        wsCfg.load();

        presetCfg = new ArmorPresetsCfg("plugins/DvZ/data/ArmorPresets.yml");
        presetCfg.load();

        bannerCfg = new BannerCfg("plugins/DvZ/data/Banners.yml");
        bannerCfg.load();

        mm = new MapManager(this);
        gm = new GameManager(this);
        cm = new ClassManager(this);
        pm = new PlayerManager(this);
        wm = new WorkshopManager(this);
        tm = new TipManager();
        gm.calculateMonsterPerc();

        soundMenu = new SoundMenu(this);
        armorMenu = new ArmorMenu(this);
        colorMenu = new ColorMenu(this);
        bannerMenu = new BannerMenu(this);
        patternMenu = new PatternMenu(this);

        registerEvents();

        cmds = new Commands(this);

        startRunnables();

        log("loaded successfully");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);
        pm.registerEvents(new LobbyEvents(this), this);
        pm.registerEvents(new SwitchEvents(this), this);
        pm.registerEvents(new UtilityEvents(this), this);
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
        pm.registerEvents(new VIPEvents(this), this);
        pm.registerEvents(armorMenu, this);
        pm.registerEvents(colorMenu, this);
        pm.registerEvents(soundMenu, this);
        pm.registerEvents(bannerMenu, this);
        pm.registerEvents(patternMenu, this);
    }

    private void startRunnables() {
        gameRunnable = new GameRunnable(this);
        gameRunnable.runTaskTimer(this, 0, 2);
        new AntiCamp(this).runTaskTimer(this, 40, getCfg().CAMP_DELAY_TICKS);
    }

    private void setupBoard() {
        cwb = CWBoard.get("dvz");
        cwb.init(true);

        String[] nameSuffix = new String[] {"", "_v", "_s"};
        String[] colorFormat = new String[] {"", "&l&o", "&l"};
        for (int i = 0; i < nameSuffix.length; i++) {
            cwb.addTeam("builder" + nameSuffix[i], "&9" + colorFormat[i], "", "&e" + colorFormat[i] + "Builder", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("miner" + nameSuffix[i], "&8" + colorFormat[i], "", "&8" + colorFormat[i] + "Miner", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("tailor" + nameSuffix[i], "&3" + colorFormat[i], "", "&3" + colorFormat[i] + "Tailor", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("fletcher" + nameSuffix[i], "&2" + colorFormat[i], "", "&2" + colorFormat[i] + "Fletcher", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("alchemist" + nameSuffix[i], "&5" + colorFormat[i], "", "&5" + colorFormat[i] + "Alchemist", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("baker" + nameSuffix[i], "&6" + colorFormat[i], "", "&5" + colorFormat[i] + "Alchemist", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("monster" + nameSuffix[i], "&c" + colorFormat[i], "", "&c" + colorFormat[i] + "Monster", false, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("dragonslayer" + nameSuffix[i], "&d" + colorFormat[i], "", "&d" + colorFormat[i] + "Dragon-Slayer", true, true, NameTagVisibility.ALWAYS);
        }

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

    public List<Material> getUndestroyableBlocks() {
        return undestroyableBlocks;
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

    public ArmorPresetsCfg getPresetCfg() {
        return presetCfg;
    }

    public BannerCfg getBannerCfg() {
        return bannerCfg;
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

    public WorkshopManager getWM() {
        return wm;
    }

    public TipManager getTM() {
        return tm;
    }

    public ArmorMenu getArmorMenu() {
        return armorMenu;
    }

    public ColorMenu getColorMenu() {
        return colorMenu;
    }

    public SoundMenu getSoundMenu() {
        return soundMenu;
    }

    public BannerMenu getBannerMenu() {
        return bannerMenu;
    }

    public PatternMenu getPatternMenu() {
        return patternMenu;
    }

}
