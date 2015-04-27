package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class Burn extends DragonAbility {

    public Burn() {
        super();
        ability = Ability.BURN;
        castItem = new DvzItem(Material.BAKED_POTATO, 1, 50);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        for (Entity entity : CWUtil.getNearbyEntities(triggerLoc, getFloatOption("distance"), Arrays.asList(new EntityType[]{EntityType.PLAYER}))) {
            ((Player)entity).setFireTicks(40);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
