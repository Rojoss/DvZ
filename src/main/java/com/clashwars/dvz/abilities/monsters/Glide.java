package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Glide extends MobAbility {

    public Glide() {
        super();
        ability = Ability.GLIDE;
        castItem = new DvzItem(Material.BLAZE_POWDER, 1, (short)0, displayName, 199, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        player.setVelocity(player.getLocation().getDirection().multiply(getIntOption("force")).setY(0));
        //TODO: Add particle and sound effects.
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
