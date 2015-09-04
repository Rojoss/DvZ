package com.clashwars.dvz;

import com.clashwars.clashwars.ClashWars;
import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.damage.DamageHandler;
import com.clashwars.cwcore.damage.log.LogMenu;
import com.clashwars.cwcore.debug.TimingsLog;
import com.clashwars.cwcore.effect.EffectManager;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.scoreboard.CWBoard;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.CWStats;
import com.clashwars.cwstats.stats.DataManager;
import com.clashwars.cwstats.stats.StatsManager;
import com.clashwars.cwvote.CWVote;
import com.clashwars.cwvote.shop.ShopItem;
import com.clashwars.dvz.VIP.ArmorMenu;
import com.clashwars.dvz.VIP.BannerMenu;
import com.clashwars.dvz.VIP.ColorMenu;
import com.clashwars.dvz.VIP.PatternMenu;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassManager;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.commands.Commands;
import com.clashwars.dvz.config.*;
import com.clashwars.dvz.listeners.*;
import com.clashwars.dvz.maps.MapManager;
import com.clashwars.dvz.player.PlayerManager;
import com.clashwars.dvz.player.SettingsMenu;
import com.clashwars.dvz.runnables.AntiCamp;
import com.clashwars.dvz.runnables.GameRunnable;
import com.clashwars.dvz.structures.internal.StructureType;
import com.clashwars.dvz.tips.TipManager;
import com.clashwars.dvz.workshop.WorkShop;
import com.clashwars.dvz.workshop.WorkshopManager;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import com.google.gson.Gson;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.NameTagVisibility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DvZ extends JavaPlugin {

    private static DvZ instance;
    private CWCore cwcore;
    private ClashWars cw;
    private CWStats cws;

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
    private PlayerSettingsCfg settingsCfg;
    private ReleasePlayersCfg releasePlayersCfg;
    private RewardsCfg rewardsCfg;

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
    private BannerMenu bannerMenu;
    private PatternMenu patternMenu;
    private SettingsMenu settingsMenu;

    private TimingsLog timingsLog;
    boolean timingsEnabled = false;
    private final Logger log = Logger.getLogger("Minecraft");

    public GameRunnable gameRunnable;

    public static Location pvpArenaSpawn;
    private List<Material> undestroyableBlocks = Arrays.asList(new Material[] {Material.BEDROCK, Material.OBSIDIAN, Material.ENDER_STONE, Material.ENDER_PORTAL_FRAME,
        Material.WEB, Material.STANDING_BANNER, Material.WALL_BANNER, Material.DRAGON_EGG, Material.BARRIER, Material.REDSTONE_BLOCK, Material.GLOWSTONE, Material.REDSTONE_LAMP_OFF, Material.REDSTONE_LAMP_ON});

    @Override
    public void onDisable() {
        Long t = System.currentTimeMillis();
        //Save all player data.
        getPM().savePlayers();
        for (Player player : getServer().getOnlinePlayers()) {
            player.closeInventory();
        }

        //Destroy all workshops but keep them in config. (they will be build again on load)
        HashMap<UUID, WorkShop> workshopsClone = new HashMap<UUID, WorkShop>(getWM().getWorkShops());
        for (WorkShop ws : workshopsClone.values()) {
            ws.destroy();
        }
        getWM().removeWorkshops(false);

        //Clean up effect stuff.
        em.dispose();

        //Clean up holograms
        Hologram[] holograms = HolographicDisplaysAPI.getHolograms(this);
        for (Hologram hologram : holograms) {
            hologram.delete();
        }

        Bukkit.getScheduler().cancelAllTasks();

        log("disabled");
        logTimings("Dvz.onDisable()", t);
    }

    @Override
    public void onEnable() {
        Long t = System.currentTimeMillis();
        instance = this;

        Plugin plugin = getServer().getPluginManager().getPlugin("CWCore");
        if (plugin == null || !(plugin instanceof CWCore)) {
            log("CWCore dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        cwcore = (CWCore)plugin;

        plugin = getServer().getPluginManager().getPlugin("ClashWars");
        if (plugin == null || !(plugin instanceof ClashWars)) {
            log("ClashWars dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        cw = (ClashWars)plugin;

        plugin = getServer().getPluginManager().getPlugin("CWStats");
        if (plugin == null || !(plugin instanceof CWStats)) {
            log("CWStats dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        cws = (CWStats)plugin;

        plugin = getServer().getPluginManager().getPlugin("CWVote");
        if (plugin == null || !(plugin instanceof CWVote)) {
            log("CWVote dependency couldn't be loaded! Vote rewards won't be added!");
        } else {
            addVoteRewards();
        }

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
        settingsCfg = new PlayerSettingsCfg("plugins/DvZ/PlayerSettings.yml");
        settingsCfg.load();
        releasePlayersCfg = new ReleasePlayersCfg("plugins/DvZ/data/ReleasePlayers.yml");
        releasePlayersCfg.load();
        rewardsCfg = new RewardsCfg("plugins/DvZ/data/Rewards.yml");
        rewardsCfg.load();

        mm = new MapManager(this);
        gm = new GameManager(this);
        cm = new ClassManager(this);
        pm = new PlayerManager(this);
        wm = new WorkshopManager(this);
        tm = new TipManager();
        gm.calculateMonsterPerc();

        armorMenu = new ArmorMenu(this);
        colorMenu = new ColorMenu(this);
        bannerMenu = new BannerMenu(this);
        patternMenu = new PatternMenu(this);
        settingsMenu = new SettingsMenu(this);

        registerEvents();

        cmds = new Commands(this);

        startRunnables();

        pvpArenaSpawn = new Location(getCfg().getDefaultWorld(), -8, 39, -53);
        pvpArenaSpawn.setYaw(180);

        log("loaded successfully");
        logTimings("Dvz.onEnable()", t);
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);
        pm.registerEvents(new LobbyEvents(this), this);
        pm.registerEvents(new SwitchEvents(this), this);
        pm.registerEvents(new UtilityEvents(this), this);
        pm.registerEvents(new WorkShopEvents(this), this);

        Ability[] abilities = Ability.values();
        for (Ability a : abilities) {
            pm.registerEvents(a.getAbilityClass(), this);
        }
        DvzClass[] classes = DvzClass.values();
        for (DvzClass c : classes) {
            pm.registerEvents(c.getClassClass(), this);
        }
        StructureType[] structures = StructureType.values();
        for (StructureType s : structures) {
            pm.registerEvents(s.getStrucClass(), this);
        }

        pm.registerEvents(new ProtectEvents(this), this);
        pm.registerEvents(new VIPEvents(this), this);
        pm.registerEvents(armorMenu, this);
        pm.registerEvents(colorMenu, this);
        pm.registerEvents(bannerMenu, this);
        pm.registerEvents(patternMenu, this);
        pm.registerEvents(settingsMenu, this);
        pm.registerEvents(new WeaponHandler(this), this);
    }

    private void startRunnables() {
        gameRunnable = new GameRunnable(this);
        gameRunnable.runTaskTimer(this, 0, 2);
        new AntiCamp(this).runTaskTimer(this, 40, getCfg().CAMP_DELAY_TICKS);
    }

    private void setupBoard() {
        cwb = CWBoard.get("dvz");
        cwb.register();

        String[] nameSuffix = new String[] {"", "_v", "_s"};
        String[] prefix = new String[] {"", "&b&lVIP ", "&d&lSTAFF "};
        String[] colorFormat = new String[] {"", "&l&o", "&l"};
        for (int i = 0; i < nameSuffix.length; i++) {
            cwb.addTeam("builder" + nameSuffix[i], prefix[i] + "&9" + colorFormat[i], "", "&e" + colorFormat[i] + "Builder", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("miner" + nameSuffix[i], prefix[i] + "&8" + colorFormat[i], "", "&8" + colorFormat[i] + "Miner", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("tailor" + nameSuffix[i], prefix[i] + "&3" + colorFormat[i], "", "&3" + colorFormat[i] + "Tailor", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("fletcher" + nameSuffix[i], prefix[i] + "&2" + colorFormat[i], "", "&2" + colorFormat[i] + "Fletcher", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("alchemist" + nameSuffix[i], prefix[i] + "&5" + colorFormat[i], "", "&5" + colorFormat[i] + "Alchemist", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("baker" + nameSuffix[i], prefix[i] + "&6" + colorFormat[i], "", "&5" + colorFormat[i] + "Alchemist", true, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("monster" + nameSuffix[i], prefix[i] + "&c" + colorFormat[i], "", "&c" + colorFormat[i] + "Monster", false, true, NameTagVisibility.ALWAYS);
            cwb.addTeam("dragonslayer" + nameSuffix[i], prefix[i] + "&d" + colorFormat[i], "", "&d" + colorFormat[i] + "Dragon-Slayer", true, true, NameTagVisibility.ALWAYS);
        }

        for (Player player : getServer().getOnlinePlayers()) {
            cwb.addPlayer(player);
        }
        cwb.show(true);
    }

    private void addVoteRewards() {
        new ShopItem("&b&lExtra dwarf class", 1, new CWItem(Material.DIAMOND_PICKAXE),
                new String[] {"&7Get an extra dwarf class option.", "&cThis will be for one game!"}, "dvz");
        new ShopItem("&3&lExtra dwarf class", 75, new CWItem(Material.DIAMOND_PICKAXE),
                new String[] {"&7Get an extra dwarf class option.", "&2This will last forever!"}, "dvz");
        new ShopItem("&b&lVirtual structures", 1, new CWItem(Material.CHEST),
                new String[] {"&7Get access to structure commands.", "&b/storage&8, &b/enchant&8, &b/furnace", "&cThis will be for one game!"}, "dvz");
        new ShopItem("&3&l/storage command", 50, new CWItem(Material.CHEST),
                new String[] {"&7Get access to the &b/storage &7command.", "&2This will last forever!"}, "dvz");
        new ShopItem("&3&l/furnace command", 50, new CWItem(Material.FURNACE),
                new String[] {"&7Get access to the &b/furnace &7command.", "&2This will last forever!"}, "dvz");
        new ShopItem("&3&l/enchant command", 40, new CWItem(Material.ENCHANTMENT_TABLE),
                new String[] {"&7Get access to the &b/enchant &7command.", "&2This will last forever!"}, "dvz");
        new ShopItem("&b&lDwarf ability", 1, new CWItem(Material.BLAZE_ROD),
                new String[] {"&7Get a random dwarf ability.", "&cThis will be for one game!"}, "dvz");
        new ShopItem("&b&l+3% monster classes", 1, new CWItem(Material.MONSTER_EGG, 3, (byte)54),
                new String[] {"&73% extra chance for each monster class.", "&cThis will be for one game!"}, "dvz");
        new ShopItem("&b&l+6% monster classes", 2, new CWItem(Material.MONSTER_EGG, 6, (byte)54),
                new String[] {"&76% extra chance for each monster class.", "&cThis will be for one game!"}, "dvz");
        new ShopItem("&b&l+10% monster classes", 3, new CWItem(Material.MONSTER_EGG, 10, (byte)54),
                new String[] {"&710% extra chance for each monster class.", "&cThis will be for one game!"}, "dvz");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }



    public void log(Object msg) {
        log.info("[DvZ " + getDescription().getVersion() + "] " + msg.toString());
    }

    public void logError(Object msg) {
        log.severe("[DvZ " + getDescription().getVersion() + "] " + msg.toString());
    }

    public static DvZ inst() {
        return instance;
    }


    /* Timings */
    public boolean startTimings () {
        if (timingsLog == null) {
            timingsEnabled = true;
            timingsLog = new TimingsLog(this, CWUtil.getTimeStamp("dd-MM__HH-mm") + ".timings");
            return true;
        }
        return false;
    }

    public boolean stopTimings() {
        if (timingsLog != null) {
            timingsEnabled = false;
            timingsLog = null;
            return true;
        }
        return false;
    }

    public boolean pauseTimings() {
        if (timingsEnabled) {
            timingsEnabled = false;
            return true;
        }
        return false;
    }

    public boolean resumeTimings() {
        if (!timingsEnabled) {
            timingsEnabled = true;
            return true;
        }
        return false;
    }

    public void logTimings(Object msg, Long startTime) {
        if (timingsEnabled && timingsLog != null) {
            timingsLog.log(msg.toString(), startTime);
        }
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

    public PlayerSettingsCfg getSettingsCfg() {
        return settingsCfg;
    }

    public ReleasePlayersCfg getReleasePlayersCfg() {
        return releasePlayersCfg;
    }

    public RewardsCfg getRewardsCfg() {
        return rewardsCfg;
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

    public DataManager getDM() {
        return cws.dm;
    }

    public StatsManager getSM() {
        return cws.sm;
    }

    public ArmorMenu getArmorMenu() {
        return armorMenu;
    }

    public ColorMenu getColorMenu() {
        return colorMenu;
    }

    public BannerMenu getBannerMenu() {
        return bannerMenu;
    }

    public PatternMenu getPatternMenu() {
        return patternMenu;
    }

    public SettingsMenu getSettingsMenu() {
        return settingsMenu;
    }

}
