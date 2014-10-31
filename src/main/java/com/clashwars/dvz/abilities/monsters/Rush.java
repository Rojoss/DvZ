package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Rush extends MobAbility {

    public Rush() {
        super();
        ability = Ability.RUSH;
        castItem = new DvzItem(Material.SUGAR, 1, (short) 0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerloc) {
        if (CWUtil.getTargetedPlayer(player, getIntOption("range")) == null) {
            return;
        }

        Vector dir = player.getLocation().getDirection();
        player.setVelocity(new Vector(dir.getX() * getDoubleOption("multiplier"), 0.2, dir.getZ() * getDoubleOption("multiplier")));
        //TODO: Add particle and sound effects

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
