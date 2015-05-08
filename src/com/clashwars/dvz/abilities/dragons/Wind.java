package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.VectorUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class Wind extends DragonAbility {

    public Wind() {
        super();
        ability = Ability.WIND;
        castItem = new DvzItem(Material.FEATHER, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        final List<Vector> locs = VectorUtils.getPositionsInCone(player.getLocation().toVector(), 15, 20, player.getLocation().getDirection(), true);

        new BukkitRunnable() {
            int index = 0;
            int particles = 1;

            @Override
            public void run() {
                for (int i = 0; i < particles; i++) {
                    Location loc = locs.get(index).toLocation(player.getWorld());

                    List<Entity> entities = CWUtil.getNearbyEntities(loc, 1f, null);
                    for (Entity e : entities) {
                        if (!(e instanceof LivingEntity) || e == player) {
                            continue;
                        }
                        ((LivingEntity)e).damage(2);
                        e.setVelocity(e.getVelocity().add( player.getLocation().getDirection().multiply(2) ));
                    }

                    ParticleEffect.CLOUD.display(0.1f, 0.1f, 0.1f, 0f, 1, loc, 500);

                    index++;
                    if (index >= locs.size()) {
                        cancel();
                        return;
                    }
                }
                particles += 3;
            }
        }.runTaskTimer(dvz, 1, 1);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
