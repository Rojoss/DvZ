package com.clashwars.dvz.listeners;

import com.clashwars.cwcore.events.PlayerLeaveEvent;
import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.mysql.MySQL;
import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.Enjin;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainEvents implements Listener {

    private final DvZ dvz;
    private GameManager gm;

    public MainEvents(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
    }


    @EventHandler
    private void playerLeave(PlayerLeaveEvent event) {
        //Save when quiting.
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
        dvz.getGM().calculateMonsterPerc();
        dvz.getBoard().removePlayer(event.getPlayer());
    }


    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        Long t = System.currentTimeMillis();
        final Player player = event.getPlayer();
        final CWPlayer cwp = dvz.getPM().getPlayer(player);
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();

        dvz.getBoard().addPlayer(player);
        String titleStr = "&6Welcome to &6&lDvZ&6!";
        String subtitleStr = "";
        if (cwp.getPlayerClass() != null && !cwp.getPlayerClass().isBaseClass()) {
            //Player joined with a class already.
            titleStr = "&6Welcome back to &6&lDvZ&6!";
            subtitleStr = "&9You have joined dvz as a " + cwp.getPlayerClass().getClassClass().getDisplayName() + "&9!";
            player.sendMessage(Util.formatMsg("&6Welcome back!"));

            //If player has a workshop and it's not build then build it.
            if (dvz.getWM().hasWorkshop(player.getUniqueId())) {
                WorkShop ws = dvz.getWM().getWorkshop(player.getUniqueId());
                if (!ws.isBuild()) {
                    ws.build(null);
                }
            }

            spawnLoc = player.getLocation();
        } else {
            //Player joined without a class.
            cwp.reset();
            cwp.resetData();
            if (gm.getState() == GameState.CLOSED || (Util.isTest() && !Util.canTest(player))) {
                //Player joined after the game is closed.
                player.sendMessage(Util.formatMsg("&cThere is no &4DvZ &cright now!"));
                subtitleStr = "&c&lThere is &4&lno DvZ &c&lright now.";
                spawnLoc = dvz.getCfg().getDefaultWorld().getSpawnLocation();
            } else if (gm.isDwarves()) {
                //Player joined during dwarf time.
                player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &8Dwarf&6!"));
                cwp.setPlayerClass(DvzClass.DWARF);
                cwp.giveClassItems(ClassType.DWARF, false, -1);
                if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                    spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
                }
                subtitleStr = "&9You have joined DvZ as a &8Dwarf&9!";
            } else if (gm.isMonsters() || gm.getState() == GameState.DRAGON) {
                //Player joined during monster time.
                player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &4Monster&6!"));
                player.sendMessage(Util.formatMsg("&6This is because the dragon has been released already."));
                cwp.setPlayerClass(DvzClass.MONSTER);
                cwp.giveClassItems(ClassType.MONSTER, false, -1);
                if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
                    spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
                }
                subtitleStr = "&9You have joined DvZ as a &4Monster&9!";
            } else if (gm.getState() == GameState.OPENED || gm.getState() == GameState.SETUP) {
                //Player joined before the game is started
                player.sendMessage(Util.formatMsg("&6The game hasn't started yet but it will start soon."));
                subtitleStr = "&9The game hasn't started yet but it will start soon.";
            }
        }
        dvz.getGM().calculateMonsterPerc();

        //Send title and tab list format.
        Title title = new Title(titleStr, subtitleStr, 10, 100, 30);
        title.setTimingsToTicks();
        title.send(player);

        CWUtil.setTab(player, " &8======== &6&lDwarves &2VS &c&lZombies &8========", " &6INFO &8>>> &9&lwiki.clashwars.com &8<<< &6INFO");

        //Teleport player
        final Location spawnLocFinal = spawnLoc;
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(spawnLocFinal.add(0,1,0));
            }
        }.runTaskLater(dvz, 10);

        dvz.logTimings("MainEvents.playerJoin()", t);
    }


    @EventHandler
    private void respawn(PlayerRespawnEvent event) {
        Long t = System.currentTimeMillis();
        final Player player = event.getPlayer();
        final CWPlayer cwp = dvz.getPM().getPlayer(player);
        //Get the respawn location and get the active map.
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();
        if (dvz.getGM().getState() == GameState.ENDED || dvz.getGM().getState() == GameState.CLOSED || dvz.getGM().getState() == GameState.SETUP || (Util.isTest() && !Util.canTest(player))) {
            if (cwp.isPvping()) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(new ItemStack[] {});
                player.updateInventory();
                player.sendMessage(CWUtil.integrateColor("&6Respawning at &4&lPVP &6Click the &4[leave pvp] &6sign to go back."));
                event.setRespawnLocation(DvZ.pvpArenaSpawn);
            } else {
                event.setRespawnLocation(spawnLoc);
            }
            return;
        } else if (!dvz.getGM().isStarted()) {
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
            }
            event.setRespawnLocation(spawnLoc);
            return;
        }

        //Dragon death (respawn with saved data)
        if (dvz.getGM().getDragonPlayer() != null && dvz.getGM().getDragonPlayer().getUniqueId().equals(player.getUniqueId())) {
            if (dvz.getGM().getDragonSaveData() != null) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        dvz.getGM().getDragonSaveData().load(player);
                        dvz.getGM().setDragonPlayer(null);
                    }
                }.runTaskLater(dvz, 20);
            }
            return;
        }

        //Death during first day. (if dwarf respawn back at keep)
        if (cwp.isDwarf() && dvz.getGM().isDwarves()) {
            player.sendMessage(Util.formatMsg("&6You're alive again as Dwarf because the dragon hasn't come yet!"));
            event.setRespawnLocation(dvz.getMM().getActiveMap().getLocation("dwarf"));
            return;
        }

        //Spawn at monster lobby. (death after first day)
        if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
            spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
        }
        event.setRespawnLocation(spawnLoc);

        if (dvz.getGM().isStarted()) {
            //Player died as a dwarf.
            if (cwp.isDwarf()) {
                player.sendMessage(Util.formatMsg("&4&lYou have turned into a monster!!!"));
            }

            //Remove player from suicide list if he suicided.
            boolean suicide = false;
            if (dvz.getPM().suicidePlayers.contains(player.getUniqueId())) {
                suicide = true;
                dvz.getPM().suicidePlayers.remove(player.getUniqueId());
            }

            //Reset player and give class items.
            cwp.reset();
            cwp.setPlayerClass(DvzClass.MONSTER);
            if (dvz.getBoard().hasTeam(DvzClass.MONSTER.getTeam() + cwp.getTeamSuffix())) {
                dvz.getBoard().getTeam(DvzClass.MONSTER.getTeam() + cwp.getTeamSuffix()).addPlayer(player);
            }
            cwp.giveClassItems(ClassType.MONSTER, suicide, -1);
        }
        dvz.logTimings("MainEvents.respawn()", t);
    }
}
