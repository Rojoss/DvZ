package com.clashwars.dvz.player;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CWPlayer {

    private UUID uuid;
    private PlayerData data;
    private PlayerCfg pcfg;

    public CWPlayer(UUID uuid, PlayerData data) {
        this.uuid = uuid;
        this.data = data;
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
}
