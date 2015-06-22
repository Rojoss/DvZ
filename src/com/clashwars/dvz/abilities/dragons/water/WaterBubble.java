package com.clashwars.dvz.abilities.dragons.water;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class WaterBubble extends BaseAbility {

    public WaterBubble() {
        super();
        ability = Ability.WATER_BUBBLE;
        castItem = new DvzItem(Material.POTION, 1, (short)0, 198, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("WaterBubble.castAbility()[cd]", t);
            return;
        }

        player.setFlySpeed(0);
        player.setVelocity(new Vector(0,0,0));

        new BukkitRunnable() {
            int iterations = 0;
            float radius = 0.5f;

            final Location playerLoc = player.getLocation();

            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                iterations++;
                if (iterations > 20) {
                    radius += 0.5f;
                    CWUtil.createSphere(playerLoc, Material.STAINED_GLASS, (byte) 11, radius, false, false);

                    List<Entity> entities = CWUtil.getNearbyEntities(playerLoc, 5f, null);
                    for (Entity e : entities) {
                        if (e instanceof Player) {
                            if (!dvz.getPM().getPlayer((Player)e).isDwarf()) {
                                continue;
                            }
                            ((Player)e).setRemainingAir(160 - dvz.getGM().getDragonPower() * 40);
                        }
                    }

                    player.teleport(playerLoc.add(0, 6, 0));
                    player.setFlySpeed(dvz.getPM().getPlayer(player).getPlayerClass().getClassClass().getSpeed());

                    dvz.logTimings("WaterBubble.castAbilityRunnable()[completed]", t);
                    cancel();
                    return;
                }

                if (iterations <= 10) {
                    CWUtil.createSphere(playerLoc, Material.STATIONARY_WATER, (byte)0, radius, false, false);
                    radius += 0.5f;
                }

                List<Entity> entities = CWUtil.getNearbyEntities(playerLoc, 30f, null);
                for (Entity e : entities) {
                    if (!(e instanceof Player)) {
                        continue;
                    }
                    if (!dvz.getPM().getPlayer((Player)e).isDwarf()) {
                        continue;
                    }
                    double distance = e.getLocation().distance(playerLoc);
                    if (distance > 1) {
                        Vector dir = playerLoc.toVector().subtract(e.getLocation().toVector());
                        Vector v = CWUtil.lerp(dir.multiply(0.1f), dir, distance / 30);
                        e.setVelocity(v);
                        ParticleEffect.WATER_SPLASH.display(1,1,1,0,10,e.getLocation());
                    }
                }

                ParticleEffect.WATER_BUBBLE.display(5,5,5,0, 300, playerLoc);
                dvz.logTimings("WaterBubble.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 0, 5);
        dvz.logTimings("WaterBubble.castAbility()", t);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
