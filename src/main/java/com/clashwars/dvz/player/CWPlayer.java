package com.clashwars.dvz.player;

import com.clashwars.dvz.classes.DvZClass;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CWPlayer {

    private Player player;
    private PlayerData pd;

    public CWPlayer(Player player, DvZClass playerClass, int progress) {
        this.player = player;
        this.pd = new PlayerData(player, playerClass, progress);
    }

    public PlayerData getPlayerData(){
        return pd;
    }

}
