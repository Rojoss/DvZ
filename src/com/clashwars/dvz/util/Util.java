package com.clashwars.dvz.util;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.DvzMap;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.cwstats.stats.internal.StatType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        if (dvzClass == DvzClass.SILVERFISH) {
            Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "disguiseplayer " + player + " "
                    + (disguiseStr == null || disguiseStr.isEmpty() ? dvzClass.toString().toLowerCase() : disguiseStr));
        } else {
            Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "disguiseplayer " + player + " "
                    + (disguiseStr == null || disguiseStr.isEmpty() ? dvzClass.toString().toLowerCase() : disguiseStr)
                    + " setCustomName &c" + (dvzClass.getType() == ClassType.DRAGON ? dvzClass.getClassClass().getDisplayName() : player)
                    + " setCustomNameVisible true");
        }
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

    public static void broadcast(String message) {
        if (!isTest()) {
            Bukkit.broadcastMessage(CWUtil.integrateColor(message));
        } else {
            Collection<Player> players = (Collection<Player>)Bukkit.getOnlinePlayers();
            for (Player player : players) {
                if (player.hasPermission("dvz.test")) {
                    player.sendMessage(CWUtil.integrateColor(message));
                }
            }
        }
    }

    public static void broadcastTitle(Title title) {
        title.setTimingsToTicks();
        if (!isTest()) {
            title.broadcast();
        } else {
            Collection<Player> players = (Collection<Player>)Bukkit.getOnlinePlayers();
            for (Player player : players) {
                if (player.hasPermission("dvz.test")) {
                    title.send(player);
                }
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

    public static boolean isTest() {
        return DvZ.inst().getGameCfg().TEST_MODE;
    }

    public static boolean canTest(Player player) {
        return (player.hasPermission("dvz.test") && player.getGameMode() != GameMode.CREATIVE) || DvZ.inst().getPM().getPlayer(player).isTesting();
    }


    public static boolean damageShrine(Location shrineLoc, Player player, int amount) {
        ShrineBlock shrineBlock = DvZ.inst().getGM().getShrineBlock(shrineLoc);
        if (shrineBlock == null || shrineBlock.isDestroyed()) {
            return false;
        }

        if (!DvZ.inst().getGM().isMonsters()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe monsters haven't been released yet! &4&l<<"));
            return false;
        }

        if (DvZ.inst().getGM().getState() == GameState.MONSTERS) {
            if (shrineBlock.getType() == ShrineType.KEEP_1 || shrineBlock.getType() == ShrineType.KEEP_2) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou have to destroy the shrine at the wall first! &4&l<<"));
                return false;
            }
        } else if (DvZ.inst().getGM().getState() == GameState.MONSTERS_WALL) {
            if (shrineBlock.getType() == ShrineType.KEEP_2) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou have to destroy the shrine at the bottom of the keep first! &4&l<<"));
                return false;
            }
        }

        shrineBlock.damage(amount);
        DvZ.inst().getSM().changeLocalStatVal(player, StatType.MONSTER_SHRINE_DAMAGE, amount);
        return true;
    }
}
