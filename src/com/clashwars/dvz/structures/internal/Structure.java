package com.clashwars.dvz.structures.internal;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Structure implements Listener {

    protected DvZ dvz;

    public Structure() {
        this.dvz = DvZ.inst();
    }


    public void onUse(Player player) {
        //Override this in each structure class.
        //It's called when a structure is clicked or when player is sneaking in structure.
        player.sendMessage(CWUtil.formatCWMsg("&cMissing structure implementation."));
    }



    public String getRegion() {
        return "";
    }




    @EventHandler
    private void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        if (!dvz.getPM().getPlayer(player).isDwarf()) {
            return;
        }

        Location loc = event.getClickedBlock().getLocation();
        for (StructureType strucType : StructureType.values()) {
            Structure struc = strucType.getStrucClass();
            for (int i = 0; i < 3; i++) {
                ProtectedRegion region = CWWorldGuard.getRegion(loc.getWorld(), struc.getRegion() + i);
                if (region != null && region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                    event.setCancelled(true);
                    struc.onUse(player);
                    return;
                }
            }

        }
    }


    @EventHandler
    private void sneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) {
            return;
        }

        Player player = event.getPlayer();
        if (!dvz.getPM().getPlayer(player).isDwarf()) {
            return;
        }

        Location loc = player.getLocation();
        for (StructureType strucType : StructureType.values()) {
            Structure struc = strucType.getStrucClass();
            for (int i = 0; i < 5; i++) {
                ProtectedRegion region = CWWorldGuard.getRegion(loc.getWorld(), struc.getRegion() + i);
                if (region != null && region.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                    struc.onUse(player);
                    return;
                }
            }
        }
    }

}
