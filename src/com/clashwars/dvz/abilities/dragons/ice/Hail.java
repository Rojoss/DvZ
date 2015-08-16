package com.clashwars.dvz.abilities.dragons.ice;

import com.clashwars.cwcore.debug.Debug;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Hail extends BaseAbility {

    public Hail() {
        super();
        ability = Ability.HAIL;
        castItem = new DvzItem(Material.ICE, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        final Location location = player.getLocation();
        final int amount = dvz.getGM().getDragonPower() * 15;

        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                Block block = location.getBlock().getRelative(CWUtil.random(-20,20), CWUtil.random(-3, 8), CWUtil.random(-20,20));
                if (block.getType() != Material.AIR || block.getRelative(BlockFace.UP).getType() != Material.AIR) {
                    return;
                }

                FallingBlock fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation(), Material.ICE, (byte)0);
                fallingBlock.setMetadata("hail", new FixedMetadataValue(dvz, true));
                fallingBlock = block.getWorld().spawnFallingBlock(block.getLocation().add(0,1,0), Material.ICE, (byte)0);
                fallingBlock.setMetadata("hail", new FixedMetadataValue(dvz, false));

                count ++;
                if (count > amount) {
                    cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 2);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler
    private void fallingBlockLand(final EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (!event.getEntity().hasMetadata("hail")) {
            return;
        }

        Block block = event.getBlock();

        block.getWorld().playSound(block.getLocation(), Sound.GLASS, CWUtil.randomFloat(0.2f,0.6f), CWUtil.randomFloat(0,2));
        ParticleEffect.SNOW_SHOVEL.display(0.5f, 0.5f, 0.5f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (event.getBlock().getType() == Material.ICE) {
                    event.getBlock().setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, 3);

        if (!event.getEntity().getMetadata("hail").get(0).asBoolean()) {
            return;
        }

        List<Player> players = CWUtil.getNearbyPlayers(block.getLocation(), dvz.getGM().getDragonPower() + 2);
        for (Player p : players) {
            if(dvz.getPM().getPlayer(p).isDwarf()) {
                new AbilityDmg(p, dvz.getGM().getDragonPower() * 3, ability, dvz.getGM().getDragonPlayer());
            }
        }

        final List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < 10; i++) {
            Item item = block.getWorld().dropItem(block.getLocation(), new ItemStack(Material.ICE));
            item.setVelocity(RandomUtils.getRandomCircleVector().setY(CWUtil.randomFloat(0.2f, 0.6f)).multiply(CWUtil.randomFloat(0.1f, 0.5f)));
            item.setPickupDelay(9999);
            items.add(item);
        }

        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                for (Item item : items) {
                    if (item != null && item.isValid() && item.isOnGround()) {
                        item.remove();
                    }
                }
                count++;
                if (count == 10) {
                    for (Item item : items) {
                        if (item != null) {
                            item.remove();
                        }
                    }
                    cancel();
                }
            }
        }.runTaskTimer(dvz, 10, 10);
    }
}
