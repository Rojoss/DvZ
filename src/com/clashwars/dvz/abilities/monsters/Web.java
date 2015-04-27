package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class Web extends MobAbility {

    public Web() {
        super();
        ability = Ability.WEB;
        castItem = new DvzItem(Material.WEB, 5, (short)0, displayName, 51, -1);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {

        if (player.getLocation().getPitch() >= -5) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        CWUtil.removeItemsFromHand(player, 1);
        final FallingBlock web = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.WEB, (byte)0);
        web.setDropItem(false);
        web.setVelocity(player.getLocation().getDirection().multiply(getDoubleOption("force")));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }


    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.WEB) {
            return;
        }

        if (!canCast(event.getPlayer())) {
            return;
        }

        event.setCancelled(false);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.WEB) {
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, dvz.getCfg().WEB_REMOVAL_TIME);
    }

    @EventHandler
    private void fallingBlockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getTo() != Material.WEB) {
            return;
        }

        final Block block = event.getBlock();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.WEB) {
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, dvz.getCfg().WEB_REMOVAL_TIME);

    }
}