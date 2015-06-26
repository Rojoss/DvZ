package com.clashwars.dvz.damage;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomDamageEvent extends Event implements Cancellable {

    private boolean cancelled;

    private Player player;
    private double damage;
    private DmgType dmgType;
    private BaseDmg dmgClass;

    public CustomDamageEvent(Player player, double damage, DmgType dmgType, BaseDmg dmgClass) {
        this.player = player;
        this.damage = damage;
        this.dmgType = dmgType;
        this.dmgClass = dmgClass;
    }


    public Player getPlayer() {
        return player;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void changeDamage(double change) {
        this.damage += change;
    }

    public DmgType getDmgType() {
        return dmgType;
    }

    public BaseDmg getDmgClass() {
        return dmgClass;
    }


    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean bln) {
        this.cancelled = bln;
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
