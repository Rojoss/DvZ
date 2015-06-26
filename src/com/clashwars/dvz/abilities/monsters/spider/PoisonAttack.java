package com.clashwars.dvz.abilities.monsters.spider;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonAttack extends BaseAbility {

    public PoisonAttack() {
        super();
        ability = Ability.POISON_ATTACK;
    }

    @EventHandler
    public void onPlayerDamagedByPlayer(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player damager = (Player)event.getDamager();
        Player damaged = (Player)event.getEntity();

        if (!canCast(damager)) {
            return;
        }

        if(dvz.getPM().getPlayer(damaged).isMonster()) {
            return;
        }

        if (CWUtil.randomFloat() <= dvz.getGM().getMonsterPower(0.4f) + 0.1f) {
            if (onCooldown(damager)) {
                return;
            }

            event.setCancelled(true);

            new AbilityDmg(damaged, 1, ability, damager);
            damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int)dvz.getGM().getMonsterPower(50) + 20, 1));
            damaged.getWorld().playSound(damaged.getLocation(), Sound.SPIDER_IDLE, 1, 2);
            for (int i = 0; i < 20; i++) {
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(109, 138, 47), damaged.getLocation().add(0.5f - CWUtil.randomFloat(), 0.5f + CWUtil.randomFloat(), 0.5f - CWUtil.randomFloat()), 16);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
