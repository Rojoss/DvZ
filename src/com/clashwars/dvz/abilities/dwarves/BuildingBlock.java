package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BuildingBlock extends DwarfAbility {

    public BuildingBlock() {
        super();
        ability = Ability.BUILDING_BLOCK;
        castItem = new DvzItem(Material.BRICK, 1, (short) 0, displayName, -1, -1);
    }


    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void blockPlace(BlockPlaceEvent event) {
        if (!isCastItem(event.getItemInHand())) {
            return;
        }
        Block block = event.getBlockPlaced();
        Player player = event.getPlayer();

        event.setCancelled(true);
        for (int x = 0; x < getIntOption("blocks"); x++) {
            Block b = block.getRelative(0, x, 0);

            if (b.getType() != Material.AIR) {
                break;
            }

            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item.getType() == Material.SMOOTH_BRICK) {
                    b.setType(item.getType());
                    b.setData(item.getData().getData());
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(item.getType(), item.getData().getData()), 0.5f, 0.5f, 0.5f, 0.1f, 5, b.getLocation().add(0.5f, 0.5f, 0.5f));
                    block.getWorld().playSound(b.getLocation(), Sound.DIG_STONE, 0.2f, 0.5f);

                    if (item.getAmount() == 1) {
                        player.getInventory().setItem(i, new ItemStack(Material.AIR));
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }
    }


    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

}