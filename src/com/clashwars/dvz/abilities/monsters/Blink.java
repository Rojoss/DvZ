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

import java.util.Set;

public class Blink extends MobAbility {

    public Blink() {
        super();
        this.ability = Ability.BLINK;
        castItem = new DvzItem(Material.RED_ROSE, 1, (short)0, 196, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Block b = player.getTargetBlock((Set<Material>)null, getIntOption("range"));
        Location l = player.getLocation().clone();
        Location l2 = b.getRelative(BlockFace.UP).getLocation().clone();
        l2.setPitch(l.getPitch());
        l2.setYaw(l.getYaw());

        if(b != null && b.getType() != Material.AIR) {
            if (onCooldown(player)) {
                return;
            }
            player.teleport(l2);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
