package com.clashwars.dvz.events;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MainEvents implements Listener {

    private DvZ dvz;
    private GameManager gm;

    public MainEvents(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
    }

    @EventHandler
    private void levelUp(CWPlayer.ClassLevelupEvent event) {
        CWPlayer cwp = event.getCWPlayer();
        //TODO: Complete event tasks
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (cwp.getPlayerClass() != DvzClass.DWARF) {
            player.sendMessage(Util.formatMsg("&6Welcome back!"));
            return;
        }

        if (gm.isDwarves()) {
            player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &8Dwarf&6!"));
            cwp.setPlayerClass(DvzClass.DWARF);
        } else if (gm.isMonsters()) {
            player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &4Monster&6!"));
            player.sendMessage(Util.formatMsg("&6This is because the dragon has been killed already."));
            cwp.setPlayerClass(DvzClass.MONSTER);
        } else if (gm.getState() == GameState.OPENED) {
            player.sendMessage(Util.formatMsg("&6The game hasn't started yet but it will start soon."));
        } else if (gm.getState() == GameState.CLOSED) {
            player.sendMessage(Util.formatMsg("&cThere is no &4DvZ &cright now!"));
        }
    }

    @EventHandler
    private void playerQuit(PlayerQuitEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }

    @EventHandler
    private void playerKick(PlayerKickEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }

}
