package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.effect.EffectType;
import com.clashwars.cwcore.effect.effects.ArcEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
        for (Entity e : player.getNearbyEntities(radius, radius, radius)) {
            if(e instanceof Player) {
                final Player p = (Player) e;
                if(dvz.getPM().getPlayer(p).isDwarf()) {
                    ArcEffect ae = new ArcEffect(dvz.getEM());
                    ae.setLocation(player.getLocation());
                    ae.particle = ParticleEffect.FLAME;
                    ae.setTargetEntity(e);
                    ae.type = EffectType.INSTANT;
                    ae.callback = new Runnable() {

                        @Override
                        public void run() {
                            p.setFireTicks(getIntOption("fire-ticks"));
                        }

                    };
                    ae.run();
                }
            }
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
