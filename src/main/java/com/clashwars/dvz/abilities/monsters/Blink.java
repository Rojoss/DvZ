package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Blink extends MobAbility {

    public Blink() {
        super();
        this.ability = Ability.BLINK;
        castItem = new DvzItem(Material.RED_ROSE, 1, (short)0, 196, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Block b = player.getTargetBlock(null, 50);
        Location l = player.getLocation();
        if(b != null) {
            player.teleport(b.getLocation().getBlock().getRelative(BlockFace.UP).getLocation());
            player.getLocation().setPitch(l.getPitch());
            player.getLocation().setYaw(l.getYaw());
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
