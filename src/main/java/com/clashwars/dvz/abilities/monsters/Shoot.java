package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Shoot extends MobAbility {

    public Shoot() {
        super();
        ability = Ability.SHOOT;
        castItem = new DvzItem(Material.BLAZE_ROD, 1, (short)0, displayName, 200, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        player.setVelocity(new Vector(0, getIntOption("force"), 0));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
