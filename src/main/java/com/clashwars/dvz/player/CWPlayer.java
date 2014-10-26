package com.clashwars.dvz.player;

import com.clashwars.cwcore.utils.ExpUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.security.Timestamp;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class CWPlayer {

    private UUID uuid;
    private PlayerData data;
    private PlayerCfg pcfg;
    private DvZ dvz;
    private ExpUtil exputil = new ExpUtil(getPlayer());
    Calendar calendar = Calendar.getInstance();
    private long lastSave = System.currentTimeMillis();

    public CWPlayer(UUID uuid, PlayerData data) {
        this.uuid = uuid;
        this.data = data;
        this.dvz = DvZ.inst();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CWPlayer) {
            CWPlayer other = (CWPlayer) obj;

            return other.getUUID() == getUUID();
        }
        return false;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public PlayerData getPlayerData() {
        return data;
    }


    public DvzClass getPlayerClass() {
        return data.getPlayerClass();
    }

    public void setPlayerClass(DvzClass playerClass) {
        data.setPlayerClass(playerClass);
    }


    public int getClassExp() {
        return data.getClassExp();
    }

    public void setClassExp(int exp) {
        data.setClassExp(exp);
    }

    public void addClassExp(int exp) {
        if(getClassExp() + exp >= dvz.getCfg().XP_NEEDED_TO_LVL) {
            data.setClassExp(0);
            Bukkit.getServer().getPluginManager().callEvent(new ClassLevelupEvent(this));
        } else {
            data.setClassExp(getClassExp() + exp);
            exputil.changeExp(exp);
        }
        checkLastSave();
    }

    public void takeClassExp(int exp) {
        data.setClassExp(getClassExp() - exp);
        exputil.changeExp(-exp);
        checkLastSave();
    }

    private void checkLastSave() {
        if(System.currentTimeMillis() - lastSave >= 30) {
            savePlayer();
        }
    }

    public Set<DvzClass> getClassOptions() {
        return data.getClassOptions();
    }

    public void setClassOptions(Set<DvzClass> classOptions) {
        data.setClassOptions(classOptions);
    }


    public void savePlayer() {
        pcfg.setPlayer(uuid, data);
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
