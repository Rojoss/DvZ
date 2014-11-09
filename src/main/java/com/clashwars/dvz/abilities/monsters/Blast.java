package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.effect.EffectType;
import com.clashwars.cwcore.effect.effects.ArcEffect;
import com.clashwars.cwcore.effect.effects.CircleEffect;
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
import org.bukkit.util.Vector;

public class Blast extends MobAbility {

    public Blast() {
        super();
        this.ability = Ability.BLAST;
        castItem = new DvzItem(Material.FLINT_AND_STEEL, 1, (short)0, 197, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        int radius = getIntOption("radius");
        ExpandingCircleEffect ce = new ExpandingCircleEffect(dvz.getEM());
        ce.particle = ParticleEffect.FLAME;
        ce.period = getIntOption("period");
        ce.particleOffset = new Vector(0.1F, 0.2F, 0.1F);
        ce.iterations = getIntOption("rings");
        ce.distanceBetweenRings = getFloatOption("radius") / ce.iterations;
        ce.setLocation(player.getLocation().clone().add(0, 0.5, 0));
        ce.start();

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if(e instanceof Player) {
                final Player p = (Player) e;
                if(dvz.getPM().getPlayer(p).isDwarf()) {
                    ArcEffect ae = new ArcEffect(dvz.getEM());
                    ae.setLocation(player.getLocation());
                    ae.particle = ParticleEffect.FLAME;
                    ae.particles = 50;
                    ae.iterations = 20;
                    ae.setTargetEntity(e);
                    ae.type = EffectType.INSTANT;
                    ae.callback = new Runnable() {

                        @Override
                        public void run() {
                            p.setFireTicks(getIntOption("fire-ticks"));
                        }

                    };
                    ae.start();
                }
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
