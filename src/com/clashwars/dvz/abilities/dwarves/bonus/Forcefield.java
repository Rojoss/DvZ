package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.debug.Debug;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.SphereEffect;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Forcefield extends BaseAbility {

    public static List<Vector> forcefields = new ArrayList<Vector>();

    public Forcefield() {
        super();
        ability = Ability.FORCEFIELD;
        castItem = new DvzItem(Material.NETHER_STAR, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }
        if (onCooldown(player)) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.PORTAL_TRIGGER, 0.3f, 0);

        final Vector location = player.getLocation().getBlock().getLocation().toVector();
        forcefields.add(location);

        final SphereEffect effect = new SphereEffect(dvz.getEM());
        effect.setLocation(player.getLocation().getBlock().getLocation());
        effect.particleList.add(new Particle(ParticleEffect.FIREWORKS_SPARK, 0, 0, 0, 0, 1));
        effect.radius = 5;
        effect.visibleRange = 500;
        effect.particles = 20;
        effect.iterations = 160;
        effect.start();

        new BukkitRunnable() {
            @Override
            public void run() {
                effect.particleList.clear();
                effect.particleList.add(new Particle(ParticleEffect.PORTAL, 0, 0, 0, 0, 1));
                effect.particles = 40;

                player.getWorld().playSound(effect.getLocation(), Sound.ENDERMAN_TELEPORT, 0.2f, 0);

                forcefields.remove(location);
                cancel();
            }
        }.runTaskLater(dvz, 150);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler
    private void damage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Projectile)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        CWPlayer cwd = dvz.getPM().getPlayer((Player)event.getEntity());
        if (!cwd.isDwarf()) {
            return;
        }
        if (inForcefield(cwd.getLocation())) {
            event.setCancelled(true);
        }
    }

    public static boolean inForcefield(Location location) {
        for (Vector v : forcefields) {
            if (location.toVector().distance(v) <= 5) {
                return true;
            }
        }
        return false;
    }

}
