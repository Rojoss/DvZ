package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvZClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class CWPlayer {

    private DvZ dvz;

    private UUID uuid;
    private PlayerData data;

    public CWPlayer(UUID uuid, PlayerData data) {
        this.uuid = uuid;
        this.data = data;
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public Player getPlayer() {
        return dvz.getServer().getPlayer(uuid);
    }

    public PlayerData getPlayerData() {
        return data;
    }

    public void setPlayerData(PlayerData playerData) {
        this.data = playerData;
    }

    public DvZClass getPlayerClass() {
        return data.getPlayerClass();
    }

    public void setPlayerClass(DvZClass playerClass) {
        data.setPlayerClass(playerClass);
    }

    public int getClassExp() {
        return data.getClassExp();
    }

    public void setClassExp(int exp) {
        data.setClassExp(exp);
    }

    public Set<DvZClass> getClassOptions() {
        return data.getClassOptions();
    }

    public void setClassOptions(Set<DvZClass> classOptions) {
        data.setClassOptions(classOptions);
    }

}
