package com.clashwars.dvz.abilities.monsters.irongolem;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class GroundPound extends BaseAbility {

    public GroundPound() {
        super();
        ability = Ability.GROUND_POUND;
        castItem = new DvzItem(Material.IRON_TRAPDOOR, 1, (short)0, displayName, 100, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1, 2);
        player.setVelocity(player.getLocation().getDirection().multiply(0.8f).setY(1.2f));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                    return;
                }
                player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1, 0);
                player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WOOD, 0.5f, 0);

                float radius = dvz.getGM().getMonsterPower(2, 2);
                ParticleEffect.CLOUD.display(radius,0.3f,radius, 0.1f, 200, player.getLocation());
                List<Player> players = CWUtil.getNearbyPlayers(player.getLocation(), radius + 1);
                for (Player p : players) {
                    if(dvz.getPM().getPlayer(p).isDwarf()) {
                        new AbilityDmg(p, 2, ability, player);
                        Vector dir = p.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                        p.setVelocity(player.getVelocity().add(dir.setY(1.5f)));
                    }
                }

                cancel();
            }
        }.runTaskTimer(dvz, 10, 1);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
