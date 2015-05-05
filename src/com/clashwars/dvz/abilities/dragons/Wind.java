package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.VectorUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

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

            @Override
            public void run() {
                ParticleEffect.CLOUD.display(0.2f, 0.2f, 0.2f, 0f, 1, locs.get(index).toLocation(player.getWorld()), 500);
                index++;
                if (index >= locs.size()) {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(dvz, 1, 1);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
