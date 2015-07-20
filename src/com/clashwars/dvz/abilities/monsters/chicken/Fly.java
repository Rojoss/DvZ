package com.clashwars.dvz.abilities.monsters.chicken;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.listeners.custom.GameResetEvent;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fly extends BaseAbility {

    public static Map<UUID, BukkitTask> flyingPlayers = new HashMap<UUID, BukkitTask>();

    public Fly() {
        super();
        ability = Ability.FLY;
        castItem = new DvzItem(Material.FEATHER, 1, (short)0, displayName, 200, -1);

        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                for (UUID uuid : flyingPlayers.keySet()) {
                    Player player = dvz.getServer().getPlayer(uuid);
                    if (player == null || !player.isOnline() || player.isDead()) {
                        flyingPlayers.get(uuid).cancel();
                        flyingPlayers.remove(uuid);
                    }
                    if (i % 5 == 0) {
                        if (player.getLocation().getY() > player.getWorld().getHighestBlockYAt(player.getLocation()) + 20) {
                            player.setVelocity(player.getVelocity().add(new Vector(0,-1,0)));
                        }
                    }
                    ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.2f, 0.3f, 0, 1, player.getLocation(), 500);
                    i++;
                }
            }
        }.runTaskTimer(dvz, 1, 1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        if (flyingPlayers.containsKey(player.getUniqueId())) {
            return;
        }

        player.setVelocity(player.getVelocity().add(new Vector(0,0.5f,0)));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 2);

        flyingPlayers.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null) {
                    return;
                }

                flyingPlayers.remove(player.getUniqueId());
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        }.runTaskLater(dvz, (int) dvz.getGM().getMonsterPower(100, 400)));
    }

    @EventHandler
    private void playerDeath(PlayerDeathEvent event) {
        if (flyingPlayers.containsKey(event.getEntity().getUniqueId())) {
            flyingPlayers.get(event.getEntity().getUniqueId()).cancel();
            flyingPlayers.remove(event.getEntity().getUniqueId());
        }
    }

    @EventHandler
    private void playerLeave(PlayerQuitEvent event) {
        if (flyingPlayers.containsKey(event.getPlayer().getUniqueId())) {
            flyingPlayers.get(event.getPlayer().getUniqueId()).cancel();
            flyingPlayers.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void gameReset(GameResetEvent event) {
        for (BukkitTask flyingTask : flyingPlayers.values()) {
            flyingTask.cancel();
        }
        flyingPlayers.clear();
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        for (BukkitTask flyingTask : flyingPlayers.values()) {
            flyingTask.cancel();
        }
        flyingPlayers.clear();
    }

    @EventHandler
    private void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        if (dvz.getPM().getPlayer(((Player)event.getEntity())).getPlayerClass() == DvzClass.CHICKEN) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
