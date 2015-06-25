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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        Long t = System.currentTimeMillis();
        double pitch = Math.PI /2;
        double yaw  = ((playerLoc.getYaw() + 90) * Math.PI) / 180;
        Vector castVec = new org.bukkit.util.Vector(((float)Math.sin(pitch) * Math.cos(yaw)), ((float)Math.cos(pitch)), ((float)Math.sin(pitch) * Math.sin(yaw)));
        castVec.multiply(6);
        castVec.add(playerLoc.toVector());
        DvZ.inst().logTimings("Util.getDragonMouthPos()", t);
        return castVec;
    }

    public static boolean isNearShrine(Location loc, float range) {
        Long t = System.currentTimeMillis();
        DvzMap activeMap = DvZ.inst().getMM().getActiveMap();
        if (activeMap != null) {
            String[] shrines = new String[] {"shrinewall", "shrine1keep", "shrine2keep"};
            for (String shrineName : shrines) {
                Cuboid cub = activeMap.getCuboid(shrineName);
                if (cub != null && cub.getCenterLoc() != null) {
                    if (cub.getCenterLoc().distance(loc) < 10) {
                        DvZ.inst().logTimings("Util.isNearShrine()[true]", t);
                        return true;
                    }
                }
            }
        }
        DvZ.inst().logTimings("Util.isNearShrine()[false]", t);
        return false;
    }

    public static String getTimeStamp() {
        return getTimeStamp("[dd-MM HH:mm:ss]");
    }

    public static String getTimeStamp(String syntax) {
        return new SimpleDateFormat(syntax).format(Calendar.getInstance().getTime());
    }

    public static String timeStampToDateString(Timestamp timestamp) {
        return new SimpleDateFormat("dd MMM yyyy").format(timestamp);
    }


    public static void damageEntity(Entity target, double damage) {
        damageEntity(target, damage, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public static void damageEntity(Entity target, double damage, EntityDamageEvent.DamageCause cause) {
        EntityDamageEvent event = new EntityDamageEvent(target, cause, damage);
        target.setLastDamageCause(event);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public static void damageEntity(Entity target, Entity damager, double damage) {
        damageEntity(target, damager, damage, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public static void damageEntity(Entity target, Entity damager, double damage, EntityDamageEvent.DamageCause cause) {
        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(damager, target, cause, damage);
        target.setLastDamageCause(event);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

}
