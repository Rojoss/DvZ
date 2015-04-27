package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.effect.Particle;
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

public class Poison extends MobAbility {

    public Poison() {
        super();
        ability = Ability.POISON;
        castItem = new DvzItem(Material.SPIDER_EYE, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        double range = getDoubleOption("range");
        ExpandingCircleEffect poisonEffect = new ExpandingCircleEffect(dvz.getEM());
        poisonEffect.particleList.add(new Particle(ParticleEffect.SPELL_MOB, 0.1f, 0.2f, 0.1f, 0, 0, new ParticleEffect.OrdinaryColor(109, 138, 47)));
        poisonEffect.particleList.add(new Particle(ParticleEffect.REDSTONE, 0.1f, 0.2f, 0.1f, 0.1f, 0, new ParticleEffect.OrdinaryColor(117, 166, 13)));
        poisonEffect.period = 1;
        poisonEffect.iterations = (int)range * 2;
        poisonEffect.distanceBetweenRings = 0.5f;
        poisonEffect.setLocation(player.getLocation().add(0, 0.5, 0));
        poisonEffect.start();

        for (Entity ent : player.getNearbyEntities(range, range, range)) {
            if (!(ent instanceof Player)) {
                return;
            }

            final Player p = (Player) ent;

            if(dvz.getPM().getPlayer(p).isDwarf()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("duration"), getIntOption("amplifier")));
                ParticleEffect.CRIT.display(1, 0.5f, 1, 0.01f, 10, p.getLocation());
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
