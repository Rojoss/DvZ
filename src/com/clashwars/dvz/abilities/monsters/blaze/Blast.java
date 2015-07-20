package com.clashwars.dvz.abilities.monsters.blaze;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.ExpandingCircleEffect;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Blast extends BaseAbility {

    public Blast() {
        super();
        this.ability = Ability.BLAST;
        castItem = new DvzItem(Material.FLINT_AND_STEEL, 1, (short)0, 197, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        final int radius = (int)dvz.getGM().getMonsterPower(5, 7);
        ExpandingCircleEffect ce = new ExpandingCircleEffect(dvz.getEM());
        ce.particleList.add(new Particle(ParticleEffect.FLAME, 0.1f, 0.4f, 0.1f, 0.001f, 0));
        ce.period = 1;
        ce.iterations = radius * 2;
        ce.distanceBetweenRings = 0.5f;
        ce.setLocation(player.getLocation().clone().add(0, 0.5, 0));
        ce.start();

        player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 0);
        player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = CWUtil.getNearbyPlayers(player.getLocation(), radius);
                for (Player p : players) {
                    if (dvz.getPM().getPlayer((Player)p).isDwarf()) {
                        new AbilityDmg(p, 1, ability, player);
                        p.setFireTicks((int) dvz.getGM().getMonsterPower(80) + 40);
                        player.getWorld().playSound(p.getLocation(), Sound.BLAZE_HIT, 0.5f, 0);
                        ParticleEffect.FLAME.display(0.5f, 0.2f, 0.5f, 0, 10, player.getLocation());
                    }
                }
            }
        }.runTaskLater(dvz, 10);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
