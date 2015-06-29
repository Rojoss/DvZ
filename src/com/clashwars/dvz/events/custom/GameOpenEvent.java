package com.clashwars.dvz.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameOpenEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
