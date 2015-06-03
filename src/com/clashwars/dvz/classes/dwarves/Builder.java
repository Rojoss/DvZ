package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.DvzMap;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class Builder extends DwarfClass {

    public Builder() {
        super();
        dvzClass = DvzClass.BUILDER;
        classItem = new DvzItem(Material.CLAY_BRICK, 1, (byte)0, "&9&lBuilder", 10, -1);

        abilities.add(Ability.BUILDING_BRICK);
        abilities.add(Ability.BUILDING_BLOCK);
        abilities.add(Ability.REINFORCE);
        abilities.add(Ability.SUMMON_STONE);
    }

    @EventHandler
    //Allow block placement within inner walls if it's not inside a workshop and not inside the keep.
    private void blockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType() != Material.SMOOTH_BRICK) {
            return;
        }
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.BUILDER) {
            return;
        }

        if (dvz.getPM().locGetWorkShop(block.getLocation()) != null) {
            return;
        }

        DvzMap activeMap = dvz.getMM().getActiveMap();
        if (activeMap != null && event.getBlock().getLocation().distance(activeMap.getLocation("monster")) < 50f) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cCan't build this close to the monster spawn! &4&l<<"));
                    event.setCancelled(true);
            return;
        }

        if (Util.isNearShrine(block.getLocation(), 10)) {
            return;
        }

        if ((block.getWorld().getHighestBlockAt(block.getLocation()).getLocation().getBlockY() -1) <= block.getLocation().getBlockY() && dvz.getMM().getActiveMap().getCuboid("keep").contains(event.getBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't build in the keep right now! &4&l<<"));
            return;
        }

        event.setCancelled(false);
        dvz.getPM().getPlayer(player).addClassExp(2);
    }

    @EventHandler
    //Allow block breaking within inner walls if it's not inside a workshop and not inside the keep.
    private void blockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!Util.isDestroyable(block.getType())) {
            return;
        }
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.BUILDER) {
            return;
        }

        if (dvz.getPM().locGetWorkShop(block.getLocation()) != null) {
            return;
        }

        DvzMap activeMap = dvz.getMM().getActiveMap();
        if (activeMap != null && event.getBlock().getLocation().distance(activeMap.getLocation("monster")) <  50f) {
            CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cCan't break blocks this close to the monster spawn! &4&l<<"));
            event.setCancelled(true);
            return;
        }

        if (Util.isNearShrine(block.getLocation(), 10)) {
            return;
        }

        if ((block.getWorld().getHighestBlockAt(block.getLocation()).getLocation().getBlockY() -1) <= block.getLocation().getBlockY() && dvz.getMM().getActiveMap().getCuboid("keep").contains(event.getBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't break blocks in the keep right now! &4&l<<"));
            return;
        }

        event.setCancelled(false);
    }

}
