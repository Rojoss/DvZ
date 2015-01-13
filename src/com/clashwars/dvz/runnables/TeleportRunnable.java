package com.clashwars.dvz.runnables;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TeleportRunnable extends BukkitRunnable {

    private CWPlayer player;
    private int seconds;
    private Location teleportLoc;
    private String locationMsg;

    private Vector startLocation;


    public TeleportRunnable(CWPlayer player, int seconds, Location teleportLoc, String locationMsg) {
        this.player = player;
        this.seconds = seconds;
        this.teleportLoc = teleportLoc;
        this.locationMsg = locationMsg;

        Location loc = player.getLocation();
        startLocation = new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        this.runTaskTimer(DvZ.inst(), 0, 1);
    }


    @Override
    public void run() {
        Location loc = player.getLocation();
        if (loc.getBlockX() != startLocation.getBlockX() || loc.getBlockZ() != startLocation.getBlockZ()) {
            player.sendMessage(Util.formatMsg("&cTeleportation cancelled because you moved!"));
            player.resetTeleport();
            cancel();
            return;
        }

        seconds--;
        if (seconds <= 0) {
            if (locationMsg != null && !locationMsg.isEmpty()) {
                player.sendMessage(Util.formatMsg("&6Teleported to &5" + locationMsg));
            }
            player.getPlayer().teleport(teleportLoc);
            player.resetTeleport();
            cancel();
        }
    }

}
