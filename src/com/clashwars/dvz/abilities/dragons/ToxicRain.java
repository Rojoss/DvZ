package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.config.aliases.PotionEffects;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.SmokeEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ToxicRain extends DragonAbility {

    public ToxicRain() {
        super();
        ability = Ability.WINDSTORM;
        castItem = new DvzItem(Material.WATER_LILY, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }


        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                iterations++;
                if (iterations > 12) {
                    cancel();
                    return;
                }
                ParticleEffect.DRIP_WATER.display(20, 5, 20, 5, 750, player.getLocation(), 500f);
            }
        }.runTaskTimer(dvz, 10, 10);


        new BukkitRunnable() {
            Location prevLoc;
            int iterations = 0;

            @Override
            public void run() {
                iterations++;
                if (iterations > 3) {
                    cancel();
                    return;
                }
                if (prevLoc != null) {
                    Debug.bc("Poisoning nearby players");
                    for (Entity e : CWUtil.getNearbyEntities(prevLoc, 20, null)) {
                        if (e instanceof Player) {
                            Player pl = (Player) e;
                            Debug.bc(pl.getName());
                            if (dvz.getPM().getPlayer(pl).isDwarf()) {
                                Debug.bc("Dwarf!");
                                pl.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40, 1));
                            }
                        }
                    }
                }
                prevLoc = player.getLocation();
            }
        }.runTaskTimer(dvz, 0, 40);
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
