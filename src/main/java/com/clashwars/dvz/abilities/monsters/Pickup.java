package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Pickup extends MobAbility {

    public Pickup() {
        super();
        ability = Ability.PICKUP;
        castItem = new DvzItem(Material.GOLD_HOE, 1, (short)0, displayName, 3, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {

    }

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getRightClicked();

        if(!canCast(event.getPlayer())) {
            return;
        }

        if(dvz.getPM().getPlayer(player).isMonster()) {
            return;
        }

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
