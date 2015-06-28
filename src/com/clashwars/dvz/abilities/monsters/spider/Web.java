package com.clashwars.dvz.abilities.monsters.spider;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class Web extends BaseAbility {

    public Web() {
        super();
        ability = Ability.WEB;
        castItem = new DvzItem(Material.WEB, 5, (short)0, displayName, 51, -1);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (player.getLocation().getPitch() >= 50) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.DIG_GRAVEL, 0.5f, 2);
        CWUtil.removeItemsFromHand(player, 1);
        final FallingBlock web = player.getLocation().getWorld().spawnFallingBlock(player.getLocation().add(0, 1.5f, 0), Material.WEB, (byte)0);
        web.setDropItem(false);
        web.setVelocity(player.getLocation().add(0, 1.5f, 0).getDirection().multiply(0.8f));
        web.setMetadata("web", new FixedMetadataValue(dvz, true));
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

        block.getWorld().playSound(block.getLocation(), Sound.DIG_GRAVEL, 0.5f, 2);
        ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.WEB) {
                    ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, (int)dvz.getGM().getMonsterPower(140) + 60);
    }

    @EventHandler
    private void fallingBlockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getTo() != Material.WEB) {
            return;
        }
        if (!event.getEntity().hasMetadata("web")) {
            return;
        }

        final Block block = event.getBlock();
        ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.WEB) {
                    ParticleEffect.FIREWORKS_SPARK.display(0.3f, 0.3f, 0.3f, 0, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, (int)dvz.getGM().getMonsterPower(140) + 60);

    }
}