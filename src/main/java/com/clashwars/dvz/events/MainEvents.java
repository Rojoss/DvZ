package com.clashwars.dvz.events;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class MainEvents implements Listener {

    private DvZ dvz;

    public MainEvents(DvZ dvz) {
        this.dvz = dvz;
    }

    @EventHandler
    public void levelUp(CWPlayer.ClassLevelupEvent event) {
        CWPlayer cwp = event.getCWPlayer();
        //TODO: Complete event tasks
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {

    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }

    @EventHandler
    public void pluginDisable(PluginDisableEvent event) {
        dvz.getPM().savePlayers();
    }

}
