package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class Blaze extends MobClass {

    public Blaze() {
        super();
        dvzClass = DvzClass.BLAZE;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)61, displayName, 50, -1);

        abilities.add(Ability.SHOOT);
        abilities.add(Ability.GLIDE);
        abilities.add(Ability.FIREBALL);
        abilities.add(Ability.BLAST);

        equipment.add(new DvzItem(Material.GOLD_SWORD, -1, -1));
    }

    @EventHandler
    private void noFallDmg(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) {
            return;
        }

        Player player = (Player)event.getEntity();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.BLAZE) {
            return;
        }

        event.setCancelled(true);
    }

}
