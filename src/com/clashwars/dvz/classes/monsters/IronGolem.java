package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IronGolem extends MobClass {

    public IronGolem() {
        super();
        dvzClass = DvzClass.IRON_GOLEM;
        classItem = new DvzItem(Material.IRON_BLOCK, 1, (short)0, displayName, 100, -1);

        abilities.add(Ability.TOSS);
        abilities.add(Ability.SMASH);
        abilities.add(Ability.GROUND_POUND);
        abilities.add(Ability.FLOWER_TRAIL);
    }

    @Override
    public void onEquipClass(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2));
    }


    @EventHandler
    private void entityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }
        if (dvz.getPM().getPlayer(((Player)event.getEntity())).getPlayerClass() == DvzClass.IRON_GOLEM) {
            event.setCancelled(true);
        }
    }

}
