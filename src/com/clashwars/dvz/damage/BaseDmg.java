package com.clashwars.dvz.damage;

import com.clashwars.cwcore.Debug;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class BaseDmg {

    protected OfflinePlayer player;
    protected double damage;
    protected DmgType type = DmgType.UNKNOWN;

    public BaseDmg(OfflinePlayer player, double damage) {
        this.player = player;
        this.damage = damage;
    }

    public void damage() {
        //--
    }

    public String getDeathMsg() {
        return player.getName() + " died";
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public double getDmg() {
        return damage;
    }

    public DmgType getType() {
        return type;
    }
}
