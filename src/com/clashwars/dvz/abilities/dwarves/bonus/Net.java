package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Net extends BaseAbility {

    public Net() {
        super();
        ability = Ability.NET;
        castItem = new DvzItem(Material.WEB, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }

        if (player.getLocation().getPitch() >= 50) {
            return;
        }

        if (onCooldown(player)) {
            dvz.logTimings("Net.castAbility()[cd]", t);
            return;
        }
        player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 0.5f, 2);
        final FallingBlock web = player.getLocation().getWorld().spawnFallingBlock(player.getLocation().add(0, 1.5f, 0), Material.WEB, (byte)0);
        web.setDropItem(false);
        web.setVelocity(player.getLocation().add(0, 1.5f, 0).getDirection().multiply(0.8f));
        web.setMetadata("net",new FixedMetadataValue(dvz,true));

    }

    @EventHandler
    private void fallingBlockLand(final EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getTo() != Material.WEB) {
            return;
        }
        if (!event.getEntity().hasMetadata("net")) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                event.getBlock().setType(Material.AIR);
            }
        }.runTaskLater(dvz, 5);

        final BlockFace[] dirs = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        final List<Player> players = CWUtil.getNearbyPlayers(event.getEntity().getLocation(), 2.5f);
        for (final Player p : players) {
            if (dvz.getPM().getPlayer((Player) p).isDwarf()) {
                continue;
            }

            final Block castBlock = p.getLocation().getBlock();
            for (BlockFace dir : dirs) {
                castBlock.getRelative(dir).setType(Material.WEB);
                castBlock.getRelative(dir).getRelative(BlockFace.UP).setType(Material.WEB);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (BlockFace dir : dirs) {
                        if (castBlock.getRelative(dir).getType() == Material.WEB) {
                            castBlock.getRelative(dir).setType(Material.AIR);
                        }
                        if (castBlock.getRelative(dir).getRelative(BlockFace.UP).getType() == Material.WEB) {
                            castBlock.getRelative(dir).getRelative(BlockFace.UP).setType(Material.AIR);
                        }
                    }
                }
            }.runTaskLater(dvz, 100);
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
