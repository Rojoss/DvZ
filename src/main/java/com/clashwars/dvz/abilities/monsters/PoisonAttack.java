package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PoisonAttack extends MobAbility {

    public PoisonAttack() {
        super();
        ability = Ability.POISONATTACK;
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
        if (CWUtil.randomFloat() <= getFloatOption("chance")) {
            damaged.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("duration"), 1));
            //TODO: Add particle and sound effects.
        }

    }

}
