package com.clashwars.dvz.events;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WorkShopEvents implements Listener {

    private DvZ dvz;

    public WorkShopEvents(DvZ dvz) {
        this.dvz = dvz;
    }


    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        if (!dvz.getGM().isStarted()) {
            return;
        }

        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.WORKBENCH || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().toLowerCase().contains("workshop")) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        if (event.getBlockAgainst().getType() != Material.PISTON_BASE) {
            player.sendMessage(Util.formatMsg("&cCan't create your workshop here."));
            player.sendMessage(Util.formatMsg("&cYou have to place it on one of the pistons."));
            return;
        }
        
        event.setCancelled(false);
        WorkShop ws = dvz.getPM().getWorkshop(player);
        DvzClass playerClass = dvz.getPM().getPlayer(player).getPlayerClass();
        ws.setType(playerClass);
        event.getBlockPlaced().setType(Material.AIR);
        if (ws.build(event.getBlockPlaced().getLocation())) {
            player.sendMessage(Util.formatMsg("&6Workshop created!"));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 0.8f);
            ws.onBuild();
        } else {
            event.setCancelled(true);
            player.sendMessage(Util.formatMsg("&4ERROR&8: &cCould not create workshop."));
        }
    }

}
