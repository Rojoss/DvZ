package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FireBreath extends DragonAbility {

    public FireBreath() {
        super();
        ability = Ability.FIRE_BREATH;
        castItem = new DvzItem(Material.CARPET, 1, (short)1, displayName, -1, -1);
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
                if (iterations > dvz.getGM().getDragonPower() * 30 - 20) {
                    cancel();
                    return;
                }
                Location castLoc = Util.getDragonMouthPos(player.getLocation()).toLocation(player.getWorld());
                ParticleEffect.LAVA.display(1f, 0.5f, 1f, 0.01f, 2, castLoc, 500);
                ParticleEffect.FLAME.display(1f, 0.5f, 1f, 0.01f, 2, castLoc, 500);
                FallingBlock fire = player.getLocation().getWorld().spawnFallingBlock(castLoc, Material.FIRE, (byte)0);
                fire.setVelocity(player.getLocation().getDirection().add(new Vector((CWUtil.randomFloat() - 0.5f) * 0.5f, (CWUtil.randomFloat() - 0.5f) * 0.5f, (CWUtil.randomFloat() - 0.5f) * 0.5f)));
            }
        }.runTaskTimer(dvz, 0, 1);

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
