package com.clashwars.dvz.abilities.dwarves;

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
        castItem = new DvzItem(Material.BRICK, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        dvz.getServer().broadcastMessage("0");
        Block b = player.getTargetBlock(null, 80);
        dvz.getServer().broadcastMessage("0.5");
        if(b == null) {
            dvz.getServer().broadcastMessage("1");
            return;
        }
        dvz.getServer().broadcastMessage("1.5");
        if(b.getType() != Material.SMOOTH_BRICK) {
            dvz.getServer().broadcastMessage("2");
            return;
        }
        dvz.getServer().broadcastMessage("2.2");



        Block newBlockReplaced = b.getRelative(b.getFace(player.getEyeLocation().getBlock()));
        dvz.getServer().broadcastMessage("2.4");
        Block newBlock = newBlockReplaced;
        dvz.getServer().broadcastMessage("2.6");
        ItemStack is = getStoneItemStack(player);
        dvz.getServer().broadcastMessage("2.8");

        if(is == null) {
            dvz.getServer().broadcastMessage("3");
            return;
        }
        dvz.getServer().broadcastMessage("4");
        newBlock.setType(is.getType());
        dvz.getServer().broadcastMessage("5");
        newBlock.setData(is.getData().getData());
        dvz.getServer().broadcastMessage("6");
        BlockPlaceEvent bpe = new BlockPlaceEvent(newBlock, newBlockReplaced.getState(), b, is, player, true);
        dvz.getServer().broadcastMessage("7");
    }

    public ItemStack getStoneItemStack(Player player) {
        dvz.getServer().broadcastMessage("100");
        for(ItemStack is : player.getInventory().getContents()) {
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
