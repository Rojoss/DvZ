package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class Shoot extends MobAbility {

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

        List<Entity> entities = CWUtil.getNearbyEntities(player.getLocation(), 3, null);
        for (Entity e : entities) {
            if (!(e instanceof Player)) {
                continue;
            }
            CWPlayer cwp = dvz.getPM().getPlayer((Player)e);
            if (cwp.isDwarf()) {
                cwp.getPlayer().setFireTicks((int)dvz.getGM().getMonsterPower(20, 40));
            }
        }

        player.setVelocity(new Vector(0, dvz.getGM().getMonsterPower(1) + 0.5f, 0));
        ParticleEffect.FLAME.display(1.2f, 0.2f, 1.2f, 0, 50, player.getLocation());
        ParticleEffect.LAVA.display(1.5f, 0.2f, 1.5f, 0, 20, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.WITHER_SHOOT, 0.5f, 2);
        player.getWorld().playSound(player.getLocation(), Sound.BLAZE_HIT, 0.3f, 2);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
