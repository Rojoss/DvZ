package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.effect.effects.ExpandingCircleEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
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
import org.bukkit.util.Vector;

public class Poison extends MobAbility {

    public Poison() {
        super();
        ability = Ability.POISON;
        castItem = new DvzItem(Material.SPIDER_EYE, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        float range = getFloatOption("range");
        ExpandingCircleEffect happy = new ExpandingCircleEffect(dvz.getEM());
        happy.particle = ParticleEffect.HAPPY_VILLAGER;
        happy.period = getIntOption("period");
        happy.particleOffset = new Vector(0.1F, 0.2F, 0.1F);
        happy.iterations = getIntOption("rings");
        happy.distanceBetweenRings = range / happy.iterations;
        happy.setLocation(player.getLocation().clone().add(0, 0.5, 0));
        ExpandingCircleEffect reddust = happy;
        reddust.particle = ParticleEffect.RED_DUST;
        reddust.start();
        happy.start();

        for (Entity ent : player.getNearbyEntities(range, range, range)) {
            if (!(ent instanceof Player)) {
                return;
            }

            final Player p = (Player) ent;

            if(dvz.getPM().getPlayer(p).isDwarf()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("duration"), 1));
                ParticleEffect.CRIT.display(p.getLocation(), 1);
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
