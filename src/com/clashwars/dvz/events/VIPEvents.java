package com.clashwars.dvz.events;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class VIPEvents implements Listener {

    private DvZ dvz;

    public VIPEvents(DvZ dvz) {
        this.dvz = dvz;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        //Clicking on dispenser to open armor coloring.
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.DISPENSER) {
            return;
        }
        if (event.getClickedBlock().getData() != 1) {
            return;
        }
        event.setCancelled(true);
        dvz.getArmorMenu().showMenu(event.getPlayer());
    }


    @EventHandler
    private void onJoin(final PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                equipHat(event.getPlayer());
            }
        }.runTaskLater(dvz, 20);
    }

    @EventHandler
     private void onEnable(PluginEnableEvent event) {
        for (Player player : dvz.getServer().getOnlinePlayers()) {
            equipHat(player);
        }
    }

    private void equipHat(Player player) {
        CWItem hatItem = null;
        if (player.hasPermission("hat.iron"))
            hatItem = new CWItem(Material.ANVIL);
        if (player.hasPermission("hat.gold"))
            hatItem = new CWItem(Material.GOLDEN_APPLE);
        if (player.hasPermission("hat.diamond"))
            hatItem = new CWItem(Material.DIAMOND);
        if (player.hasPermission("hat.helper"))
            hatItem = new CWItem(Material.PRISMARINE_SHARD);
        if (player.hasPermission("hat.mod"))
            hatItem = new CWItem(Material.EMERALD);
        if (player.hasPermission("hat.admin"))
            hatItem = new CWItem(Material.REDSTONE);

        if (hatItem != null) {
            new Hat(player, hatItem);
        }
    }

}
