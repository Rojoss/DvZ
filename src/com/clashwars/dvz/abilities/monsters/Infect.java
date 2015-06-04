package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Infect extends MobAbility {


    public Infect() {
        super();
        ability = Ability.INFECT;
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

        if (CWUtil.randomFloat() <=  dvz.getGM().getMonsterPower(0.1f, 0.2f)) {
            if (onCooldown(damager)) {
                return;
            }

            damaged.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, (int)dvz.getGM().getMonsterPower(20, 40), 15));

            damager.getWorld().playSound(damager.getLocation(), Sound.EAT, 0.6f, 0);
            for (int i = 0; i < 30; i++) {
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(CWUtil.random(100,130), CWUtil.random(80,110), CWUtil.random(0,30)), damaged.getLocation().add(1 - CWUtil.randomFloat() * 2, 1 + CWUtil.randomFloat() * 2, 1 - CWUtil.randomFloat() * 2), 64);
            }
        }

    }

}
