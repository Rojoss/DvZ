package com.clashwars.dvz.abilities.dragons.air;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class WindStorm extends BaseAbility {

    public WindStorm() {
        super();
        ability = Ability.WINDSTORM;
        castItem = new DvzItem(Material.STRING, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("WindStorm.castAbility()[cd]", t);
            return;
        }

        //for (Entity entity : CWUtil.getNearbyEntities(triggerLoc, getFloatOption("distance"), Arrays.asList(new EntityType[]{EntityType.PLAYER}))) {
        new BukkitRunnable() {
            Long t = System.currentTimeMillis();
            int iteration = 0;
            int maxIterations = dvz.getGM().getDragonPower() * 4;

            @Override
            public void run() {
                List<CWPlayer> cwPlayers = dvz.getPM().getPlayers(ClassType.DWARF, true, false);
                for (CWPlayer cwp : cwPlayers) {
                    if (cwp.getPlayer() == player) {
                        continue;
                    }

                    Vector v = RandomUtils.getRandomCircleVector();
                    float y;
                    if (iteration > (Math.round(maxIterations / 2) + 2)) {
                        y = CWUtil.randomFloat() - 1;
                    } else {
                        y = CWUtil.randomFloat();
                    }
                    ParticleEffect.CLOUD.display(0.5f, 0.5f, 0.5f, 0f, 10, cwp.getLocation());
                    cwp.getPlayer().setVelocity(new Vector(v.getX() * 0.8f, y, v.getZ() * 0.8f));
                }

                iteration++;
                dvz.logTimings("WindStorm.castAbilityRunnable()", t);
                if (iteration > maxIterations) {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(dvz, 9, 9);
        dvz.logTimings("WindStorm.castAbility()", t);

    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
