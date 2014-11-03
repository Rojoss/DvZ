package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class HealPotion extends DwarfAbility {

    public HealPotion() {
        super();
        ability = Ability.HEAL_POTION;
        castItem = new DvzItem(Material.POTION, 1, (short)8197, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //TODO: Implement
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
