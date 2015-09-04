package com.clashwars.dvz.player;

import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.ItemMenu;
import com.clashwars.cwcore.damage.log.DamageLog;
import com.clashwars.cwcore.debug.Debug;
import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.ExpUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.VIP.BannerData;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import com.clashwars.dvz.listeners.custom.ClassLevelupEvent;
import com.clashwars.dvz.runnables.TeleportRunnable;
import com.clashwars.dvz.util.Util;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class CWPlayer {

    private DvZ dvz;
    private PlayerCfg pcfg;

    private UUID uuid;
    private PlayerData data;
    private long lastSave = System.currentTimeMillis();
    private long lastAutoSave = System.currentTimeMillis();

    private CooldownManager cdm = new CooldownManager();
    private BukkitRunnable teleport;
    public HashMap<String, Integer> productsTaken = new HashMap<String, Integer>();
    public List<DamageLog> damageLogs = new ArrayList<DamageLog>();
    private Color color;
    private Long spawnTime = System.currentTimeMillis();

    private Material endermanBlock = Material.AIR;


    public CWPlayer(UUID uuid, PlayerData data) {
        this.uuid = uuid;
        this.data = data;
        this.dvz = DvZ.inst();
        this.pcfg = dvz.getPlayerCfg();
        color = CWUtil.getRandomColor();

        damageLogs.add(new DamageLog(uuid));
    }

    public void reset() {
        reset(false);
    }

    public void reset(boolean switchable) {
        Long t = System.currentTimeMillis();
        Player player = getPlayer();
        if (player == null) {
            return;
        }

        data.setPvping(false);
        data.setSlimeSize(3);
        player.getInventory().clear();
        player.getInventory().setHelmet(new CWItem(Material.AIR));
        player.getInventory().setChestplate(new CWItem(Material.AIR));
        player.getInventory().setLeggings(new CWItem(Material.AIR));
        player.getInventory().setBoots(new CWItem(Material.AIR));
        player.updateInventory();

        if (!switchable) {
            player.setMaxHealth(20);
            player.setHealth(20);
            player.setFoodLevel(20);
            player.setFlying(false);
            player.setAllowFlight(false);
            player.setFireTicks(0);
            player.setGameMode(GameMode.SURVIVAL);
            player.setSaturation(10);
            player.setWalkSpeed(0.2f);
            player.setFlySpeed(0.1f);
            player.setTotalExperience(0);
            player.setLevel(0);
            player.setExp(0);

            Collection<PotionEffect> effects = player.getActivePotionEffects();
            for (PotionEffect pe : effects) {
                player.removePotionEffect(pe.getType());
            }
        }

        if (teleport != null) {
            teleport.cancel();
            teleport = null;
        }
        List<Team> teams = dvz.getBoard().getTeamList();
        for (Team team : teams) {
            if (team.hasPlayer(player)) {
                team.removePlayer(player);
            }
        }
        for (DvzClass dvzClass : DvzClass.values()) {
            String perm = "prefix." + dvzClass.toString().toLowerCase();
            if (dvz.getPerms().playerHas(player, perm)) {
                dvz.getPerms().playerRemove(player, perm);
            }
        }
        player.updateInventory();
        dvz.logTimings("CWPlayer.reset()", t);
    }

    public void undisguise() {
        if (getPlayer() != null) {
            Util.undisguisePlayer(getPlayer().getName());
        }
    }

    public void resetData() {
        productsTaken.clear();
        data.setPvping(false);
        data.setClassOptions(new HashSet<DvzClass>());
        data.setDwarfAbilitiesReceived(new HashSet<Ability>());
        data.setPlayerClass(DvzClass.DWARF);
        data.setClassExp(0);
        data.setParkourCompleted(false);
        data.setbombUsed(false);
        data.setBuffUsed(false);
        data.setBuffed(false);
        data.setSlimeSize(3);

        color = CWUtil.getRandomColor();
    }

    public void setClass(DvzClass dvzClass) {
        setClass(dvzClass, false);
    }

    public void setClass(final DvzClass dvzClass, final boolean eggUse) {
        final Long t = System.currentTimeMillis();
        final BaseClass c = dvzClass.getClassClass();
        final Player player = getPlayer();

        spawnTime = System.currentTimeMillis();

        if (eggUse && dvzClass.getPickStat() != null) {
            dvz.getSM().changeLocalStatVal(player, dvzClass.getPickStat(), 1);
        }

        //Reset
        reset(c.isSwitchable());

        setPlayerClass(dvzClass);
        if (dvzClass.getType() == ClassType.DWARF) {
            removeClassOption(dvzClass);
        }

        //Disguise
        if (dvzClass.getType() == ClassType.MONSTER || dvzClass.getType() == ClassType.DRAGON) {
            String disguise = c.getStrOption("disguise");
            if (dvzClass == DvzClass.SLIME) {
                disguise += " setSize 3";
            }
            Util.disguisePlayer(player.getName(), disguise, dvzClass);
        }

        //Team
        if (dvz.getBoard().hasTeam(dvzClass.getTeam() + getTeamSuffix())) {
            dvz.getBoard().getTeam(dvzClass.getTeam() + getTeamSuffix()).addPlayer(player);
        }

        //Prefix
        dvz.getPerms().playerAdd(player, "prefix." + getPlayerClass().toString().toLowerCase());

        //Teleport
        if (eggUse) {
            if (dvzClass.getType() == ClassType.DWARF) {
                player.teleport(dvz.getMM().getActiveMap().getLocation("dwarf").add(0,1,0));
            } else if (dvzClass.getType() == ClassType.MONSTER) {
                if (dvz.getGM().getState() == GameState.MONSTERS_WALL) {
                    player.teleport(dvz.getMM().getActiveMap().getLocation("wall").add(0, 1, 0));
                } else if (dvz.getGM().getState() == GameState.MONSTERS_KEEP) {
                    player.teleport(dvz.getMM().getActiveMap().getLocation("dwarf").add(0, 1, 0));
                } else {
                    player.teleport(dvz.getMM().getActiveMap().getLocation("monster").add(0, 1, 0));
                }
            } else if (dvzClass.getType() == ClassType.DRAGON) {
                player.teleport(dvz.getMM().getActiveMap().getLocation("dragon").add(0,1,0));
            }
        }

        //Equip class and items etc.
        c.equipItems(player);
        dvzClass.getClassClass().onEquipClass(player);
        if (!c.isSwitchable()) {
            player.setMaxHealth(c.getHealth());
            player.setHealth(c.getHealth());
        }
        player.setWalkSpeed(Math.min(Math.max(c.getSpeed(), 0), 1));
        player.setFlySpeed(Math.min(Math.max(c.getSpeed(), 0), 1));

        //Equip VIP items.
        if (c.getType() == ClassType.DWARF) {
            int bannerCount = 0;
            for (int i = 10; i > 0; i--) {
                if (player.hasPermission("banner." + i)) {
                    bannerCount = i;
                    break;
                }
            }
            if (bannerCount > 0) {
                CWItem banner = Product.VIP_BANNER.getItem(bannerCount);
                BannerData bannerData;
                if (dvz.getBannerCfg().BANNERS.containsKey(player.getUniqueId().toString())) {
                    bannerData = dvz.getBannerCfg().getBanner(player.getUniqueId());
                    banner.setBaseColor(bannerData.getBaseColor());
                    banner.setPatterns(bannerData.getPatterns());
                } else {
                    bannerData = new BannerData();
                }
                bannerData.setGiven(true);
                dvz.getBannerCfg().setBanner(uuid, bannerData);

                banner.giveToPlayer(player);
            }
        }

        //Equip hat
        Hat hat = HatManager.getHat(player);
        if (hat != null && !hat.isEquipped()) {
            hat.equip();
        }

        if (eggUse) {
            player.sendMessage(Util.formatMsg("&6You became a &5" + c.getDisplayName()));
        }
        if (c.getType() == ClassType.DWARF) {
            player.sendMessage(CWUtil.integrateColor("&7&o" + c.getTask()));
        }
        savePlayer();
        dvz.logTimings("CWPlayer.setClass()", t);
    }


    public void switchClass(DvzClass dvzClass, final ItemMenu menu) {
        Long t = System.currentTimeMillis();
        BaseClass c = dvzClass.getClassClass();
        final Player player = getPlayer();

        dvz.getSM().changeLocalStatVal(player, StatType.GENERAL_TIMES_SWITCHED, 1);

        player.getInventory().clear();

        if (dvz.getWM().hasWorkshop(player.getUniqueId())) {
            dvz.getWM().getWorkshop(player.getUniqueId()).destroy();
        }
        dvz.getWM().removeWorkshop(player.getUniqueId(), true);

        setPlayerClass(dvzClass);
        removeClassOption(dvzClass);
        c.equipItems(player);

        if (dvz.getBoard().hasTeam(dvzClass.getTeam() + getTeamSuffix())) {
            List<Team> teams = dvz.getBoard().getTeamList();
            for (Team team : teams) {
                if (team.hasPlayer(player)) {
                    team.removePlayer(player);
                }
            }
            dvz.getBoard().getTeam(dvzClass.getTeam() + getTeamSuffix()).addPlayer(player);
        }

        for (DvzClass dvzClasss : DvzClass.values()) {
            String perm = "prefix." + dvzClasss.toString().toLowerCase();
            if (dvz.getPerms().playerHas(player, perm)) {
                dvz.getPerms().playerRemove(player, perm);
            }
        }
        dvz.getPerms().playerAdd(player, "prefix." + getPlayerClass().toString().toLowerCase());

        for (int i = 9; i < menu.getSize(); i++) {
            if (menu.getItems()[i] != null && menu.getItems()[i].getType() != Material.AIR) {
                if (player.getInventory().getItem(i - 9) == null || player.getInventory().getItem(i - 9).getType() == Material.AIR) {
                    player.getInventory().setItem(i - 9, menu.getItems()[i]);
                } else {
                    player.getInventory().addItem(menu.getItems()[i]);
                }
            }
        }

        player.setMaxHealth(c.getHealth());
        player.setHealth(c.getHealth());
        player.sendMessage(Util.formatMsg("&6You became a &5" + c.getDisplayName()));
        if (c.getType() == ClassType.DWARF) {
            player.sendMessage(CWUtil.integrateColor("&7&o" + c.getTask()));
        }
        savePlayer();
        dvz.logTimings("CWPlayer.switchClass()", t);
    }


    public void onClassLoad() {
        Long t = System.currentTimeMillis();
        if (getPlayerClass() == null) {
            return;
        }
        Player player = getPlayer();
        DvzClass dvzClass = getPlayerClass();

        if (player == null || !player.isOnline()) {
            dvz.logTimings("CWPlayer.onClassLoad()[invalid player]", t);
            return;
        }

        //Disguise
        if (dvzClass.getType() == ClassType.MONSTER || dvzClass.getType() == ClassType.DRAGON) {
            Util.disguisePlayer(player.getName(), dvzClass.getClassClass().getStrOption("disguise"), dvzClass);
        }
        dvz.logTimings("CWPlayer.onClassLoad()", t);
    }


    public void giveClassItems(final ClassType type, final boolean forcePrevious, final int amount) {
        Long t = System.currentTimeMillis();
        Player player = getPlayer();
        if (forcePrevious) {
            if (type == ClassType.MONSTER) {
                DvzClass.ZOMBIE.getClassClass().getClassItem().giveToPlayer(player);
            }
            return;
        }
        Map<DvzClass, BaseClass> classOptions = dvz.getCM().getRandomClasses(player, type, amount);
        clearClassOptions();
        setClassOptions(classOptions.keySet());
        if (type == ClassType.MONSTER) {
            DvzClass.ZOMBIE.getClassClass().getClassItem().giveToPlayer(player);
            if (!classOptions.containsKey(DvzClass.ZOMBIE)) {
                classOptions.put(DvzClass.ZOMBIE, DvzClass.ZOMBIE.getClassClass());
            }
        }
        for (DvzClass c : classOptions.keySet()) {
            if (classOptions.get(c) != null && classOptions.get(c).getClassItem() != null) {
                classOptions.get(c).getClassItem().giveToPlayer(player);
            }
        }
        dvz.logTimings("CWPlayer.giveClassItems()", t);
    }

    public void giveRandomClassItems(ClassType type, int amount) {
        Player player = getPlayer();
        if (type == ClassType.MONSTER) {
            Map<DvzClass, BaseClass> classOptions = dvz.getCM().getRandomClasses(player, type, amount);
            for (DvzClass c : classOptions.keySet()) {
                if (classOptions.get(c) != null && classOptions.get(c).getClassItem() != null) {
                    classOptions.get(c).getClassItem().giveToPlayer(player);
                }
            }
        } else {
            List<DvzClass> allDwarfClasses = new ArrayList<>(dvz.getCM().getClasses(type).keySet());
            List<DvzClass> options = new ArrayList<DvzClass>();
            for (DvzClass dvzClass : allDwarfClasses) {
                //Debug.bcOps(dvzClass);
                if (!getClassOptions().contains(dvzClass)) {
                    //Debug.bcOps("Adding as option!");
                    options.add(dvzClass);
                }
            }
            //Debug.bcOps(options.size());
            //Debug.bcOps(options);
            int optionsCount = options.size();
            //Debug.bcOps("Giving " + amount + " random classes");
            for (int i = 0; i < amount && i < optionsCount; i++) {
                //Debug.bcOps(i);
                DvzClass option = CWUtil.random(options);
                //Debug.bcOps(option);
                addClassOption(option);
                //Debug.bc(option.getClassClass().getClassItem());
                if (option.getClassClass().getClassItem() != null) {
                    //Debug.bcOps("Giving class item!");
                    option.getClassClass().getClassItem().giveToPlayer(player);
                }
                options.remove(option);
            }
        }

    }

    public PlayerData getPlayerData() {
        return data;
    }

    public void setPlayerData(PlayerData data) {
        this.data = data;
        savePlayer();
    }


    public boolean isDwarf() {
        if (getPlayerClass() == null) {
            return false;
        }
        return getPlayerClass().getType() == ClassType.DWARF;
    }

    public boolean isMonster() {
        if (getPlayerClass() == null) {
            return false;
        }
        return getPlayerClass().getType() == ClassType.MONSTER;
    }

    public DvzClass getPlayerClass() {
        return data.getPlayerClass();
    }

    public void setPlayerClass(DvzClass playerClass) {
        dvz.getGM().calculateMonsterPerc();
        data.setPlayerClass(playerClass);
    }


    public Set<DvzClass> getClassOptions() {
        return data.getClassOptions();
    }

    public void setClassOptions(Set<DvzClass> classOptions) {
        data.setClassOptions(classOptions);
    }

    public void addClassOption(DvzClass dvzClass) {
        Set<DvzClass> classOptions = data.getClassOptions();
        if (!classOptions.contains(dvzClass)) {
            classOptions.add(dvzClass);
            data.setClassOptions(classOptions);
        }
    }

    public void removeClassOption(DvzClass dvzClass) {
        data.removeClassOption(dvzClass);
    }

    public void clearClassOptions() {
        data.setClassOptions(new HashSet<DvzClass>());
    }


    public void addDwarfAbility(Ability ability) {
        data.addDwarfAbility(ability);
    }


    public boolean hasCompletedParkour() {
        return data.hasCompletedParkour();
    }

    public void setParkourCompleted(boolean completed) {
        data.setParkourCompleted(completed);
        savePlayer();
    }

    public boolean isTesting() {
        return data.isTesting();
    }

    public void setTesting(boolean testing) {
        data.setTesting(testing);
    }

    public boolean isPvping() {
        return data.isPvping();
    }

    public void setPvping(boolean pvping) {
        data.setPvping(pvping);
    }


    public boolean timedTeleport(Location loc, int seconds, String locationMsg) {
        Long t = System.currentTimeMillis();
        if (teleport != null) {
            return false;
        }

        dvz.getSM().changeLocalStatVal(uuid, StatType.GENERAL_TIMES_TELEPORTED, 1);

        //If dwarf and no monsters nearby tp instant and other way around.
        List<Player> nearbyPlayers = CWUtil.getNearbyPlayers(getPlayer().getLocation(), 40);
        boolean nearbyEnemy = false;
        for (Player p : nearbyPlayers) {
            CWPlayer cwp = dvz.getPM().getPlayer(p);
            if (cwp.getPlayerClass().getType() == ClassType.DWARF) {
                if (getPlayerClass().getType() == ClassType.MONSTER) {
                    nearbyEnemy = true;
                }
            } else if (cwp.getPlayerClass().getType() == ClassType.MONSTER) {
                if (getPlayerClass().getType() == ClassType.DWARF) {
                    nearbyEnemy = true;
                }
            }
        }
        if (!nearbyEnemy) {
            seconds = 0;
        }

        if (seconds <= 0) {
            if (locationMsg != null && !locationMsg.isEmpty()) {
                sendMessage(Util.formatMsg("&6Teleported to &5" + locationMsg));
            }
            getPlayer().teleport(loc);
            dvz.logTimings("CWPlayer.timedTeleport()[instant]", t);
            return true;
        }
        teleport = new TeleportRunnable(this, seconds, loc, locationMsg);
        getPlayer().sendMessage(Util.formatMsg("&6Teleporting to &5" + locationMsg + "&8(&7Don't move!&8)"));
        dvz.logTimings("CWPlayer.timedTeleport()", t);
        return true;
    }

    public boolean isTeleporting() {
        return teleport != null;
    }

    public void resetTeleport() {
        if (teleport != null) {
            teleport.cancel();
        }
        teleport = null;
    }

    public int getClassExp() {
        return data.getClassExp();
    }

    public void setClassExp(int exp) {
        data.setClassExp(exp);
    }


    public void addClassExp(int exp) {
        dvz.getSM().changeLocalStatVal(uuid, StatType.DWARF_XP_EARNED, exp);
        if (getClassExp() + exp >= dvz.getCfg().XP_NEEDED_TO_LVL) {
            data.setClassExp(0);
            data.setClassLvl(data.getClassLvl() + 1);
            savePlayer();
            dvz.getSM().changeLocalStatVal(uuid, StatType.DWARF_LEVELS_EARNED, 1);
            Bukkit.getServer().getPluginManager().callEvent(new ClassLevelupEvent(this));
        } else {
            data.setClassExp(getClassExp() + exp);
        }

        ExpUtil xpu = new ExpUtil(getPlayer());
        xpu.changeExp(exp);

        if (System.currentTimeMillis() - lastSave >= 30000) {
            lastSave = 0;
            savePlayer();
        }
    }

    public void takeClassExp(int exp) {
        data.setClassExp(getClassExp() - exp);
    }


    public Color getColor() {
        return color;
    }

    public Long getSpawnTime() {
        return spawnTime;
    }


    public String getTeamSuffix() {
        if (getPlayer().hasPermission("team.staff")) {
            return "_s";
        }
        if (getPlayer().hasPermission("team.vip")) {
            return "_v";
        }
        return "";
    }


    public Material getEndermanBlock() {
        return endermanBlock;
    }

    public void setEndermanBlock(Material endermanBlock) {
        this.endermanBlock = endermanBlock;
    }


    public CooldownManager getCDM() {
        return cdm;
    }


    public void savePlayer() {
        //Time played stat
        if (dvz.getGM().isStarted()) {
            if (lastAutoSave <= 0) {
                lastAutoSave = System.currentTimeMillis() - 30000;
            }
            if (getPlayer() != null && getPlayer().isOnline()) {
                dvz.getSM().changeLocalStatVal(uuid, StatType.GENERAL_TIME_PLAYED, (float)System.currentTimeMillis() - (float)lastAutoSave);
            }
            dvz.getSM().changeLocalStatVal(uuid, StatType.GENERAL_GAME_TIME, (float)System.currentTimeMillis() - (float)lastAutoSave);
            lastAutoSave = System.currentTimeMillis();
        }

        pcfg.setPlayer(uuid, data);
    }


    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }

    public void sendMessage(String msg) {
        getPlayer().sendMessage(msg);
    }

    public String getName() {
        return getOfflinePlayer().getName();
    }

    public Location getLocation() {
        return getPlayer().getLocation();
    }

    public World getWorld() {
        return getPlayer().getWorld();
    }

    public void playSound(Location loc, Sound sound, float volume, float pitch) {
        getPlayer().playSound(loc, sound, volume, pitch);
    }

    public boolean isOnline() {
        return getPlayer() == null ? false : getPlayer().isOnline();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CWPlayer) {
            CWPlayer other = (CWPlayer)obj;

            return other.getUUID() == getUUID();
        }
        return false;
    }

}
