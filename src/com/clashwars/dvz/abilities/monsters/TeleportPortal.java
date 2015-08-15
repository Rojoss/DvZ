package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.abilities.monsters.enderman.Portal;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class TeleportPortal extends BaseAbility {

    public TeleportPortal() {
        super();
        ability = Ability.TELEPORT_PORTAL;
        castItem = new DvzItem(Material.EYE_OF_ENDER, 1, (short)0, displayName, 999, 8);
    }


    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (Portal.activePortal == null) {
            player.sendMessage(Util.formatMsg("&cThere is no active portal right now."));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        dvz.getPM().getPlayer(player).timedTeleport(Portal.activePortal.getLocation().clone().add(1,1,0), 5, "the portal");
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
