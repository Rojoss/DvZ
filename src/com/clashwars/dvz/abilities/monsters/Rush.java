package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
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

        final Player target = CWUtil.getTargetedPlayer(player, getIntOption("range"));

        if (target == null) {
            return;
        }

        if (dvz.getPM().getPlayer(target).getPlayerClass().getType() != ClassType.DWARF) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        final DvzClass dvzClass = dvz.getPM().getPlayer(player).getPlayerClass();

        new BukkitRunnable() {
            int iterations = 0;
            double initDistance = player.getLocation().distance(target.getLocation());

            @Override
            public void run() {
                iterations++;
                if (iterations >= 3) {
                    iterations = 0;
                    if (target != CWUtil.getTargetedPlayer(player, getIntOption("range"))) {
                        player.setWalkSpeed(dvzClass.getClassClass().getSpeed());
                        cancel();
                        return;
                    }
                }

                double distance = player.getLocation().distance(target.getLocation());

                if (distance > initDistance / 2 && player.getWalkSpeed() < dvzClass.getClassClass().getSpeed() + 2f) {
                    player.setWalkSpeed(player.getWalkSpeed() + 0.1f);
                } else if (distance <= initDistance / 2 && player.getWalkSpeed() > dvzClass.getClassClass().getSpeed()) {
                    player.setWalkSpeed(player.getWalkSpeed() - 0.1f);
                }
                if (distance < 1) {
                    player.setWalkSpeed(dvzClass.getClassClass().getSpeed());
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(dvz, 0, 3);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
