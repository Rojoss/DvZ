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
        if (player != null && player.isOnline()) {
            CustomDamageEvent event = new CustomDamageEvent((Player)player, damage, type, this);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled() && player.isOnline() && !((Player)player).isDead()) {
                ((Player)player).setHealth(Math.max(((Player) player).getHealth() - event.getDamage(), 0));
            }
        }
    }

    public String getDeathMsg() {
        return player.getName() + " died";
    }

    public String getDmgMsg(boolean damageTaken) {
        return "unknown damage";
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
