package com.clashwars.dvz.listeners;

import com.clashwars.cwcore.damage.BaseDmg;
import com.clashwars.cwcore.damage.Iattacker;
import com.clashwars.cwcore.damage.log.DamageLog;
import com.clashwars.cwcore.damage.log.DamageLogEntry;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.damage.types.CustomDmg;
import com.clashwars.cwcore.damage.types.MeleeDmg;
import com.clashwars.cwcore.damage.types.RangedDmg;
import com.clashwars.cwcore.events.CustomDamageEvent;
import com.clashwars.cwcore.events.CustomDeathEvent;
import com.clashwars.cwcore.events.PlayerLeaveEvent;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.monsters.enderman.Pickup;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.player.PlayerSettings;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


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


    @EventHandler
    private void customDmgTake(CustomDamageEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (cwp.isMonster()) {
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_MONSTER_DAMAGE_TAKEN, (float)event.getDamage());
        } else if (cwp.isDwarf()) {
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DWARF_DAMAGE_TAKEN, (float)event.getDamage());
        }

        BaseDmg dmg = event.getDmgClass();
        OfflinePlayer damager = null;
        if (dmg instanceof Iattacker) {
            damager = ((Iattacker)dmg).getAttacker();
        }

        if (damager != null) {
            if (cwp.isMonster()) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_DWARF_DAMAGE_DEALT, (float) event.getDamage());
            } else if (cwp.isDwarf()) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_MONSTER_DAMAGE_DEALT, (float)event.getDamage());
            } else if (dvz.getGM().getDragonPlayer() != null && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(cwp.getName())) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_DRAGON_DAMAGE, (float)event.getDamage());
            }
        }
    }


    @EventHandler
    private void customDeath(CustomDeathEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        OfflinePlayer killer = event.getKiller();
        CWPlayer cwk = dvz.getPM().getPlayer(killer);

        if (cwp.isMonster()) {
            broadcastDeathMessage(event.getDamageLog(), killer, ClassType.MONSTER, CWUtil.integrateColor("&4>> &7&o" + event.getDeathMessage() + " &4<<"));
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_MONSTER_DEATHS, 1);

            if (killer != null) {
                if (cwk.isDwarf()) {
                    dvz.getSM().changeLocalStatVal(killer.getUniqueId(), StatType.COMBAT_MONSTER_KILLS, 1);
                }
            }

            //Reset witch/villager data
            //TODO: Move this to witch/villager class
            if (cwp.getPlayerClass() != null && (cwp.getPlayerClass() == DvzClass.WITCH || cwp.getPlayerClass() == DvzClass.VILLAGER)) {
                cwp.getPlayerData().setbombUsed(false);
                cwp.getPlayerData().setBuffUsed(false);
            }

            //Enderman died. (Drop picked up player)
            //TODO: Move this to enderman class
            if (cwp.getPlayerClass() == DvzClass.ENDERMAN) {
                if (Pickup.pickupRunnables.containsKey(cwp.getUUID())) {
                    Pickup.pickupRunnables.get(cwp.getUUID()).died = true;
                }
            }
        } else if (cwp.isDwarf()) {
            broadcastDeathMessage(event.getDamageLog(), killer, ClassType.DWARF, CWUtil.integrateColor("&6>> &7&o" + event.getDeathMessage() + " &6<<"));
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DWARF_DEATHS, 1);

            if (killer != null && dvz.getGM().getDragonPlayer() != null && dvz.getGM().getDragonPlayer().getName().equals(killer.getName())) {
                dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DEATHS_BY_DRAGON, 1);
            }

            if (killer != null) {
                if (cwk.isMonster()) {
                    dvz.getSM().changeLocalStatVal(killer.getUniqueId(), StatType.COMBAT_DWARF_KILLS, 1);
                }
            }

            //Dragon slayer died
            if (dvz.getGM().getDragonSlayer() != null && dvz.getGM().getDragonSlayer().getName().equalsIgnoreCase(player.getName())) {
                dvz.getGM().resetDragonSlayer();
                Util.broadcast("&d&lThe DragonSlayer died!");
            }
        } else if (cwp.getPlayerClass().getType() == ClassType.DRAGON) {
            broadcastDeathMessage(event.getDamageLog(), killer, ClassType.DRAGON, CWUtil.integrateColor("&5>> &7&o" + event.getDeathMessage() + " &5<<"));

            //First dragon death
            if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer() != null && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(player.getName())) {
                Util.broadcast("&7======= &a&lThe dragon has been killed! &7=======");
                if (killer != null) {
                    dvz.getSM().changeLocalStatVal(killer.getUniqueId(), StatType.COMBAT_DRAGON_KILLS, 1);
                    Util.broadcast("&a- &3" + killer.getName() + " &7is the &bDragonSlayer&7!");
                    if (killer.isOnline()) {
                        dvz.getGM().setDragonSlayer((Player)killer);
                    }
                } else {
                    Util.broadcast("&a- &7Couldn't find the killer so there is no DragonSlayer.");
                }
                dvz.getGM().releaseMonsters(false);
            }
        } else {
            //PvP arena
            broadcastDeathMessage(event.getDamageLog(), killer, ClassType.DWARF, CWUtil.integrateColor("&6>> &7&o" + event.getDeathMessage() + " &6<<"));

            if (killer != null && killer.isOnline()) {
                Player killerP = (Player)killer;
                if (dvz.getPM().getPlayer(killerP).isPvping()) {
                    new CWItem(PotionType.INSTANT_HEAL, true, 1).addPotionEffect(PotionEffectType.HEAL, 2, 1).giveToPlayer(killerP);
                }
            }
        }


        //Destroy shrines if not any dwarves left.
        //TODO: Move this to GameManager
        /*
        final ShrineType[] shrineTypes = new ShrineType[] {ShrineType.WALL, ShrineType.KEEP_1, ShrineType.KEEP_2};
        new BukkitRunnable() {
            int index = 0;
            @Override
            public void run() {
                List<CWPlayer> dwarvesLeft = cwc.getPM().getPlayers(ClassType.DWARF, true, false);
                if (dwarvesLeft == null || dwarvesLeft.size() == 0) {
                    Set<ShrineBlock> shrineBlocks = cwc.getGM().getShrineBlocks(shrineTypes[index]);
                    for (ShrineBlock shrineBlock : shrineBlocks) {
                        if (shrineBlock != null && shrineBlock.isDestroyed() == false) {
                            cwc.getGM().getShrineBlock(shrineBlock.getLocation()).damage(500);
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
        }.runTaskTimer(cwc, 60, 60);
        */
    }

    private void broadcastDeathMessage(DamageLog dmgLog, OfflinePlayer killer, ClassType classType, String deathMsg) {
        if (dmgLog == null || dmgLog.logOwner == null) {
            Util.broadcast(deathMsg);
            return;
        }

        //Dragon messages for everyone
        if (classType == ClassType.DRAGON) {
            Util.broadcast(deathMsg);
            return;
        }

        //Get all damagers for assists.
        List<UUID> damagers = new ArrayList<UUID>();
        for (DamageLogEntry entry : dmgLog.log) {
            if (entry.dmgClass instanceof Iattacker) {
                if (((Iattacker)entry.dmgClass).hasAttacker()) {
                    damagers.add(((Iattacker)entry.dmgClass).getAttacker().getUniqueId());
                }
            }
        }

        Collection<Player> players = (Collection<Player>) dvz.getServer().getOnlinePlayers();
        for (Player player : players) {
            String msg = deathMsg.replace(player.getName(), "&a" + player.getName() + "&7");
            PlayerSettings settings = dvz.getSettingsCfg().getSettings(player.getUniqueId());
            if (settings != null) {
                //No messages at all
                if (settings.dwarfDeathMessages == 0 && classType == ClassType.DWARF) {
                    continue;
                }
                if (settings.monsterDeathMessages == 0 && classType == ClassType.MONSTER) {
                    continue;
                }

                //All messages
                if (settings.dwarfDeathMessages == 1 && classType == ClassType.DWARF) {
                    player.sendMessage(CWUtil.integrateColor(msg));
                    continue;
                }
                if (settings.monsterDeathMessages == 1 && classType == ClassType.MONSTER) {
                    player.sendMessage(CWUtil.integrateColor(msg));
                    continue;
                }

                //Personal kills/deaths/assists only
                if (settings.dwarfDeathMessages == 2 && classType == ClassType.DWARF
                        && ((killer != null && player.getUniqueId().equals(killer.getUniqueId())) || player.getUniqueId().equals(dmgLog.logOwner) || damagers.contains(player.getUniqueId()))) {
                    player.sendMessage(CWUtil.integrateColor(msg));
                    continue;
                }
                if (settings.monsterDeathMessages == 2 && classType == ClassType.MONSTER
                        && ((killer != null && player.getUniqueId().equals(killer.getUniqueId())) || player.getUniqueId().equals(dmgLog.logOwner) || damagers.contains(player.getUniqueId()))) {
                    player.sendMessage(CWUtil.integrateColor(msg));
                    continue;
                }

                //Personal kills/deaths only
                if (settings.dwarfDeathMessages == 3 && classType == ClassType.DWARF
                        && ((killer != null && player.getUniqueId().equals(killer.getUniqueId())) || player.getUniqueId().equals(dmgLog.logOwner))) {
                    player.sendMessage(CWUtil.integrateColor(msg));
                    continue;
                }
                if (settings.monsterDeathMessages == 3 && classType == ClassType.MONSTER
                        && ((killer != null && player.getUniqueId().equals(killer.getUniqueId())) || player.getUniqueId().equals(dmgLog.logOwner))) {
                    player.sendMessage(CWUtil.integrateColor(msg));
                    continue;
                }
            } else {
                player.sendMessage(CWUtil.integrateColor(msg));
            }
        }
    }

}
