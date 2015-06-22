package com.clashwars.dvz.abilities.dragons.air;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.VectorUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Wind extends BaseAbility {

    public Wind() {
        super();
        ability = Ability.WIND;
        castItem = new DvzItem(Material.FEATHER, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("Wind.castAbility()[cd]", t);
            return;
        }

        final List<Vector> locs = VectorUtils.getPositionsInCone(Util.getDragonMouthPos(player.getLocation()).add(new Vector(0, 2, 0)), 20, 28, player.getLocation().getDirection(), true);

        new BukkitRunnable() {
            int index = 0;
            int particles = 1;

            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                for (int i = 0; i < particles; i++) {
                    Location loc = locs.get(index).toLocation(player.getWorld());

                    List<Entity> entities = CWUtil.getNearbyEntities(loc, 1f, null);
                    for (Entity e : entities) {
                        if (!(e instanceof Player) || e == player) {
                            continue;
                        }
                        if (!dvz.getPM().getPlayer((Player)e).isDwarf()) {
                            continue;
                        }
                        ((Player)e).damage(dvz.getGM().getDragonPower() * 2 - 1);
                        e.setVelocity(e.getVelocity().add( player.getLocation().getDirection().multiply(dvz.getGM().getDragonPower() * 0.6f) ));
                    }

                    ParticleEffect.CLOUD.display(0.1f, 0.1f, 0.1f, 0.1f, 1, loc, 500);

                    index++;
                    if (index >= locs.size()) {
                        cancel();
                        return;
                    }
                }
                particles += 4;
                dvz.logTimings("Wind.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 1, 1);
        dvz.logTimings("Wind.castAbility()", t);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
