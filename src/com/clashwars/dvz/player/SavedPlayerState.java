package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SavedPlayerState {

    private DvzClass dvzClass;
    private ItemStack[] invItems;
    private ItemStack[] invArmor;
    private Location loc;

    public SavedPlayerState(DvzClass dvzClass, ItemStack[] invItems, ItemStack[] invArmor, Location loc) {
        this.dvzClass = dvzClass;
        this.invItems = invItems;
        this.invArmor = invArmor;
        this.loc = loc;
    }

    public void load(Player player) {
        CWPlayer cwp = DvZ.inst().getPM().getPlayer(player);
        cwp.setClass(dvzClass, true);
        player.getInventory().setContents(invItems);
        player.getInventory().setArmorContents(invArmor);
        player.updateInventory();
        player.teleport(loc);
    }
}
