package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
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

        if (CWUtil.randomFloat() <= getFloatOption("chance")) {
            if (onCooldown(damager)) {
                return;
            }

            damaged.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, getIntOption("duration"), getIntOption("amplifier")));
            //TODO: Add particle and sound effects.
        }

    }

}
