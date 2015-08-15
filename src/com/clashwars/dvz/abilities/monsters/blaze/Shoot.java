package com.clashwars.dvz.abilities.monsters.blaze;

import com.clashwars.dvz.abilities.dwarves.bonus.Forcefield;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

public class Shoot extends BaseAbility {

    public Shoot() {
        super();
        ability = Ability.SHOOT;
        castItem = new DvzItem(Material.BLAZE_ROD, 1, (short)0, displayName, 200, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't use this in mid air! &4&l<<"));
            return;
        }

        List<Player> players = CWUtil.getNearbyPlayers(player.getLocation(), 3);
        for (Player p : players) {
            if (dvz.getPM().getPlayer(p).isDwarf() && !Forcefield.inForcefield(p.getLocation())) {
                new AbilityDmg(p, 1, ability, player);
                p.setFireTicks((int)dvz.getGM().getMonsterPower(20, 40));
            }
        }

        player.setVelocity(new Vector(0, dvz.getGM().getMonsterPower(1f) + 1f, 0));
        ParticleEffect.FLAME.display(1.2f, 0.2f, 1.2f, 0, 50, player.getLocation());
        ParticleEffect.LAVA.display(1.5f, 0.2f, 1.5f, 0, 20, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 0.5f, 2);
        player.getWorld().playSound(player.getLocation(), Sound.BLAZE_HIT, 0.3f, 2);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
