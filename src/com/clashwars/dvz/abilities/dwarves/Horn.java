package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Horn extends DwarfAbility {

    public Horn() {
        super();
        ability = Ability.HORN;
        castItem = new DvzItem(Material.HOPPER, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        if (dvz.getGM().getDragonSlayer() != player) {
            return;
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                for (CWPlayer cwp : dvz.getPM().getPlayers(ClassType.DWARF, true)) {
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, getIntOption("duration"), 1));
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getIntOption("duration"), 1));
                }
            }
        }.runTaskLater(dvz, 40);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
