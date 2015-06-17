package com.clashwars.dvz.abilities.dwarves.builder;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BuildingBlock extends BaseAbility {

    public BuildingBlock() {
        super();
        ability = Ability.BUILDING_BLOCK;
        castItem = new DvzItem(Material.BRICK, 2, (short) 0, displayName, -1, -1);
    }


    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        if (!isCastItem(event.getItemInHand())) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!canCast(player)) {
            return;
        }

        if (dvz.getMM().getActiveMap().getCuboid("keep").contains(event.getBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild tools have to be used outside the keep! &4&l<<"));
            return;
        }
        if (dvz.getMM().getActiveMap().getCuboid("innerwall").contains(event.getBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild tools have to be used outside the keep! &4&l<<"));
            return;
        }
        if (Util.isNearShrine(event.getBlock().getLocation(), 10)) {
            return;
        }
        if (dvz.getMM().getActiveMap().getLocation("monster").distance(event.getBlock().getLocation()) < 50f) {
            return;
        }

        Block block = event.getBlock();
        boolean placed = false;
        for (int x = 0; x < getIntOption("blocks"); x++) {
            Block b = block.getRelative(0, x, 0);

            if (b.getType() != Material.AIR && b.getType() != Material.BRICK) {
                break;
            }

            for (int i = 0; i < 9; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() == Material.SMOOTH_BRICK) {

                    if (!onCooldown(player)) {
                        if (x == 0) {
                            event.getBlockReplacedState().setType(item.getType());
                            event.getBlockReplacedState().setRawData(item.getData().getData());
                        } else {
                            b.setType(item.getType());
                            b.setData(item.getData().getData());
                        }
                        placed = true;

                        CWUtil.removeItemsFromSlot(player.getInventory(), i, 1);
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(item.getType(), item.getData().getData()), 0.5f, 0.5f, 0.5f, 0.1f, 5, b.getLocation().add(0.5f, 0.5f, 0.5f));
                        block.getWorld().playSound(b.getLocation(), Sound.DIG_STONE, 0.8f, 1f);
                        player.updateInventory();
                    }
                    break;
                }
            }
            if (!placed) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cNo more stone in your hotbar! &4&l<<"));
            }
        }
        if (placed) {
            dvz.getPM().getPlayer(player).addClassExp(1);
        }
    }


    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

}