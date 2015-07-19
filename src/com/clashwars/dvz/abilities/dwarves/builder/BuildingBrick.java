package com.clashwars.dvz.abilities.dwarves.builder;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class BuildingBrick extends BaseAbility {

    public BuildingBrick() {
        super();
        ability = Ability.BUILDING_BRICK;
        castItem = new DvzItem(Material.CLAY_BRICK, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>)null, getIntOption("range"));
        Block block = blocks.get(0);

        if (block == null || (block.getType() != Material.AIR && block.getType() != Material.LONG_GRASS)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can't build there! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[not air]", t);
            return;
        }

        if (blocks.get(1).getType() != Material.SMOOTH_BRICK) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan only build against stone bricks! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[not against stone]", t);
            return;
        }

        if (dvz.getMM().getActiveMap().getCuboid("keep").contains(blocks.get(0))) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild tools have to be used outside the keep! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[inside keep]", t);
            return;
        }
        if (dvz.getMM().getActiveMap().getCuboid("innerwall").contains(blocks.get(0))) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild tools have to be used outside the keep! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[inside walls]", t);
            return;
        }

        if (Util.isNearShrine(blocks.get(0).getLocation(), 10)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't build this close to the shrine! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[near shrine]", t);
            return;
        }
        if (dvz.getMM().getActiveMap().getLocation("monster").toVector().distance(blocks.get(0).getLocation().toVector()) < 50f) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't build this close to the monster spawn! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[near monster spawn]", t);
            return;
        }

        if (CWUtil.getNearbyPlayers(block.getLocation(), 1.5f).size() > 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't build where people stand! &4&l<<"));
            dvz.logTimings("BuildingBrick.castAbility()[person in way]", t);
            return;
        }

        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.SMOOTH_BRICK) {
                if (onCooldown(player)) {
                    dvz.logTimings("BuildingBrick.castAbility()[cd]", t);
                    return;
                }
                block.setType(item.getType());
                block.setData(item.getData().getData());

                CWUtil.removeItemsFromSlot(player.getInventory(), i, 1);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(item.getType(), item.getData().getData()), 0.5f, 0.5f, 0.5f, 0.1f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                block.getWorld().playSound(block.getLocation(), Sound.DIG_STONE, 0.8f, 1f);
                player.updateInventory();

                dvz.getPM().getPlayer(player).addClassExp(3);
                dvz.getSM().changeLocalStatVal(player, StatType.BUILDER_BRICK_USED, 1);
                dvz.logTimings("BuildingBrick.castAbility()[place]", t);
                return;
            }
        }
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cNo more stone in your hotbar! &4&l<<"));
        dvz.logTimings("BuildingBrick.castAbility()[no more stone]", t);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
