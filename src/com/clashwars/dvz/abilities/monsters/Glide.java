package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Glide extends MobAbility {

    public Glide() {
        super();
        ability = Ability.GLIDE;
        castItem = new DvzItem(Material.BLAZE_POWDER, 1, (short)0, displayName, 199, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        Vector d = player.getLocation().getDirection();
        player.setVelocity(d.multiply(getDoubleOption("force")).setY(getDoubleOption("height-force")));
        //TODO: Add particle and sound effects.
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
