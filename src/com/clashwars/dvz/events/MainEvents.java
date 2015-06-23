package com.clashwars.dvz.events;

import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.Enjin;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.monsters.enderman.Pickup;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.mysql.MySQL;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.stats.internal.StatType;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Set;

public class MainEvents implements Listener {

    private final DvZ dvz;
    private GameManager gm;

    public MainEvents(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
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
        Long t = System.currentTimeMillis();
        final Player player = event.getPlayer();
        final CWPlayer cwp = dvz.getPM().getPlayer(player);
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
                //player.setResourcePack("http://web.clashwars.com/ResourcePack/CWDvZ.zip");
            }
        }.runTaskLater(dvz, 10);


        //Sync user/character with database ASYNC
        if (dvz.getSql() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Long t = System.currentTimeMillis();
                    try {
                        //Get the enjin ID with the Enjin API.
                        String enjinID = Enjin.getUserIdByCharacter(player.getName(), false);
                        int userID = -1;
                        int charID = -1;
                        if (enjinID == null || enjinID.isEmpty()) {
                            //No website account found (Do nothing but send a message)
                            if (dvz.getSettingsCfg().getSettings(player.getUniqueId()).enjinWarning) {
                                player.sendMessage(CWUtil.integrateColor("&4&lNOT LINKED&8&l: &4Your character isn't linked with the website."));
                                player.sendMessage(CWUtil.integrateColor("&4This can have one of the following reasons:"));
                                player.sendMessage(CWUtil.integrateColor("&81. &cYou haven't joined the website."));
                                player.sendMessage(CWUtil.integrateColor("    &7Please join the website first: &9http://clashwars.com"));
                                player.sendMessage(CWUtil.integrateColor("&82. &cYou don't have this character added to your profile."));
                                player.sendMessage(CWUtil.integrateColor("    &7Follow this step by step tutorial! &9http://goo.gl/BrckMP"));
                                player.sendMessage(CWUtil.integrateColor("    &7After adding your character also add it to this server!"));
                                player.sendMessage(CWUtil.integrateColor("&83. &cSomething went wrong with syncing your account."));
                                player.sendMessage(CWUtil.integrateColor("    &7Try again later... :D (Or contact a staff member!)"));
                                player.sendMessage(CWUtil.integrateColor("&a&lYou can still play! &7(Some advanced features might be locked)"));
                            }
                        } else {
                            //Create/get the User based on enjin ID. (if user has multiple characters and already registered one there will be an user already,
                            //If the player has one character or multiple and it's the first he joins with it will need to create a new user based on the enjin ID)
                            try {
                                Statement selectUserS = dvz.getSql().createStatement();
                                ResultSet userResult = selectUserS.executeQuery("SELECT user_id FROM Users WHERE enjin_id='" + enjinID + "';");

                                if (!userResult.next()) {
                                    //New user
                                    player.sendMessage(CWUtil.integrateColor("&3It seems like this is the first time you join this server!"));
                                    player.sendMessage(CWUtil.integrateColor("&3We have detected your website account and linked this."));
                                    player.sendMessage(CWUtil.integrateColor("&3Please go to &bhttp://clashwars.com/profile/" + enjinID));
                                    player.sendMessage(CWUtil.integrateColor("&3If this isn't your profile please type &b/enjinprofile."));

                                    Statement newUserS = dvz.getSql().createStatement();
                                    int added = newUserS.executeUpdate("INSERT INTO Users (enjin_id) VALUES ('" + enjinID + "');");
                                    //Get the new added user ID (don't know a better to get this)
                                    if (added > 0) {
                                        userResult = selectUserS.executeQuery("SELECT user_id FROM Users WHERE enjin_id='" + enjinID + "';");
                                        if (userResult.next()) {
                                            userID = userResult.getInt("user_id");
                                        }
                                    }
                                } else {
                                    //Existing user
                                    userID = userResult.getInt("user_id");
                                }

                            } catch (SQLException e) {
                                dvz.log("Failed to retrieve user from database!");
                                e.printStackTrace();
                            }
                        }
                        cwp.setUserID(userID);

                        //Then we get the character
                        //If the character isn't added, add it. If it is save the id's in CWPLayer and update name etc if needed.
                        Statement selectS = dvz.getSql().createStatement();
                        ResultSet charResult = selectS.executeQuery("SELECT char_id,user_id,username,prev_names FROM Characters WHERE uuid='" + player.getUniqueId().toString() + "';");
                        if (!charResult.next()) {
                            //Create new character
                            try {
                                Statement newCharS = dvz.getSql().createStatement();
                                int added = 0;
                                if (enjinID != null && !enjinID.isEmpty() && userID >= 1) {
                                    added = newCharS.executeUpdate("INSERT INTO Characters (user_id,uuid,username) VALUES ('" + userID + "','" + player.getUniqueId().toString() + "','" + player.getName() + "');");
                                } else {
                                    added = newCharS.executeUpdate("INSERT INTO Characters (uuid,username) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "');");
                                }

                                if (added > 0) {
                                    charResult = selectS.executeQuery("SELECT char_id FROM Characters WHERE uuid='" + player.getUniqueId().toString() + "';");
                                    if (charResult.next()) {
                                        charID = charResult.getInt("char_id");
                                        cwp.setCharID(charID);
                                    }
                                }
                            } catch (SQLException e) {
                                dvz.log("Failed to insert new character in the database!");
                                e.printStackTrace();
                            }
                        } else {
                            //Existing character
                            cwp.setCharID(charResult.getInt("char_id"));

                            //Check for username change.
                            if (!player.getName().equals(charResult.getString("username"))) {
                                player.sendMessage(Util.formatMsg("&6We have changed your username from &7&l" + charResult.getString("username") + " &6to &a&l" + player.getName() + "&6!"));
                                String prevNames = charResult.getString("prev_names");
                                if (prevNames == null || prevNames.isEmpty()) {
                                    prevNames = charResult.getString("username");
                                } else {
                                    prevNames += "," + charResult.getString("username");
                                }

                                //Update DB with new username.
                                try {
                                    if (userID > 0) {
                                        PreparedStatement updatePS = dvz.getSql().prepareStatement("UPDATE Characters SET last_joined=?,username=?,prev_names=?,user_id=? WHERE char_id=?;");
                                        updatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updatePS.setString(2, player.getName());
                                        updatePS.setString(3, prevNames);
                                        updatePS.setInt(4, userID);
                                        updatePS.setInt(5, charResult.getInt("char_id"));
                                        updatePS.executeUpdate();
                                    } else {
                                        PreparedStatement updatePS = dvz.getSql().prepareStatement("UPDATE Characters SET last_joined=?,username=?,prev_names=? WHERE char_id=?;");
                                        updatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updatePS.setString(2, player.getName());
                                        updatePS.setString(3, prevNames);
                                        updatePS.setInt(4, charResult.getInt("char_id"));
                                        updatePS.executeUpdate();
                                    }
                                } catch (SQLException e) {
                                    player.sendMessage(Util.formatMsg("&cFailed at updating your username in the database."));
                                    player.sendMessage(Util.formatMsg("&cIf this message keeps apearing please contact a staff member!"));
                                    dvz.log("Failed to update username in change in database!");
                                    e.printStackTrace();
                                }
                            } else {
                                //Update just the last joined field and user ID if it's set.
                                try {
                                    if (userID > 0) {
                                        PreparedStatement updateDatePS = dvz.getSql().prepareStatement("UPDATE Characters SET last_joined=?, user_id=? WHERE char_id=?;");
                                        updateDatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updateDatePS.setInt(2, userID);
                                        updateDatePS.setInt(3, charResult.getInt("char_id"));
                                        updateDatePS.executeUpdate();
                                    } else {
                                        PreparedStatement updateDatePS = dvz.getSql().prepareStatement("UPDATE Characters SET last_joined=? WHERE char_id=?;");
                                        updateDatePS.setTimestamp(1, MySQL.getCurrentTimeStamp());
                                        updateDatePS.setInt(2, charResult.getInt("char_id"));
                                        updateDatePS.executeUpdate();
                                    }
                                } catch (SQLException e) {
                                    dvz.log("Failed to update last joined value for player!");
                                }
                            }
                        }
                    } catch(SQLException e) {
                        dvz.log("Failed to sync userdata with MySQL database!");
                    }
                    dvz.logTimings("MainEvents.playerJoin()[async mysql sync task]", t);
                }
            }.runTaskAsynchronously(dvz);
        }
        dvz.logTimings("MainEvents.playerJoin()", t);
    }


    @EventHandler
    private void death(PlayerDeathEvent event) {
        Long t = System.currentTimeMillis();
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
                dvz.getSM().changeLocalStatVal(player, StatType.GENERAL_DEATHS_BY_DRAGON, 1);
            } else {
                event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7was killed by " + killer.getName() + "!"));
            }
        } else {
            event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7died!"));
        }

        if (cwp.getPlayerClass().getType() == ClassType.MONSTER) {
            dvz.getSM().changeLocalStatVal(player, StatType.MONSTER_DEATHS, 1);
        } else if (cwp.getPlayerClass().getType() == ClassType.DWARF) {
            dvz.getSM().changeLocalStatVal(player, StatType.DWARF_DEATHS, 1);
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

        //Destroy shrines if not any dwarves left.
        final ShrineType[] shrineTypes = new ShrineType[] {ShrineType.WALL, ShrineType.KEEP_1, ShrineType.KEEP_2};
        new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                List<CWPlayer> dwarvesLeft = dvz.getPM().getPlayers(ClassType.DWARF, true, false);
                if (dwarvesLeft == null || dwarvesLeft.size() == 0) {
                    Set<ShrineBlock> shrineBlocks = dvz.getGM().getShrineBlocks(shrineTypes[index]);
                    for (ShrineBlock shrineBlock : shrineBlocks) {
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
        dvz.logTimings("MainEvents.death()", t);
    }


    @EventHandler
    private void respawn(PlayerRespawnEvent event) {
        Long t = System.currentTimeMillis();
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
        dvz.logTimings("MainEvents.respawn()", t);
    }
}
