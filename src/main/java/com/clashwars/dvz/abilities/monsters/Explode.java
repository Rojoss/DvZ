package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Explode extends MobAbility {

    public Explode() {
        super();
        ability = Ability.EXPLODE;
        castItem = new DvzItem(Material.SULPHUR, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(final Player player, final Location triggerLoc) {
        new BukkitRunnable() {
            int x = 0;

            @Override
            public void run() {
                x += 5;
                float power = getFloatOption("powerpersec") * x;

                if (power < getFloatOption("minpower")) {
                    return;
                }

                if (power > getFloatOption("maxpower")) {
                    power = getFloatOption("maxpower");
                }

                if (x > getFloatOption("maxTime")) {
                    cancel();
                }

                if (player.isDead()) {
                    triggerLoc.getWorld().createExplosion(player.getLocation(), power);
                    cancel();
                }

                //TODO: Add sound effects and particles.

            }
        }.runTaskTimer(dvz, 0, 5);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
