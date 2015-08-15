package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Suicide extends BaseAbility {

    public Suicide() {
        super();
        ability = Ability.SUICIDE;
        //displayName = "&4&lSuicide";
        //description = "&4Kill &7yourself when you're stuck somewhere.\n&cYou will get the same classes as before.\n&cSo you &4can't abuse &cit to get new classes!";
        castItem = new DvzItem(Material.GHAST_TEAR, 1, (short)0, displayName, 0, 9);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        new AbilityDmg(player, 500, ability);
        //player.sendMessage(Util.formatMsg("&cSuicided!"));
        dvz.getPM().suicidePlayers.add(player.getUniqueId());
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
