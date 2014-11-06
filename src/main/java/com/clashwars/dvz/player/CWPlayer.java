package com.clashwars.dvz.player;

import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.ExpUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import com.clashwars.dvz.util.Util;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public class CWPlayer {

    private DvZ dvz;
    private PlayerCfg pcfg;

    private UUID uuid;
    private PlayerData data;
    private long lastSave = System.currentTimeMillis();

    private CooldownManager cdm = new CooldownManager();
    public HashMap<String, Integer> productsTaken = new HashMap<String, Integer>();


    public CWPlayer(UUID uuid, PlayerData data) {
        this.uuid = uuid;
        this.data = data;
        this.dvz = DvZ.inst();
        this.pcfg = dvz.getPlayerCfg();
    }


    public void reset() {
        Player player = getPlayer();
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
        player.getInventory().clear();
        player.getInventory().setHelmet(new CWItem(Material.AIR));
        player.getInventory().setChestplate(new CWItem(Material.AIR));
        player.getInventory().setLeggings(new CWItem(Material.AIR));
        player.getInventory().setBoots(new CWItem(Material.AIR));
        for (PotionEffect pe : player.getActivePotionEffects()) {
            player.removePotionEffect(pe.getType());
        }
        player.updateInventory();
    }

    public void undisguise() {
        Util.undisguisePlayer(getPlayer().getName());
    }

    public void resetData() {
        productsTaken.clear();
        data.setClassOptions(new HashSet<DvzClass>());
        data.setPlayerClass(DvzClass.DWARF);
        data.setClassExp(0);
        data.setParkourCompleted(false);
    }


    public void setClass(DvzClass dvzClass) {
        BaseClass c = dvzClass.getClassClass();
        Player player = getPlayer();
        //Reset
        reset();

        setPlayerClass(dvzClass);

        //Disguise
        if (dvzClass.getType() == ClassType.MONSTER || dvzClass.getType() == ClassType.DRAGON) {
            Util.disguisePlayer(player.getName(), c.getDisguise());
        }

        //Teleport
        if (dvzClass.getType() == ClassType.DWARF) {
            player.teleport(dvz.getMM().getActiveMap().getLocation("dwarf"));
        } else if (dvzClass.getType() == ClassType.MONSTER) {
            if (dvz.getGM().getState() == GameState.MONSTERS_WALL) {
                player.teleport(dvz.getMM().getActiveMap().getLocation("wall"));
            } else {
                player.teleport(dvz.getMM().getActiveMap().getLocation("monster"));
            }
        } else if (dvzClass.getType() == ClassType.DRAGON) {
            player.teleport(dvz.getMM().getActiveMap().getLocation("dragon"));
        }

        //Equip class and items etc.
        c.equipItems(player);
        player.setMaxHealth(c.getHealth());
        player.setHealth(c.getHealth());
        player.sendMessage(Util.formatMsg("&6You became a &5" + c.getDisplayName()));
        player.sendMessage(CWUtil.integrateColor("&8&l❝&7" + c.getDescription().replace("\\|\\|", "") + "&8&l❞"));
        savePlayer();
    }


    public void giveClassItems(ClassType type, boolean forcePrevious) {
        Player player = getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (forcePrevious) {
            for (DvzClass c : cwp.getClassOptions()) {
                dvz.getCM().getClass(c).getClassItem().giveToPlayer(player);
            }
            return;
        }
        Map<DvzClass, BaseClass> classOptions = dvz.getCM().getRandomClasses(player, type);
        cwp.clearClassOptions();
        cwp.setClassOptions(classOptions.keySet());
        for (DvzClass c : classOptions.keySet()) {
            classOptions.get(c).getClassItem().giveToPlayer(player);
        }
    }



    public PlayerData getPlayerData() {
        return data;
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
        data.setPlayerClass(playerClass);
    }


    public Set<DvzClass> getClassOptions() {
        return data.getClassOptions();
    }

    public void setClassOptions(Set<DvzClass> classOptions) {
        data.setClassOptions(classOptions);
    }

    public void clearClassOptions() {
        data.setClassOptions(new HashSet<DvzClass>());
    }


    public boolean hasCompletedParkour() {
        return data.hasCompletedParkour();
    }

    public void setParkourCompleted(boolean completed) {
        data.setParkourCompleted(completed);
    }


    public int getClassExp() {
        return data.getClassExp();
    }

    public void setClassExp(int exp) {
        data.setClassExp(exp);
    }


    public void addClassExp(int exp) {
        if (getClassExp() + exp >= dvz.getCfg().XP_NEEDED_TO_LVL) {
            data.setClassExp(0);
            Bukkit.getServer().getPluginManager().callEvent(new ClassLevelupEvent(this));
        } else {
            data.setClassExp(getClassExp() + exp);
            ExpUtil xpu = new ExpUtil(getPlayer());
            xpu.changeExp(exp);
        }
        if (System.currentTimeMillis() - lastSave >= 30000) {
            lastSave = 0;
            savePlayer();
        }
    }

    public void takeClassExp(int exp) {
        data.setClassExp(getClassExp() - exp);
    }


    public CooldownManager getCDM() {
        return cdm;
    }


    public void savePlayer() {
        pcfg.setPlayer(uuid, data);
    }


    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void sendMessage(String msg) {
        getPlayer().sendMessage(msg);
    }

    public String getName() {
        return getPlayer().getName();
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
        return getPlayer().isOnline();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CWPlayer) {
            CWPlayer other = (CWPlayer)obj;

            return other.getUUID() == getUUID();
        }
        return false;
    }


    //Custom ClassLevelupEvent.
    public static class ClassLevelupEvent extends Event {

        private CWPlayer cwp;

        public ClassLevelupEvent(CWPlayer cwp) {
            this.cwp = cwp;
        }

        public CWPlayer getCWPlayer() {
            return cwp;
        }

        private static final HandlerList handlers = new HandlerList();

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }
    }

}
