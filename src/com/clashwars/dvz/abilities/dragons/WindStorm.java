package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WindStorm extends DragonAbility {

    public WindStorm() {
        super();
        ability = Ability.WINDSTORM;
        castItem = new DvzItem(Material.STRING, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        //for (Entity entity : CWUtil.getNearbyEntities(triggerLoc, getFloatOption("distance"), Arrays.asList(new EntityType[]{EntityType.PLAYER}))) {
        new BukkitRunnable() {
            int iteration = 0;

            @Override
            public void run() {
                for (Player pl : Bukkit.getOnlinePlayers() ) {
                    if (pl == player) {
                        continue;
                    }

                    Vector v = RandomUtils.getRandomCircleVector();
                    float y;
                    if (iteration > 6) {
                        y = CWUtil.randomFloat() - 1;
                    } else {
                        y = CWUtil.randomFloat();
                    }
                    ParticleEffect.CLOUD.display(0.5f, 0.5f, 0.5f, 0f, 10, pl.getLocation());
                    pl.setVelocity(new Vector(v.getX() * 0.8f, y * 0.8f, v.getZ() * 0.8f));
                }

                iteration++;
                if (iteration > 10) {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(dvz, 8, 8);

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
