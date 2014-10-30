package com.clashwars.dvz.util;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4DvZ&8] &6" + msg);
    }

    public static void disguisePlayer(String player, String disguiseStr) {
        Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "disguiseplayer " + player + " "
                + (disguiseStr == null || disguiseStr.isEmpty() ? DvZ.inst().getPM().getPlayer(player).getPlayerClass().toString().toLowerCase() : disguiseStr));
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

}
