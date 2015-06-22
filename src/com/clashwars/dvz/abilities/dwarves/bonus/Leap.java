package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Leap extends BaseAbility {

    public Leap() {
        super();
        ability = Ability.LEAP;
        castItem = new DvzItem(Material.RABBIT_FOOT, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("Leap.castAbility()[cd]", t);
            return;
        }
        Vector dir = player.getLocation().getDirection();
        player.setVelocity(player.getVelocity().add(new Vector(dir.getX() * 2.5f, 0.8f, dir.getZ() * 2.5f)));
        player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 0.5f, 1.5f);
        ParticleEffect.CRIT_MAGIC.display(0.8f, 0.2f, 0.8f, 0, 30, player.getLocation());
        dvz.logTimings("Leap.castAbility()", t);
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
