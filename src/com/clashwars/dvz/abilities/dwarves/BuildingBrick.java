package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BuildingBrick extends DwarfAbility {

    public BuildingBrick() {
        super();
        ability = Ability.BUILDING_BRICK;
        castItem = new DvzItem(Material.CLAY_BRICK, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        ItemStack is = getStoneItemStack(player);
        Block b = player.getLastTwoTargetBlocks(null, 100).get(0);

        if(is == null) {
            return;
        }

        if (b == null) {
            return;
        }

        if(player.getLastTwoTargetBlocks(null, 100).get(1).getType() != Material.SMOOTH_BRICK) {
            return;
        }

        if (b.getLocation().distance(player.getLocation()) > getIntOption("range") && b.getLocation().distance(player.getLocation()) > 1) {
            return;
        }

        b.setType(is.getType());
        b.setData(is.getData().getData());
        CWUtil.removeItems(player.getInventory(), is, 1);
    }

    public ItemStack getStoneItemStack(Player player) {
        for(ItemStack is : player.getInventory().getContents()) {
            if(is == null) {
                continue;
            }

            if(is.getType() == Material.SMOOTH_BRICK) {
                return is;
            }
        }
        return null;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
