package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class ExplosiveEgg extends BaseAbility {

    public ExplosiveEgg() {
        super();
        ability = Ability.EXPLOSIVE_EGG;
        castItem = new DvzItem(Material.EGG, 1, (short) 0, displayName, 200, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.6f, 1.6f);
        player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 0);
        player.throwEgg();
    }

    @EventHandler
    private void eggLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg)) {
            return;
        }
        event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.EXPLODE, 1, 1.5f);
        ParticleEffect.EXPLOSION_LARGE.display(dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), 0, 10, event.getEntity().getLocation(), 500);
        ParticleEffect.SMOKE_LARGE.display(dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), 0, 150, event.getEntity().getLocation(), 500);
        List<Entity> entities = CWUtil.getNearbyEntities(event.getEntity().getLocation(), (int)dvz.getGM().getMonsterPower(1, 2), null);
        for (Entity e : entities) {
            if (e instanceof Player) {
                CWPlayer cwp = dvz.getPM().getPlayer((Player)e);
                if (cwp.isDwarf()) {
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) dvz.getGM().getMonsterPower(20, 80), 1));
                    cwp.getPlayer().damage(dvz.getGM().getMonsterPower(1,5));
                    ParticleEffect.SMOKE_LARGE.display(0.5f, 0.5f, 0.5f, 0, 50, cwp.getLocation());
                }
            }
        }
    }

    @EventHandler
    private void eggHatch(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Chicken)) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.EGG) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
