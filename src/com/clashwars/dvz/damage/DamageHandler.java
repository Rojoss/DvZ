package com.clashwars.dvz.damage;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.monsters.enderman.Pickup;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.damage.log.DamageLog;
import com.clashwars.dvz.damage.log.DamageLogEntry;
import com.clashwars.dvz.damage.types.*;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.player.PlayerSettings;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.util.Util;
import net.minecraft.server.v1_8_R2.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DamageHandler implements Listener {

    private DvZ dvz;

    public DamageHandler(DvZ dvz) {
        this.dvz = dvz;
    }


    @EventHandler
    private void customDmgTake(CustomDamageEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        //Update damage log
        List<DamageLog> dmgLogs = cwp.damageLogs;
        if (dmgLogs == null || dmgLogs.size() < 1) {
            cwp.damageLogs.add(new DamageLog(player.getUniqueId()));
        }
        dmgLogs.get(dmgLogs.size() -1).updateLog(event);

        if (cwp.isMonster()) {
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_MONSTER_DAMAGE_TAKEN, (float)event.getDamage());
        } else if (cwp.isDwarf()) {
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DWARF_DAMAGE_TAKEN, (float)event.getDamage());
        }

        BaseDmg dmg = event.getDmgClass();
        if (!(dmg instanceof MeleeDmg) && !(dmg instanceof RangedDmg) && !(dmg instanceof EnvironmentDmg)) {
            if (dmg.getDmg() > 0) {
                player.damage(0);
            }
        }

        OfflinePlayer damager = null;
        if (dmg instanceof MeleeDmg) {
            damager = ((MeleeDmg)dmg).getAttacker();
        }
        if (dmg instanceof RangedDmg) {
            damager = ((RangedDmg)dmg).getShooter();
        }
        if (dmg instanceof AbilityDmg) {
            damager = ((AbilityDmg)dmg).getCaster();
        }
        if (dmg instanceof CustomDmg) {
            damager = ((CustomDmg)dmg).getDamageSource();
        }

        if (damager != null) {
            List<DamageLog> dmgLogsDmger = dvz.getPM().getPlayer(damager).damageLogs;
            if (dmgLogsDmger == null || dmgLogsDmger.size() < 1) {
                dvz.getPM().getPlayer(damager).damageLogs.add(new DamageLog(damager.getUniqueId()));
            }
            dmgLogsDmger.get(dmgLogsDmger.size() -1).updateLog(event);

            if (cwp.isMonster()) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_DWARF_DAMAGE_DEALT, (float) event.getDamage());
            } else if (cwp.isDwarf()) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_MONSTER_DAMAGE_DEALT, (float)event.getDamage());
            } else if (dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(cwp.getName())) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_DRAGON_DAMAGE, (float)event.getDamage());
            }
        }
    }


    /* Cancel all damage and transform it into custom damage */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void damageTake(final EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        //event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) / 100 * 125);

        final double dmg = event.getDamage();
        final double finalDmg = event.getFinalDamage();

        final Player player = (Player)event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        event.setDamage(0);

        if (event instanceof  EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent entityDmgByEntityEvent = (EntityDamageByEntityEvent)event;

            if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (entityDmgByEntityEvent.getDamager() instanceof Player) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new MeleeDmg(player, finalDmg, (Player)entityDmgByEntityEvent.getDamager());
                        }
                    }.runTaskLater(dvz, 1);
                    return;
                }
                //TODO: MobDmg type with option to get owner of mob.
                //It can say something like Worstboy was killed by Kadowster's endermite.
            }

            if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                if (entityDmgByEntityEvent.getDamager() instanceof Projectile) {
                    final Projectile proj = (Projectile)entityDmgByEntityEvent.getDamager();
                    if (proj.getShooter() instanceof Player) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                new RangedDmg(player, finalDmg, (Player)proj.getShooter(), ((EntityDamageByEntityEvent) event).getDamager().getType());
                            }
                        }.runTaskLater(dvz, 1);
                    }
                    return;
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    new EnvironmentDmg(player, finalDmg, event.getCause());
                } else {
                    new EnvironmentDmg(player, dmg, event.getCause());
                }
            }
        }.runTaskLater(dvz, 1);
    }

    @EventHandler
    private void death(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        event.setDroppedExp(0);

        List<DamageLog> dmgLogs = cwp.damageLogs;
        DamageLog dmgLog = null;
        if (dmgLogs != null && dmgLogs.size() > 0) {
            dmgLog = cwp.damageLogs.get(dmgLogs.size() - 1);
        }

        OfflinePlayer killer = null;
        DamageLogEntry finalDamage = null;
        Long currTime = System.currentTimeMillis();
        if (dmgLog != null) {
            //Try find a killer from the damage log from the last 10 seconds.
            for (int i = dmgLog.log.size()-1; i >= 0; i--) {
                DamageLogEntry entry = dmgLog.log.get(i);
                if (!entry.dmgTaken) {
                    continue;
                }

                if (currTime - entry.timestamp > 10000) {
                    break;
                }
                if (entry.dmgClass instanceof MeleeDmg) {
                    finalDamage = entry;
                    killer = ((MeleeDmg)entry.dmgClass).getAttacker();
                    break;
                }
                if (entry.dmgClass instanceof RangedDmg) {
                    finalDamage = entry;
                    killer = ((RangedDmg)entry.dmgClass).getShooter();
                    break;
                }
                if (entry.dmgClass instanceof AbilityDmg) {
                    if (((AbilityDmg)entry.dmgClass).hasCaster()) {
                        finalDamage = entry;
                        killer = ((AbilityDmg)entry.dmgClass).getCaster();
                        break;
                    }
                }
                if (entry.dmgClass instanceof CustomDmg) {
                    if (((CustomDmg)entry.dmgClass).hasDmgSource()) {
                        finalDamage = entry;
                        killer = ((CustomDmg)entry.dmgClass).getDamageSource();
                        break;
                    }
                }
            }
            //If no killer was found the last 10 seconds use the last damage type.
            if (finalDamage == null) {
                finalDamage = dmgLog.log.get(dmgLog.log.size() -1);
            }
        }

        String deathMsg = player.getName() + " died";
        if (finalDamage != null) {
            deathMsg = finalDamage.dmgClass.getDeathMsg();
        }

        if (cwp.isMonster()) {
            broadcastDeathMessage(dmgLog, killer, ClassType.MONSTER, CWUtil.integrateColor("&4>> &7&o" + deathMsg + " &4<<"));
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_MONSTER_DEATHS, 1);

            if (killer != null) {
                CWPlayer cwk = dvz.getPM().getPlayer(killer);
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
            broadcastDeathMessage(dmgLog, killer, ClassType.DWARF, CWUtil.integrateColor("&6>> &7&o" + deathMsg + " &6<<"));
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DWARF_DEATHS, 1);

            if (killer != null && dvz.getGM().getDragonPlayer() != null && dvz.getGM().getDragonPlayer().getName().equals(killer.getName())) {
                dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DEATHS_BY_DRAGON, 1);
            }

            if (killer != null) {
                CWPlayer cwk = dvz.getPM().getPlayer(killer);
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
            broadcastDeathMessage(dmgLog, killer, ClassType.DRAGON, CWUtil.integrateColor("&5>> &7&o" + deathMsg + " &5<<"));

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
        }


        //Destroy shrines if not any dwarves left.
        //TODO: Move this to GameManager
        /*
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
        */


        //Save damage log and create new one.
        dmgLog.deathMsg = deathMsg;
        dmgLog.deathClass = cwp.getPlayerClass();
        dmgLog.deathTime = System.currentTimeMillis();
        cwp.damageLogs.set(cwp.damageLogs.size()-1, dmgLog);
        dvz.getPM().getPlayer(event.getEntity()).damageLogs.add(new DamageLog(event.getEntity().getUniqueId()));


        //Instant respawning.
        //player.spigot().respawn();
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
            if (entry.dmgClass instanceof MeleeDmg) {
                damagers.add(((MeleeDmg) entry.dmgClass).getAttacker().getUniqueId());
                break;
            }
            if (entry.dmgClass instanceof RangedDmg) {
                damagers.add(((RangedDmg)entry.dmgClass).getShooter().getUniqueId());
                break;
            }
            if (entry.dmgClass instanceof AbilityDmg) {
                if (((AbilityDmg)entry.dmgClass).hasCaster()) {
                    damagers.add(((AbilityDmg)entry.dmgClass).getCaster().getUniqueId());
                    break;
                }
            }
            if (entry.dmgClass instanceof CustomDmg) {
                if (((CustomDmg)entry.dmgClass).hasDmgSource()) {
                    damagers.add(((CustomDmg)entry.dmgClass).getDamageSource().getUniqueId());
                    break;
                }
            }
        }

        Collection<Player> players = (Collection<Player>)dvz.getServer().getOnlinePlayers();
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
