package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
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
        castItem = new CWItem(Material.SPIDER_EYE, 1, (short) 0, displayName);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        int range = getIntOption("range");
        for (Entity ent : player.getNearbyEntities(range, range, range)) {
            if (ent instanceof Player) {
                Player p = (Player) ent;
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("duration"), 1));
            }
        }
        //TODO: Add particle and sound effects.
    }

}
