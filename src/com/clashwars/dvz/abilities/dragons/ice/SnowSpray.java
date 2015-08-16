package com.clashwars.dvz.abilities.dragons.ice;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SnowSpray extends BaseAbility {

    public SnowSpray() {
        super();
        ability = Ability.SNOW_SPRAY;
        castItem = new DvzItem(Material.SNOW_BALL, 1, (short)0, displayName, -1, -1);
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
                Long t = System.currentTimeMillis();
                iterations++;
                if (iterations > dvz.getGM().getDragonPower() * 50 - 40) {
                    cancel();
                    return;
                }
                Location castLoc = Util.getDragonMouthPos(player.getLocation()).toLocation(player.getWorld());
                ParticleEffect.SNOWBALL.display(1f, 0.5f, 1f, 0.01f, 2, castLoc, 500);
                ParticleEffect.SNOW_SHOVEL.display(1f, 0.5f, 1f, 0.01f, 2, castLoc, 500);
                CWEntity snowball = CWEntity.create(EntityType.SNOWBALL, castLoc);
                snowball.setVelocity(player.getLocation().getDirection().add(new Vector((CWUtil.randomFloat() - 0.5f) * 0.5f, (CWUtil.randomFloat() - 0.5f) * 0.5f, (CWUtil.randomFloat() - 0.5f) * 0.5f)));
                snowball.entity().setMetadata("snowspray", new FixedMetadataValue(dvz, true));
            }
        }.runTaskTimer(dvz, 0, 1);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void snowballHit(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Snowball)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getDamager().hasMetadata("snowspray")) {
            return;
        }
        final Player player = (Player)event.getEntity();
        if (!dvz.getPM().getPlayer(player).isDwarf()) {
            return;
        }
        event.setCancelled(true);
        player.getWorld().playSound(player.getLocation(), Sound.FALL_BIG, 0.5f, 0);
        new AbilityDmg(player, dvz.getGM().getDragonPower(), Ability.SNOW_SPRAY, dvz.getGM().getDragonPlayer());
        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) dvz.getGM().getDragonPower() * 30, 4), true);

        new BukkitRunnable() {
            int ticks = dvz.getGM().getDragonPower() * 30;
            @Override
            public void run() {
                ParticleEffect.SNOW_SHOVEL.display(0.3f, 0.2f, 0.3f, 0, 8, player.getLocation().add(0,0.15f,0), 500);
                ParticleEffect.CLOUD.display(0.2f, 0.1f, 0.2f, 0, 2, player.getLocation(), 500);
                ticks--;
                if (ticks < 1) {
                    cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }

}
