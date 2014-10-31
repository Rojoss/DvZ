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
        castItem = new DvzItem(Material.SULPHUR, 1, (short) 0, displayName, 50, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        new BukkitRunnable() {
            @Override
            public void run() {
            if (player.isDead()) {

                cancel();
            }
            }
        }.runTaskTimer(dvz, 0, 250);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
