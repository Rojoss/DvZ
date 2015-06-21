package com.clashwars.dvz.util;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.DvzMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4DvZ&8] &6" + msg);
    }

    public static void disguisePlayer(String player, String disguiseStr) {
        disguisePlayer(player, disguiseStr, DvzClass.MONSTER);
    }

    public static void disguisePlayer(String player, String disguiseStr, DvzClass dvzClass) {
        Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "disguiseplayer " + player + " "
                + (disguiseStr == null || disguiseStr.isEmpty() ? dvzClass.toString().toLowerCase() : disguiseStr)
                + " setCustomName &c" + (dvzClass.getType() == ClassType.DRAGON ? dvzClass.getClassClass().getDisplayName() : player)
                + " setCustomNameVisible true");
    }

    public static void undisguisePlayer(String player) {
        Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "undisguiseplayer " + player);
    }

    public static void broadcastAdmins(String msg) {
        Collection<Player> players = (Collection<Player>)Bukkit.getOnlinePlayers();
        for (Player player : players) {
            if (player.isOp() || player.hasPermission("dvz.admin")) {
                player.sendMessage(msg);
            }
        }
    }

    public static boolean isDestroyable(Material mat) {
        return !DvZ.inst().getUndestroyableBlocks().contains(mat);
    }

    public static Vector getDragonMouthPos(Location playerLoc) {
        double pitch = Math.PI /2;
        double yaw  = ((playerLoc.getYaw() + 90) * Math.PI) / 180;
        Vector castVec = new org.bukkit.util.Vector(((float)Math.sin(pitch) * Math.cos(yaw)), ((float)Math.cos(pitch)), ((float)Math.sin(pitch) * Math.sin(yaw)));
        castVec.multiply(6);
        castVec.add(playerLoc.toVector());
        return castVec;
    }

    public static boolean isNearShrine(Location loc, float range) {
        DvzMap activeMap = DvZ.inst().getMM().getActiveMap();
        if (activeMap != null) {
            String[] shrines = new String[] {"shrinewall", "shrine1keep", "shrine2keep"};
            for (String shrineName : shrines) {
                Cuboid cub = activeMap.getCuboid(shrineName);
                if (cub != null && cub.getCenterLoc() != null) {
                    if (cub.getCenterLoc().distance(loc) < 10) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
