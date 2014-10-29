package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Explode extends MobAbility {

    public Explode() {
        super();
        ability = Ability.EXPLODE;
        castItem = new CWItem(Material.SULPHUR, 1, (short) 0, displayName);
    }

    public void castAbility(Player player, Location triggerLoc) {
        
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
