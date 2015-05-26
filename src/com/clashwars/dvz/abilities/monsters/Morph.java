package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Morph extends MobAbility {

    public Morph() {
        super();
        ability = Ability.MORPH;
        castItem = new DvzItem(Material.PRISMARINE_SHARD, 1, (short)0, displayName, 0, 1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        DvzClass dvzClass = dvz.getPM().getPlayer(player).getPlayerClass();
        if (dvzClass == DvzClass.VILLAGER) {
            dvz.getPM().getPlayer(player).setClass(DvzClass.WITCH);
        }
        else if (dvzClass == DvzClass.WITCH) {
            dvz.getPM().getPlayer(player).setClass(DvzClass.VILLAGER);
        }
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
