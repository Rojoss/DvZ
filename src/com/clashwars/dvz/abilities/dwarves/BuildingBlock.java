package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BuildingBlock extends DwarfAbility {

    public BuildingBlock() {
        super();
        ability = Ability.BUILDING_BLOCK;
        castItem = new DvzItem(Material.BRICK, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        ItemStack is = getStoneItemStack(player);
        Block b = player.getLastTwoTargetBlocks(null, 100).get(0);

        if (is == null) {
            return;
        }

        if (b == null) {
            return;
        }

        if(is.getAmount() < 4) {
            return;
        }

        if (player.getLastTwoTargetBlocks(null, 100).get(1).getType() != Material.SMOOTH_BRICK) {
            return;
        }

        if (b.getLocation().distance(player.getLocation()) > getIntOption("range") && b.getLocation().distance(player.getLocation()) > 1) {
            return;
        }

        for (int x = 0; x <= 4; x++) {
            Block c = b.getRelative(0, x, 0);

            if (c.getType() != Material.AIR) {
                continue;
            }

            c.setType(is.getType());
            c.setData(is.getData().getData());
            CWUtil.removeItems(player.getInventory(), is, 1);
        }
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
