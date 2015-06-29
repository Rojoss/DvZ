package com.clashwars.dvz.events.custom;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameResetEvent extends Event {

    private boolean nextGame;
    private String mapName;

    public GameResetEvent(boolean nextGame, String mapName) {
        this.nextGame = nextGame;
        this.mapName = mapName;
    }

    public boolean hasNextGame() {
        return nextGame;
    }

    public String getMapName() {
        return mapName;
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
