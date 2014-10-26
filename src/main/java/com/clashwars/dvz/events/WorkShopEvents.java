package com.clashwars.dvz.events;

import com.clashwars.dvz.DvZ;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class WorkShopEvents implements Listener {

    private DvZ dvz;

    public WorkShopEvents(DvZ dvz) {
        this.dvz = dvz;
    }

    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        if (event.getPlayer() == null) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();


    }

}
