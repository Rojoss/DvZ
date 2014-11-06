package com.clashwars.dvz.events;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
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
import org.bukkit.util.Vector;

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
        if (!dvz.getGM().isStarted()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Location loc = event.getBlockPlaced().getLocation();

        //Check block placement in other people their workshop.
        if (!dvz.getGM().isDwarves()) {
            if (dvz.getPM().locIsOwnWorkshop(player.getUniqueId(), loc) == 0) {
                player.sendMessage(Util.formatMsg("&cCan't place blocks in other people their workshop!"));
                event.setCancelled(true);
                return;
            }
        }

        if (item.getType() != Material.WORKBENCH || !item.hasItemMeta() || !item.getItemMeta().getDisplayName().toLowerCase().contains("workshop")) {
            return;
        }

        if (!dvz.getMM().getActiveMap().isLocWithin(loc, "innerwall", new Vector(-5, 2, -5))) {
            player.sendMessage(Util.formatMsg("&cCan't create your workshop here."));
            player.sendMessage(Util.formatMsg("&cIt has to be created within the inner walls."));
            event.setCancelled(true);
            return;
        }

        if (dvz.getMM().getActiveMap().isLocWithin(loc, "keep", new Vector(5, 50, 5))) {
            player.sendMessage(Util.formatMsg("&cCan't create your workshop here."));
            player.sendMessage(Util.formatMsg("&cIt has to be created outside the keep."));
            event.setCancelled(true);
            return;
        }

        if (event.getBlockAgainst().getType() != Material.PISTON_BASE) {
            player.sendMessage(Util.formatMsg("&cCan't create your workshop here."));
            player.sendMessage(Util.formatMsg("&cYou have to place it on one of the pistons."));
            event.setCancelled(true);
            return;
        }

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

    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        if (!dvz.getGM().isStarted() || !dvz.getGM().isDwarves()) {
            return;
        }

        //Check block breaking in other people their workshop.
        if (dvz.getPM().locIsOwnWorkshop(event.getPlayer().getUniqueId(), event.getBlock().getLocation()) == 0) {
            event.getPlayer().sendMessage(Util.formatMsg("&cCan't break blocks in other people their workshop!"));
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!dvz.getGM().isStarted() || !dvz.getGM().isDwarves()) {
            return;
        }
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();

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

        if (!dvz.getGM().isStarted() || !dvz.getGM().isDwarves()) {
            return;
        }

        //Check opening containers in other people their workshop
        if (block.getState() instanceof InventoryHolder) {
            if (dvz.getPM().locIsOwnWorkshop(player.getUniqueId(), block.getLocation()) == 0) {
                player.sendMessage(Util.formatMsg("&cCan't open containers in other people their workshop!"));
                event.setCancelled(true);
                return;
            }
        }
    }

}
