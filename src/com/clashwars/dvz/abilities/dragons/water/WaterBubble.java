package com.clashwars.dvz.abilities.dragons.water;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

                    List<Player> players = CWUtil.getNearbyPlayers(playerLoc, 5);
                    for (Player p : players) {
                        if (!dvz.getPM().getPlayer(p).isDwarf()) {
                            continue;
                        }
                        p.setRemainingAir(190 - dvz.getGM().getDragonPower() * 40);
                        new AbilityDmg(p, 0, ability, player);
                    }

                    dvz.logTimings("WaterBubble.castAbilityRunnable()[completed]", t);
                    cancel();
                    return;
                }

                if (iterations <= 10) {
                    CWUtil.createSphere(playerLoc, Material.STATIONARY_WATER, (byte)0, radius, false, false);
                    radius += 0.5f;
                }

                List<Player> players = CWUtil.getNearbyPlayers(playerLoc, 30f);
                for (Player p : players) {
                    if (!(p instanceof Player)) {
                        continue;
                    }
                    if (!dvz.getPM().getPlayer(p).isDwarf()) {
                        continue;
                    }
                    double distance = p.getLocation().distance(playerLoc);
                    if (distance > 1) {
                        Vector dir = playerLoc.toVector().subtract(p.getLocation().toVector());
                        Vector v = CWUtil.lerp(dir.multiply(0.1f), dir, distance / 30);
                        p.setVelocity(v);
                        ParticleEffect.WATER_SPLASH.display(1,1,1,0,10,p.getLocation());
                    }
                }

                ParticleEffect.WATER_BUBBLE.display(5,5,5,0, 300, playerLoc);
                dvz.logTimings("WaterBubble.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 0, 5);
        dvz.logTimings("WaterBubble.castAbility()", t);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
