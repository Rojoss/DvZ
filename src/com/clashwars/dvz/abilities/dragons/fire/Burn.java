package com.clashwars.dvz.abilities.dragons.fire;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.LineEffect;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;

public class Burn extends BaseAbility {

    public Burn() {
        super();
        ability = Ability.BURN;
        castItem = new DvzItem(Material.BAKED_POTATO, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("Burn.castAbility()[cd]", t);
            return;
        }

        List<Player> players = CWUtil.getNearbyPlayers(triggerLoc, getFloatOption("distance"));
        for (Player p : players) {
            if (p == player) {
                continue;
            }
            if (!dvz.getPM().getPlayer(p).isDwarf()) {
                continue;
            }
            new AbilityDmg(p, 1, ability, player);
            p.setFireTicks(dvz.getGM().getDragonPower() * 60 - 40);
            LineEffect effect = new LineEffect(dvz.getEM());
            effect.setLocation(player.getLocation());
            effect.setTargetEntity(p);
            effect.visibleRange = 300;
            effect.particles = 20;
            effect.particleList.add(new Particle(ParticleEffect.FLAME, 0.2f, 0.2f, 0.2f, 0, 1));
            effect.start();

        }
        dvz.logTimings("Burn.castAbility()", t);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
