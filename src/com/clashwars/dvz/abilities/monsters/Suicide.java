package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Suicide extends BaseAbility {

    public Suicide() {
        super();
        ability = Ability.SUICIDE;
        //displayName = "&4&lSuicide";
        //description = "&4Kill &7yourself when you're stuck somewhere.\n&cYou will get the same classes as before.\n&cSo you &4can't abuse &cit to get new classes!";
        castItem = new DvzItem(Material.GHAST_TEAR, 1, (short)0, displayName, 0, 8);
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
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
