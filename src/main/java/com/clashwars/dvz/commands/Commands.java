package com.clashwars.dvz.commands;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Commands {
    private DvZ dvz;

    public Commands(DvZ dvz) {
        this.dvz = dvz;
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("dvz")) {
            if (args.length >= 1) {
                //##########################################################################################################################
                //############################################# /dvz something {something else} ############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("something")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {something else}"));
                        return true;
                    }

                    player.sendMessage(Util.formatMsg("DvZ!!!!!"));
                    return true;
                }

            }

            sender.sendMessage(CWUtil.integrateColor("&8===== &4&lCommand Help &6/" + label + " &8====="));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " &8- &5Show this page."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " something {something} &8- &5DvZ!."));
            return true;
        }
        return false;
    }
}
