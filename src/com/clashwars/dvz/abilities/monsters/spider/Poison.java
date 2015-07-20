package com.clashwars.dvz.abilities.monsters.spider;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.ExpandingCircleEffect;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class Poison extends BaseAbility {

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

        double range = 4;
        player.getWorld().playSound(player.getLocation(), Sound.SILVERFISH_HIT, 0.8f, 0f);
        player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_REMEDY, 0.2f, 2f);
        ExpandingCircleEffect poisonEffect = new ExpandingCircleEffect(dvz.getEM());
        poisonEffect.particleList.add(new Particle(ParticleEffect.SPELL_MOB, 0.1f, 0.2f, 0.1f, 0, 0, new ParticleEffect.OrdinaryColor(109, 138, 47)));
        poisonEffect.particleList.add(new Particle(ParticleEffect.REDSTONE, 0.1f, 0.2f, 0.1f, 0.1f, 0, new ParticleEffect.OrdinaryColor(117, 166, 13)));
        poisonEffect.period = 1;
        poisonEffect.iterations = (int)range * 2;
        poisonEffect.distanceBetweenRings = 0.5f;
        poisonEffect.setLocation(player.getLocation().add(0, 0.5, 0));
        poisonEffect.start();

        List<Player> players = CWUtil.getNearbyPlayers(player.getLocation(), (float)range * 1.5f);
        for (Player p : players) {
            if(dvz.getPM().getPlayer(p).isDwarf()) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int)dvz.getGM().getMonsterPower(100) + 40, 2));
                ParticleEffect.CRIT.display(1, 0.5f, 1, 0.01f, 10, p.getLocation());
                new AbilityDmg(p, 0, ability, player);
            }
        }
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
