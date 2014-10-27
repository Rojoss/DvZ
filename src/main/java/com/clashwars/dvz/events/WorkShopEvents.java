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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
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

        //Check block placement in other people their workshop.
        //TODO: Check if monsters have been released then allow it.
        if (dvz.getPM().locIsOwnWorkshop(player.getUniqueId(), loc) == 0) {
            player.sendMessage(Util.formatMsg("&cCan't place blocks in other people their workshop!"));
            event.setCancelled(true);
            return;
        }

        if (item.getType() != Material.PISTON_BASE || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().toLowerCase().contains("workshop")) {
            return;
        }

        //TODO: Check y position.

        //TODO: Check if it's placed inside inner walls.

        //TODO: Check if it's placed outside the main building.

        for (WorkShop ws : dvz.getPM().getWorkShops().values()) {
            if (ws.getCenter().distance(loc) <= 8) {
                player.sendMessage(Util.formatMsg("&cCan't create your workshop here."));
                player.sendMessage(Util.formatMsg("&cIt's too close to another workshop."));
                event.setCancelled(true);
                return;
            }
        }

        WorkShop ws = dvz.getPM().getWorkshop(player);
        ws.setCenter(event.getBlockPlaced().getLocation());
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
    private void blockBreak(BlockBreakEvent event) {
        //TODO: Check if monsters have been released then allow it.

        //Check block breaking in other people their workshop.
        if (dvz.getPM().locIsOwnWorkshop(event.getPlayer().getUniqueId(), event.getBlock().getLocation()) == 0) {
            event.getPlayer().sendMessage(Util.formatMsg("&cCan't break blocks in other people their workshop!"));
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();

        //TODO: Check if monsters have been released then allow it.

        //Check killing mobs in other people their workshop
        if (dvz.getPM().locIsOwnWorkshop(damager.getUniqueId(), event.getEntity().getLocation()) == 0) {
            damager.sendMessage(Util.formatMsg("&cCan't attack mobs in other people their workshop!"));
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        //Check opening containers in other people their workshop
        if (block.getState() instanceof InventoryHolder) {
            if (dvz.getPM().locIsOwnWorkshop(player.getUniqueId(), block.getLocation()) == 0) {
                player.sendMessage(Util.formatMsg("&cCan't open containers in other people their workshop!"));
                event.setCancelled(true);
                return;
            }
        }

        if (block.getType() != Material.WORKBENCH) {
            return;
        }
        event.setCancelled(true);
        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws != null && ws.getCraftBlock() != null && ws.getCraftBlock().equals(block.getLocation())) {
            ParticleEffect.WITCH_MAGIC.display(block.getLocation().add(0.5f,0.5f,0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 20);

            //TODO: Crafting implementation.

            return;
        }
        player.sendMessage(Util.formatMsg("&cUse your own crafting table."));
    }

}
