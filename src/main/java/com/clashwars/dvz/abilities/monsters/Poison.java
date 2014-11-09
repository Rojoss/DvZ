package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poison extends MobAbility {

    public Poison() {
        super();
        ability = Ability.POISON;
        castItem = new DvzItem(Material.SPIDER_EYE, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        int range = getIntOption("range");
        for (Entity ent : player.getNearbyEntities(range, range, range)) {
            if (!(ent instanceof Player)) {
                return;
            }

            Player p = (Player) ent;

            if(dvz.getPM().getPlayer(p).isMonster()) {
                return;
            }

            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("duration"), 1));
        }
        //TODO: Add particle and sound effects.
    }

}
