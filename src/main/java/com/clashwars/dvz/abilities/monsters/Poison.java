package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
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
        dvz.getServer().broadcastMessage("4");
        for (Entity ent : player.getNearbyEntities(range, range, range)) {
            dvz.getServer().broadcastMessage("9");
            if (!(ent instanceof Player)) {
                return;
            }

            Player p = (Player) ent;

            /*if(dvz.getPM().getPlayer(p).isMonster()) {
                return;
            }*/
            dvz.getServer().broadcastMessage("10");
            p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("duration"), 1));
            dvz.getServer().broadcastMessage("11");
        }
        //TODO: Add particle and sound effects.
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
