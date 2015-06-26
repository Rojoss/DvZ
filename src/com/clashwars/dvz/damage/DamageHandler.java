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

import java.util.List;
import java.util.Set;

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

        if (damager != null) {
            if (cwp.isMonster()) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_DWARF_DAMAGE_DEALT, (float) event.getDamage());
            } else if (cwp.isDwarf()) {
                dvz.getSM().changeLocalStatVal(damager.getUniqueId(), StatType.COMBAT_MONSTER_DAMAGE_DEALT, (float)event.getDamage());
            }
        }
    }


    /* Cancel all damage and transform it into custom damage */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void damageTake(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, event.getDamage(EntityDamageEvent.DamageModifier.MAGIC) / 4);

        double dmg = event.getDamage();
        double finalDmg = event.getFinalDamage();

        Player player = (Player)event.getEntity();
        EntityDamageEvent.DamageCause cause = event.getCause();

        event.setDamage(0);

        if (event instanceof  EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDmgByEntityEvent = (EntityDamageByEntityEvent)event;

            if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                if (entityDmgByEntityEvent.getDamager() instanceof Player) {
                    new MeleeDmg(player, finalDmg, (Player)entityDmgByEntityEvent.getDamager());
                    return;
                }
                //TODO: MobDmg type with option to get owner of mob.
                //It can say something like Worstboy was killed by Kadowster's endermite.
            }

            if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
                if (entityDmgByEntityEvent.getDamager() instanceof Projectile) {
                    Projectile proj = (Projectile)entityDmgByEntityEvent.getDamager();
                    if (proj.getShooter() instanceof Player) {
                        new RangedDmg(player, finalDmg, (Player)proj.getShooter(), ((EntityDamageByEntityEvent) event).getDamager().getType());
                    }
                    return;
                }
            }
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            new EnvironmentDmg(player, finalDmg, event.getCause());
        } else {
            new EnvironmentDmg(player, dmg, event.getCause());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void damageTakeByEntity(EntityDamageByEntityEvent event) {
        //event.setCancelled(true);
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
            }
            //If no killer was found the last 10 seconds use the last damage type.
            if (finalDamage == null) {
                finalDamage = dmgLog.log.get(dmgLog.log.size() -1);
            }
        }

        String deathMsg = player.getName() + " died";
        if (finalDamage != null) {
            if (finalDamage.dmgClass instanceof MeleeDmg) {
                deathMsg = ((MeleeDmg)finalDamage.dmgClass).getDeathMsg();
            }
            if (finalDamage.dmgClass instanceof RangedDmg) {
                deathMsg = ((RangedDmg)finalDamage.dmgClass).getDeathMsg();
            }
            if (finalDamage.dmgClass instanceof AbilityDmg) {
                deathMsg = ((AbilityDmg)finalDamage.dmgClass).getDeathMsg();
            }
            if (finalDamage.dmgClass instanceof EnvironmentDmg) {
                deathMsg = ((EnvironmentDmg)finalDamage.dmgClass).getDeathMsg();
            }
            if (finalDamage.dmgClass instanceof CustomDmg) {
                deathMsg = ((CustomDmg)finalDamage.dmgClass).getDeathMsg();
            }
        }

        if (cwp.isMonster()) {
            dvz.getServer().broadcastMessage(CWUtil.integrateColor("&4>> &7&o" + deathMsg + " &4<<"));
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
            dvz.getServer().broadcastMessage(CWUtil.integrateColor("&6>> &7&o" + deathMsg + " &6<<"));
            dvz.getSM().changeLocalStatVal(player, StatType.COMBAT_DWARF_DEATHS, 1);

            if (killer != null && dvz.getGM().getDragonPlayer().getName().equals(killer.getName())) {
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
                dvz.getServer().broadcastMessage(Util.formatMsg("&d&lThe DragonSlayer died!"));
            }
        } else if (cwp.getPlayerClass().getType() == ClassType.DRAGON) {
            dvz.getServer().broadcastMessage(CWUtil.integrateColor("&5>> &7&o" + deathMsg + " &5<<"));

            //First dragon death
            if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(player.getName())) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe dragon has been killed! &7======="));
                if (killer != null) {
                    dvz.getSM().changeLocalStatVal(killer.getUniqueId(), StatType.COMBAT_DRAGON_KILLS, 1);
                    Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &3" + killer.getName() + " &7is the &bDragonSlayer&7!"));
                    if (killer.isOnline()) {
                        dvz.getGM().setDragonSlayer((Player)killer);
                    }
                } else {
                    Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Couldn't find the killer so there is no DragonSlayer."));
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


        //New damage log.
        dvz.getPM().getPlayer(event.getEntity()).damageLogs.add(new DamageLog(event.getEntity().getUniqueId()));

        //Instant respawning.
        dvz.getServer().getScheduler().scheduleSyncDelayedTask(dvz, new Runnable() {
            public void run() {
                if (player.isDead()) {
                    ((CraftPlayer) player).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));
                }
            }
        });
    }


    private static void getDamage(EntityDamageEvent.DamageCause cause, double baseDmg) {

    }
}
