package com.clashwars.dvz.util;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Util {

    public static String formatMsg(String msg) {
        return CWUtil.integrateColor("&8[&4DvZ&8] &6" + msg);
    }

    public static void disguisePlayer(Player player, String disguiseStr) {
        Bukkit.dispatchCommand(DvZ.inst().getServer().getConsoleSender(), "disguiseplayer " + player.getName() + " " + disguiseStr);
    }

}
