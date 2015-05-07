package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class TeleportPortal extends MobAbility {

    public TeleportPortal() {
        super();
        ability = Ability.TELEPORT_PORTAL;
        castItem = new DvzItem(Material.EYE_OF_ENDER, 1, (short)0, displayName, 999, 7);
    }


    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (Portal.activePortal == null) {
            player.sendMessage(Util.formatMsg("&cThere is no active portal right now."));
            return;
        }
        //TODO: Maybe a teleport delay so you don't tp instantly (might get abused)
        if (onCooldown(player)) {
            return;
        }
        player.teleport(Portal.activePortal.getLocation());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
