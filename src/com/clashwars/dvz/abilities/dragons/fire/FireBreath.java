package com.clashwars.dvz.abilities.dragons.fire;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireBreath extends BaseAbility {

    public FireBreath() {
        super();
        ability = Ability.FIRE_BREATH;
        castItem = new DvzItem(Material.CARPET, 1, (short)1, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("FireBreath.castAbility()[cd]", t);
            return;
        }

        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                iterations++;
                if (iterations > dvz.getGM().getDragonPower() * 30 - 20) {
                    cancel();
                    return;
                }
                Location castLoc = Util.getDragonMouthPos(player.getLocation()).toLocation(player.getWorld());
                ParticleEffect.LAVA.display(1f, 0.5f, 1f, 0.01f, 2, castLoc, 500);
                ParticleEffect.FLAME.display(1f, 0.5f, 1f, 0.01f, 2, castLoc, 500);
                FallingBlock fire = player.getLocation().getWorld().spawnFallingBlock(castLoc, Material.FIRE, (byte)0);
                fire.setVelocity(player.getLocation().getDirection().add(new Vector((CWUtil.randomFloat() - 0.5f) * 0.5f, (CWUtil.randomFloat() - 0.5f) * 0.5f, (CWUtil.randomFloat() - 0.5f) * 0.5f)));
                dvz.logTimings("FireBreath.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 0, 1);
        dvz.logTimings("FireBreath.castAbility()", t);

    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
