package com.clashwars.dvz.abilities.dragons.fire;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FireFly extends BaseAbility {

    public FireFly() {
        super();
        ability = Ability.FIREFLY;
        castItem = new DvzItem(Material.BLAZE_POWDER, 1, (short)0, 198, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("FireFly.castAbility()[cd]", t);
            return;
        }

        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                iterations++;
                if (iterations > 20) {
                    cancel();
                    return;
                }

                List<Entity> entities = player.getNearbyEntities(3, 100, 3);
                for (Entity e : entities) {
                    if (e instanceof Player) {
                        if (!dvz.getPM().getPlayer((Player)e).isDwarf()) {
                            continue;
                        }
                        if (dvz.getGM().getDragonPower() == 1) {
                            new AbilityDmg((Player)e, 1, ability, player);
                        } else {
                            new AbilityDmg((Player)e, dvz.getGM().getDragonPower() * 2, ability, player);
                        }
                    }
                }


                float perc = ((float)dvz.getGM().getDragonPower() / 30);
                for (int x = -3; x < 3; x++) {
                    for (int z = -3; z < 3; z++) {
                        Block block = player.getWorld().getHighestBlockAt(player.getLocation().add(x, 0, z));

                        if (block.getType() == Material.AIR && CWUtil.randomFloat() < perc) {
                            block.setType(Material.FIRE);
                            final Location blockLoc = block.getLocation();
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (blockLoc.getBlock().getType() == Material.FIRE) {
                                        blockLoc.getBlock().setType(Material.AIR);
                                    }

                                }

                            }.runTaskLater(dvz, 200);
                        }
                        ParticleEffect.LAVA.display(0.7f, 0.7f, 0.7f, 0.1f, 3, block.getLocation(), 500);
                        if (CWUtil.randomFloat() < 0.2f) {
                            ParticleEffect.LAVA.display(1, 1, 1, 0.1f, 1, block.getLocation().add(0, CWUtil.random(0, player.getLocation().getBlockY() - block.getLocation().getBlockY()), 0), 500);
                        }
                    }
                }
                dvz.logTimings("FireFly.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 0, 8);
        dvz.logTimings("FireFly.castAbility()", t);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
