package com.clashwars.dvz.abilities.dragons.water;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ToxicRain extends BaseAbility {

    public ToxicRain() {
        super();
        ability = Ability.WINDSTORM;
        castItem = new DvzItem(Material.WATER_LILY, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("ToxicRain.castAbility()[cd]", t);
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
                Long t = System.currentTimeMillis();
                iterations++;
                if (iterations > 3) {
                    cancel();
                    return;
                }
                if (prevLoc != null) {
                    List<Player> players = CWUtil.getNearbyPlayers(prevLoc, 40);
                    for (Player p : players) {
                        if (!dvz.getPM().getPlayer(p).isDwarf()) {
                            continue;
                        }
                        new AbilityDmg(p, 0, ability, player);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, dvz.getGM().getDragonPower() * 20, dvz.getGM().getDragonPower() - 1));
                    }
                }
                prevLoc = player.getLocation();
                dvz.logTimings("ToxicRain.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 0, dvz.getGM().getDragonPower() * 20);
        dvz.logTimings("ToxicRain.castAbility()", t);
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
