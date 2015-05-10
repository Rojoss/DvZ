package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.dragons.DragonAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Horn extends DwarfAbility {

    public Horn() {
        super();
        ability = Ability.HORN;
        castItem = new DvzItem(Material.HOPPER, 1, (short)0, displayName, -1, -1);
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
                for (Player pl : Bukkit.getOnlinePlayers() ) {
                    if (pl == player) {
                        continue;
                    }
                    if (dvz.getPM().getPlayer(pl).isDwarf()) {
                        pl.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, getIntOption("duration"), 1));
                        pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, getIntOption("duration"), 1));
                    }
                }
            }
        }.runTaskLater(dvz, 40);

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
