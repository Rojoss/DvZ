package com.clashwars.dvz.abilities.dwarves.builder;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
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
        Long t = System.currentTimeMillis();
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
            dvz.logTimings("BuildingBlock.castAbility()[inside keep]", t);
            return;
        }
        if (dvz.getMM().getActiveMap().getCuboid("innerwall").contains(event.getBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild tools have to be used outside the keep! &4&l<<"));
            dvz.logTimings("BuildingBlock.castAbility()[inside walls]", t);
            return;
        }
        if (Util.isNearShrine(event.getBlock().getLocation(), 10)) {
            dvz.logTimings("BuildingBlock.castAbility()[near shrine]", t);
            return;
        }
        if (dvz.getMM().getActiveMap().getLocation("monster").toVector().distance(event.getBlock().getLocation().toVector()) < 100f) {
            dvz.logTimings("BuildingBlock.castAbility()[near monster spawn]", t);
            return;
        }

        Block block = event.getBlock();
        boolean placed = false;
        int blockCount = getIntOption("blocks");
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y < blockCount; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block b = block.getRelative(x, y, z);

                    if (b.getType() != Material.AIR && b.getType() != Material.BRICK) {
                        continue;
                    }

                    for (int i = 0; i < 9; i++) {
                        ItemStack item = player.getInventory().getItem(i);
                        if (item != null && item.getType() == Material.SMOOTH_BRICK) {

                            if (y == 0 && x == 0 && z == 0) {
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
                            break;
                        }
                    }
                    if (!placed) {
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cNo more stone in your hotbar! &4&l<<"));
                        break;
                    }
                }
            }
        }
        if (placed) {
            dvz.getSM().changeLocalStatVal(player, StatType.BUILDER_BLOCK_USED, 1);
            dvz.getPM().getPlayer(player).addClassExp(4);
        }
        dvz.logTimings("BuildingBlock.castAbility()", t);
    }


    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

}