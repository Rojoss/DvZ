package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class Web extends MobAbility {

    public Web() {
        super();
        ability = Ability.WEB;
        castItem = new DvzItem(Material.WEB, 1, (short) 0, displayName, 51, -1);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {

        if(player.getLocation().getPitch() >= -10) {
            return;
        }

        final FallingBlock web = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.WEB, (byte) 0);
        web.setDropItem(false);
        web.setVelocity(player.getLocation().getDirection().multiply(getDoubleOption("multiplier")));
        new BukkitRunnable() {
            @Override
            public void run() {
                web.remove();
            }
        }.runTaskLater(dvz, getIntOption("removeAfter"));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        if(!event.getBlock().getType().equals(Material.WEB)) {
            return;
        }

        if(!canCast(event.getPlayer())) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(dvz, getIntOption("removeAfter"));

    }

}