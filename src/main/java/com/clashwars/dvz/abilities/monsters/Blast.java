package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.effect.EffectType;
import com.clashwars.cwcore.effect.effects.ArcEffect;
import com.clashwars.cwcore.effect.effects.CircleEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Blast extends MobAbility {

    public Blast() {
        super();
        this.ability = Ability.BLAST;
        castItem = new DvzItem(Material.FLINT_AND_STEEL, 1, (short)0, 197, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        int radius = getIntOption("radius");
            CircleEffect ce = new CircleEffect(dvz.getEM());
            ce.setLocation(player.getLocation());
            ce.diameter = 2;
            ce.particle = ParticleEffect.FLAME;
            ce.start();

        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if(e instanceof Player) {
                final Player p = (Player) e;
                if(dvz.getPM().getPlayer(p).isDwarf()) {
                    ArcEffect ae = new ArcEffect(dvz.getEM());
                    ae.setLocation(player.getLocation());
                    ae.particle = ParticleEffect.FLAME;
                    ae.iterations = 200;
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
