package com.clashwars.dvz.util;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4DvZ&8] &6" + msg);
    }

    public static void disguisePlayer(String player, String disguiseStr) {
        Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "disguiseplayer " + player + " "
                + (disguiseStr == null || disguiseStr.isEmpty() ? DvZ.inst().getPM().getPlayer(player).getPlayerClass().toString().toLowerCase() : disguiseStr) + " setCustomName &c" + player + " setCustomNameVisible true");
    }

    public static void undisguisePlayer(String player) {
        Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "undisguiseplayer " + player);
    }

    public static void broadcastAdmins(String msg) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("dvz.admin")) {
                player.sendMessage(msg);
            }
        }
    }

    public static boolean isDestroyable(Material mat) {
        return DvZ.inst().getDestroyableBlocks().contains(mat);
    }

    public static Vector getDragonMouthPos(Location playerLoc) {
        double pitch = Math.PI /2;
        double yaw  = ((playerLoc.getYaw() + 90) * Math.PI) / 180;
        Vector castVec = new org.bukkit.util.Vector(((float)Math.sin(pitch) * Math.cos(yaw)), ((float)Math.cos(pitch)), ((float)Math.sin(pitch) * Math.sin(yaw)));
        castVec.multiply(6);
        castVec.add(playerLoc.toVector());
        return castVec;
    }

}
