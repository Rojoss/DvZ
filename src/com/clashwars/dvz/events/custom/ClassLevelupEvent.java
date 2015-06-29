package com.clashwars.dvz.events.custom;

import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassLevelupEvent extends Event {

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

}
