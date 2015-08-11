package com.clashwars.dvz.abilities.monsters.spider;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class Climb extends BaseAbility {

    public Climb() {
        super();
        ability = Ability.CLIMB;
    }

    @EventHandler
    public void move(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.getPlayerClass() != DvzClass.SPIDER) {
            return;
        }

        Location playerLoc = player.getLocation();
        Block block = player.getLocation().add(0,1,0).getBlock();

        double pitch = Math.PI /2;
        double yaw  = ((playerLoc.getYaw() + 90) * Math.PI) / 180;
        Vector castVec = new Vector(((float)Math.sin(pitch) * Math.cos(yaw)), ((float)Math.cos(pitch)), ((float)Math.sin(pitch) * Math.sin(yaw)));
        castVec.multiply(1.2f);
        castVec.add(playerLoc.toVector());

        Block b = castVec.toLocation(playerLoc.getWorld()).getBlock();

        if (b.getType() != Material.AIR && b.getRelative(BlockFace.UP).getType() != Material.AIR) {
            if (player.isSneaking()) {
                player.setFallDistance(0f);
                player.setVelocity(player.getVelocity().setY(-0.2f));
            } else {
                player.setVelocity(player.getVelocity().setY(0.4f));
            }
        }
    }


}
