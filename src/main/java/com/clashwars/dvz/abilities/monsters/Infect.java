package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.PlayerManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class Infect extends MobAbility {


    public Infect() {
        super();
        ability = Ability.INFECT;
    }

    @EventHandler
    public void a(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType().equals(EntityType.PLAYER) && event.getEntity().getType().equals(EntityType.PLAYER)) {
            Random r = new Random();
            Player damager = (Player) event.getDamager();
            Player damaged = (Player) event.getEntity();

            if(dvz.getPM().getPlayer(damager).getPlayerClass().getType().equals(ClassType.MONSTER) &&
               dvz.getPM().getPlayer(damaged).getPlayerClass().getType().equals(ClassType.DWARF)) {
                if(r.nextFloat() <= dvz.getCfg().INFECT_CHANCE) {
                    damaged.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, dvz.getCfg().INFECT_DURATION, 1));
                }
            }

        }
    }

}
