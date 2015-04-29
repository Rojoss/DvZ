package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.LineEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class Burn extends DragonAbility {

    public Burn() {
        super();
        ability = Ability.BURN;
        castItem = new DvzItem(Material.BAKED_POTATO, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        //for (Entity entity : CWUtil.getNearbyEntities(triggerLoc, getFloatOption("distance"), Arrays.asList(new EntityType[]{EntityType.PLAYER}))) {
        for (Entity entity : CWUtil.getNearbyEntities(triggerLoc, getFloatOption("distance"), null)) {
            if (entity == player) {
                continue;
            }
            entity.setFireTicks(40);
            LineEffect effect = new LineEffect(dvz.getEM());
            effect.setLocation(player.getLocation());
            effect.setTargetEntity(entity);
            effect.visibleRange = 300;
            effect.particles = 20;
            effect.particleList.add(new Particle(ParticleEffect.FLAME, 0.2f, 0.2f, 0.2f, 0, 1));
            effect.start();

        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
