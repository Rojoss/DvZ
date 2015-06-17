package com.clashwars.dvz.events;

import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.monsters.enderman.Pickup;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import net.minecraft.server.v1_8_R2.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class MainEvents implements Listener {

    private final DvZ dvz;
    private GameManager gm;

    public MainEvents(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
    }


    @EventHandler
    private void levelUp(CWPlayer.ClassLevelupEvent event) {
        CWPlayer cwp = event.getCWPlayer();
        cwp.sendMessage(Util.formatMsg("&a&lLEVEL UP! &8[&7Dwarf abilities will be available soon&8]"));
        //TODO: Give dwarf ability to player.
    }


    @EventHandler
    private void playerQuit(PlayerQuitEvent event) {
        //Save when quiting.
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
        dvz.getGM().calculateMonsterPerc();
    }

    @EventHandler
    private void playerKick(PlayerKickEvent event) {
        //Save when being kicked.
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
        dvz.getGM().calculateMonsterPerc();
    }


    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();

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
            if (gm.isDwarves()) {
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
            } else if (gm.getState() == GameState.CLOSED) {
                //Player joined after the game is closed.
                player.sendMessage(Util.formatMsg("&cThere is no &4DvZ &cright now!"));
                subtitleStr = "&c&lThere is &4&lno DvZ &c&lright now.";
            }
        }
        dvz.getGM().calculateMonsterPerc();

        //Send title and tab list format.
        Title title = new Title(titleStr, subtitleStr, 10, 100, 30);
        title.setTimingsToTicks();
        title.send(player);

        CWUtil.setTab(player, " &8======== &6&lDwarves &2VS &c&lZombies &8========", " &6INFO &8>>> &9&lclashwars.com/info &8<<< &6INFO");

        //Teleport player
        final Location spawnLocFinal = spawnLoc;
        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(spawnLocFinal);
                player.setResourcePack("http://web.clashwars.com/ResourcePack/CWDvZ.zip");
            }
        }.runTaskLater(dvz, 10);
    }


    @EventHandler
    private void death(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        Player killer = player.getKiller();

        event.setDroppedExp(0);

        //Death message
        String prefix = "&8";
        if (cwp.getPlayerClass() != null) {
            if (cwp.getPlayerClass().getType() == ClassType.DWARF) {
                prefix = "&6";
            } else {
                prefix = "&c";
            }
        }
        if (killer != null) {
            if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(killer.getName())) {
                event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7was killed by the dragon!"));
            } else {
                event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7was killed by " + killer.getName() + "!"));
            }
        } else {
            event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7died!"));
        }

        //Enderman died. (Drop picked up player)
        if (cwp.getPlayerClass() == DvzClass.ENDERMAN) {
            if (Pickup.pickupRunnables.containsKey(cwp.getUUID())) {
                Pickup.pickupRunnables.get(cwp.getUUID()).died = true;
            }
        }

        //Reset witch/villager data
        if (cwp.getPlayerClass() != null && (cwp.getPlayerClass() == DvzClass.WITCH || cwp.getPlayerClass() == DvzClass.VILLAGER)) {
            cwp.getPlayerData().setbombUsed(false);
            cwp.getPlayerData().setBuffUsed(false);
        }

        //Dragon died.
        if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(player.getName())) {

            Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe dragon has been killed! &7======="));
            if (killer != null) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &3" + killer.getName() + " &7is the &bDragonSlayer&7!"));
                gm.setDragonSlayer(killer);
            } else {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Couldn't find the killer so there is no DragonSlayer."));
            }
            dvz.getGM().releaseMonsters(false);
        }

        //DragonSlayer died.
        if (dvz.getGM().getDragonSlayer() != null && dvz.getGM().getDragonSlayer().getName().equalsIgnoreCase(player.getName())) {
            dvz.getGM().resetDragonSlayer();
            dvz.getServer().broadcastMessage(Util.formatMsg("&d&lThe DragonSlayer died!"));
        }

        ClassType playerClass = dvz.getPM().getPlayer(player).getPlayerClass().getType();

        final ShrineType[] shrineTypes = new ShrineType[] {ShrineType.WALL, ShrineType.KEEP_1, ShrineType.KEEP_2};
        new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                List<CWPlayer> dwarvesLeft = dvz.getPM().getPlayers(ClassType.DWARF, true, false);
                if (dwarvesLeft == null || dwarvesLeft.size() == 0) {
                    for (ShrineBlock shrineBlock : dvz.getGM().getShrineBlocks(shrineTypes[index])) {
                        if (shrineBlock != null && shrineBlock.isDestroyed() == false) {
                            dvz.getGM().getShrineBlock(shrineBlock.getLocation()).damage(500);
                        }
                    }

                    index++;
                    if (index >= 3) {
                        cancel();
                        return;
                    }
                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(dvz, 60, 60);


        //Instant respawning.
        dvz.getServer().getScheduler().scheduleSyncDelayedTask(dvz, new Runnable() {
            public void run() {
                if (player.isDead()) {
                    ((CraftPlayer) player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
                }
            }
        });
    }


    @EventHandler
    private void respawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        //Get the respawn location and get the active map.
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();
        if (!dvz.getGM().isStarted()) {
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
            }
            event.setRespawnLocation(spawnLoc);
            return;
        }
        final CWPlayer cwp = dvz.getPM().getPlayer(player);

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

        new BukkitRunnable() {
            public void run() {
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
            }
        }.runTaskLater(dvz, 15);
    }
}
