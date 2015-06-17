package com.clashwars.dvz.abilities.dwarves.dragonslayer;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Horn extends BaseAbility {

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

        for (Player p : dvz.getServer().getOnlinePlayers()) {
            p.playSound(p.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 0);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (CWPlayer cwp : dvz.getPM().getPlayers(ClassType.DWARF, true, false)) {
                    ParticleEffect.VILLAGER_ANGRY.display(0.5f, 1f, 0.5f, 0, 30, cwp.getLocation().add(0,1,0));
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, getIntOption("duration"), 0));
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getIntOption("duration"), 0));
                }
            }
        }.runTaskLater(dvz, 40);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
