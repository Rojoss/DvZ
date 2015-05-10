package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Rush extends MobAbility {

    public Rush() {
        super();
        ability = Ability.RUSH;
        castItem = new DvzItem(Material.SUGAR, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerloc) {
        if (CWUtil.getTargetedPlayer(player, getIntOption("range")) == null) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }
        /*

        player.setWalkSpeed(getDvzClass().getClassClass().getSpeed() + getFloatOption("bonusspeed"));
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(getDvzClass().getClassClass().getSpeed());
            }
        }.runTaskLater(dvz, getIntOption("duration"));
    */
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
