package com.clashwars.dvz.player;

import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvZClass;
import org.bukkit.entity.Player;

public class PlayerData {

    private Player player;
    private DvZClass playerClass;
    private int progress;

    public PlayerData(Player player, DvZClass playerClass, int progress) {
        this.player = player;
        this.playerClass = playerClass;
        this.progress = progress;
    }

    public Player getPlayer() {
        return player;
    }

    public DvZClass getPlayerClass() {
        return playerClass;
    }

    public int getProgress() {
        return progress;
    }

}
