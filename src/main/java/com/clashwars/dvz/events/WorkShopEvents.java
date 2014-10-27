package com.clashwars.dvz.events;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
        Location loc = event.getBlockPlaced().getLocation();

        if (item.getType() != Material.PISTON_BASE || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().toLowerCase().contains("workshop")) {
            return;
        }

        //TODO: Check y position.

        //TODO: Check if it's placed inside inner walls.

        //TODO: Check if it's placed outside the main building.

        for (WorkShop ws : dvz.getPM().getWorkShops().values()) {
            if (ws.getLocation().distance(loc) <= 8) {
                player.sendMessage(Util.formatMsg("&cCan't create your workshop here."));
                player.sendMessage(Util.formatMsg("&cIt's too close to another workshop."));
                event.setCancelled(true);
                return;
            }
        }

        WorkShop ws = dvz.getPM().getWorkshop(player);
        ws.setLocation(event.getBlockPlaced().getLocation());
        ws.setType(dvz.getPM().getPlayer(player).getPlayerClass());
        if (ws.build()) {
            event.getBlockPlaced().setType(Material.AIR);
            player.sendMessage(Util.formatMsg("&6Workshop created!"));
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 0.8f);
            ws.save();
        } else {
            event.setCancelled(true);
            player.sendMessage(Util.formatMsg("&4ERROR&8: &cCould not create workshop."));
        }
    }

    @EventHandler
    public void craft(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.WORKBENCH) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws != null && ws.getCraftBlock() != null && ws.getCraftBlock().equals(block.getLocation())) {
            ParticleEffect.WITCH_MAGIC.display(block.getLocation().add(0.5f,0.5f,0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 20);

            //TODO: Crafting implementation.

            return;
        }
        player.sendMessage(Util.formatMsg("&cUse your own crafting table."));
    }

}
