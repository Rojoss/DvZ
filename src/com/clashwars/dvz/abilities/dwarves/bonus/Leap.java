package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class Leap extends BaseAbility {

    public Leap() {
        super();
        ability = Ability.LEAP;
        castItem = new DvzItem(Material.RABBIT_FOOT, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("Leap.castAbility()[cd]", t);
            return;
        }

        boolean nearbyMonster = false;
        List<Player> players = CWUtil.getNearbyPlayers(player.getLocation(), 10);
        for (Player p : players) {
            if (dvz.getPM().getPlayer(p).isMonster()) {
                nearbyMonster = true;
                break;
            }
        }

        Vector dir = player.getLocation().getDirection();
        if (nearbyMonster) {
            player.setVelocity(player.getVelocity().add(new Vector(dir.getX() * 1.8f, 0.5f, dir.getZ() * 1.8f)));
            player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 0.25f, 1.8f);
            ParticleEffect.CRIT_MAGIC.display(0.8f, 0.2f, 0.8f, 0, 15, player.getLocation());
        } else {
            player.setVelocity(player.getVelocity().add(new Vector(dir.getX() * 2.5f, 0.8f, dir.getZ() * 2.5f)));
            player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 0.5f, 1.5f);
            ParticleEffect.CRIT_MAGIC.display(0.8f, 0.2f, 0.8f, 0, 30, player.getLocation());
        }
        new AbilityDmg(player, 0, ability);
        dvz.logTimings("Leap.castAbility()", t);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
