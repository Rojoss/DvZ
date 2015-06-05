package com.clashwars.dvz.events;

import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.DvZ;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class VIPEvents implements Listener {

    private DvZ dvz;

    public VIPEvents(DvZ dvz) {
        this.dvz = dvz;
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        equipHat(event.getPlayer());
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
