package com.clashwars.dvz.abilities.dragons.air;

import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.SphereEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AirShield extends BaseAbility {

    public AirShield() {
        super();
        ability = Ability.AIRSHIELD;
        castItem = new DvzItem(Material.THIN_GLASS, 1, (short)0, displayName, -1, -1);
    }

    private boolean toggled = false;
    private Player dragonPlayer;
    private SphereEffect effect;

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        if (toggled) {
            toggled = false;
            effect.cancel();
            effect = null;
            player.setFlySpeed(dvz.getPM().getPlayer(player).getPlayerClass().getClassClass().getSpeed());
        } else {
            player.setFlySpeed(0);
            player.setVelocity(new Vector(0,0,0));
            toggled = true;
            effect = new SphereEffect(dvz.getEM());
            effect.setEntity(player);
            effect.particleList.add(new Particle(ParticleEffect.FIREWORKS_SPARK, 0, 0, 0, 0, 1));
            effect.radius = 8;
            effect.visibleRange = 500;
            effect.particles = 20;
            effect.infinite();
            effect.start();
        }

        dragonPlayer = player;
    }

    @EventHandler
    private void onDamage(final EntityDamageByEntityEvent event) {
        if (!toggled) {
            return;
        }
        if (event.getEntity() != dragonPlayer) {
            return;
        }

        if (!(event.getDamager() instanceof Projectile)) {
            return;
        }

        Projectile proj = (Projectile)event.getDamager();
        if (!(proj.getShooter() instanceof Player)) {
            return;
        }
        final Player shooter = (Player)proj.getShooter();

        ParticleEffect.CRIT.display(2, 2, 2, 0.1f, 50, event.getDamager().getLocation());
        shooter.playSound(shooter.getLocation(), Sound.ARROW_HIT, 1.0f, 1.0f);
        dragonPlayer.getWorld().playSound(dragonPlayer.getLocation(), Sound.ZOMBIE_METAL, 2, 2);

        event.setCancelled(true);

        new BukkitRunnable() {
            @Override
            public void run() {
                shooter.damage(event.getDamage());
            }
        }.runTaskLater(dvz, 20);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }


}
