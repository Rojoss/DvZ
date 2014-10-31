package com.clashwars.dvz.events;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();;

        if (cwp.getClassOptions() != null && !cwp.getClassOptions().isEmpty()) {
            player.sendMessage(Util.formatMsg("&6Welcome back!"));
            return;
        }

        if (gm.isDwarves()) {
            player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &8Dwarf&6!"));
            cwp.setPlayerClass(DvzClass.DWARF);
            cwp.giveClassItems(ClassType.DWARF, false);
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
            }
        } else if (gm.isMonsters()) {
            player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &4Monster&6!"));
            player.sendMessage(Util.formatMsg("&6This is because the dragon has been killed already."));
            cwp.setPlayerClass(DvzClass.MONSTER);
            cwp.giveClassItems(ClassType.MONSTER, false);
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
            }
        } else if (gm.getState() == GameState.OPENED || gm.getState() == GameState.SETUP) {
            player.sendMessage(Util.formatMsg("&6The game hasn't started yet but it will start soon."));
        } else if (gm.getState() == GameState.CLOSED) {
            player.sendMessage(Util.formatMsg("&cThere is no &4DvZ &cright now!"));
        }

        player.teleport(spawnLoc);
    }


    @EventHandler
    private void playerQuit(PlayerQuitEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }

    @EventHandler
    private void playerKick(PlayerKickEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }


    @EventHandler
    private void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        Player killer = player.getKiller();

        //No drops
        event.setDroppedExp(0);
        event.getDrops().clear();

        //Death message
        if (killer != null) {
            if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(killer.getName())) {
                event.setDeathMessage(CWUtil.integrateColor((cwp.getPlayerClass() != null ? cwp.getPlayerClass().getClassClass().getColor() : "&8") + " &7was killed by the dragon!"));
            } else {
                event.setDeathMessage(CWUtil.integrateColor((cwp.getPlayerClass() != null ? cwp.getPlayerClass().getClassClass().getColor() : "&8") + " &7was killed by " + killer.getName() + "!"));
            }
        } else {
            event.setDeathMessage(CWUtil.integrateColor((cwp.getPlayerClass() != null ? cwp.getPlayerClass().getClassClass().getColor() : "&8") + player.getName() + " &7died!"));
        }

        //Dragon died.
        if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(player.getName())) {

            Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe dragon has been killed! &7======="));
            if (killer != null) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &3" + player.getName() + " &7is the &bDragonSlayer&7!"));
                //TODO: Set DragonSlayer.
            } else {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Couldn't find the killer so there is no DragonSlayer."));
            }
            dvz.getGM().releaseMonsters(false);
        }
    }

    @EventHandler
    private void respawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();
        if (!dvz.getGM().isStarted()) {
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
            }
            event.setRespawnLocation(spawnLoc);
            return;
        }
        final CWPlayer cwp = dvz.getPM().getPlayer(player);

        //Spawn at monster lobby.
        if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
            spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
        }
        event.setRespawnLocation(spawnLoc);

        new BukkitRunnable() {
            public void run() {
                if (dvz.getGM().isStarted()) {
                    if (cwp.isDwarf()) {
                        player.sendMessage(Util.formatMsg("&4&lYou have turned in to a monster!!!"));
                    }

                    cwp.reset();
                    cwp.setPlayerClass(DvzClass.MONSTER);
                    //TODO: Suicide check for last arg
                    cwp.giveClassItems(ClassType.MONSTER, false);
                }
            }
        }.runTaskLater(dvz, 5);
    }


    @EventHandler
    private void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        //Check for class item usage.
        for (DvzClass dvzClass : DvzClass.values()) {
            BaseClass c = dvzClass.getClassClass();
            if (c == null || c.getClassItem() == null || c.getClassItem().getType() != item.getType()) {
                continue;
            }
            if ((c.getClassItem().hasItemMeta() && !item.hasItemMeta()) || (!c.getClassItem().hasItemMeta() && item.hasItemMeta())) {
                continue;
            }
            if (item.hasItemMeta()) {
                if (!CWUtil.integrateColor(c.getDisplayName()).equalsIgnoreCase(CWUtil.integrateColor(item.getItemMeta().getDisplayName()))) {
                    continue;
                }
            }
            if (!dvz.getGM().isStarted()) {
                player.sendMessage(Util.formatMsg("&cThe game hasn't started yet!"));
                break;
            }
            if (dvzClass.getType() == ClassType.MONSTER && !dvz.getGM().isMonsters()) {
                player.sendMessage(Util.formatMsg("&cThe monsters haven't been released yet."));
                //TODO: Say game state and time remaining etc.
                break;
            }
            cwp.setClass(dvzClass);
            dvzClass.getClassClass().onEquipClass(player);
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if(!event.getBlock().getType().equals(Material.WEB)) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(dvz, Integer.parseInt(dvz.getAbilityCfg().getOption(Ability.WEB, "removeAfter")));

    }

}
