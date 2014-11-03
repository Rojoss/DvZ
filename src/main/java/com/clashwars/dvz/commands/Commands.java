package com.clashwars.dvz.commands;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.*;
import com.clashwars.dvz.player.PlayerManager;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class Commands {

    private DvZ dvz;
    private GameManager gm;
    private PlayerManager pm;
    private ClassManager cm;

    public Commands(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
        pm = dvz.getPM();
        cm = dvz.getCM();
    }


    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("dvz")) {
            if (args.length >= 1) {
                //##########################################################################################################################
                //######################################################## /dvz help #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(CWUtil.integrateColor("&8===== &4&lPlayer Commands &8====="));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " &8- &5Show game information."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " help &8- &5Show this page."));
                    if (sender.isOp() || sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(CWUtil.integrateColor("&6/" + label + " admin &8- &5Help for admin commands."));
                    }
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " switch &8- &5Switch to another class."));
                    sender.sendMessage(CWUtil.integrateColor("&7(You will get the same class options again!)"));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " spawn &8- &5Teleport to dwarf/monster spawn."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " class {name} &8- &5Detailed class info."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " abilities &8- &5List all abilities you can use."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " ability {name} &8- &5Detailed ability info."));
                    return true;
                }


                //##########################################################################################################################
                //####################################################### /dvz admin #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("admin")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8===== &4&lAdmin Commands &8====="));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " reset [mapName] &8- &5Reset the game."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " open &8- &5Open the game."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " start &8- &5Start the game."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " stop [reason] &8- &5Stop the game."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " speed [value] &8- &5Set the game speed (def:0)"));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " dragon [type] &8- &5Set yourself to be the dragon."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " loc {name} [block] &8- &5Set a location at ur location."));
                    sender.sendMessage(CWUtil.integrateColor("&7(Or at the block on cursor within 5 blocks if 'block' is specified)"));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " save &8- &5Save everything."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " reload [force] &8- &5Reload configs."));
                    sender.sendMessage(CWUtil.integrateColor("&7(If you use force it wont first save the configs. &4Careful&7!)"));
                    return true;
                }


                //TODO: Spawn cmd

                //TODO: class cmd

                //TODO: Abilities cmd

                //TODO: Ability cmd


                //##########################################################################################################################
                //############################################### /dvz reset [map] [nextgame] ##############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("reset")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    String mapName = "";
                    if (args.length > 1) {
                        if (!dvz.getMM().getMaps().containsKey(args[1])) {
                            sender.sendMessage(Util.formatMsg("&cInvalid map name specified."));
                            sender.sendMessage(Util.formatMsg("&4Maps&8: &c" + CWUtil.implode(dvz.getMM().getMaps().keySet().toArray(new String[dvz.getMM().getMaps().size()]), "&8, &c")));
                            return true;
                        }
                        mapName = args[1];
                    }

                    boolean nextGame = true;
                    if (args.length > 2) {
                        if (args[2].equalsIgnoreCase("false") || args[2].equalsIgnoreCase("no")) {
                            nextGame = false;
                        }
                    }

                    gm.resetGame(nextGame, mapName);
                    sender.sendMessage(Util.formatMsg("&6You have reset the game!"));
                    return true;
                }


                //##########################################################################################################################
                //######################################################## /dvz open #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("open")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (gm.getState() != GameState.SETUP) {
                        sender.sendMessage(Util.formatMsg("&cReset the game first before opening!"));
                        //return true;
                    }

                    gm.openGame();
                    return true;
                }


                //##########################################################################################################################
                //####################################################### /dvz start #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("start")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (gm.getState() != GameState.OPENED) {
                        sender.sendMessage(Util.formatMsg("&cOpen the game first before starting!"));
                        //return true;
                    }

                    gm.startGame();
                    sender.sendMessage(Util.formatMsg("&6You have started the game!"));
                    return true;
                }


                //##########################################################################################################################
                //####################################################### /dvz stop #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("stop")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (!gm.isStarted()) {
                        sender.sendMessage(Util.formatMsg("&cThe game has to be started before it can be stopped!"));
                        //return true;
                    }

                    String reason = "";
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; i++) {
                            reason += args[i] + " ";
                        }
                        reason = reason.trim();
                    }

                    gm.stopGame(true, reason);
                    sender.sendMessage(Util.formatMsg("&6You have stopped the game!"));
                    return true;
                }


                //##########################################################################################################################
                //################################################### /dvz speed [speed] ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("speed")) {
                    if (args.length < 2) {
                        sender.sendMessage(Util.formatMsg("&6Game speed&8: &5" + gm.getSpeed()));
                        sender.sendMessage(Util.formatMsg("&5Specify a value to modify it. &7(default:0)"));
                        return true;
                    }

                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    gm.setSpeed(CWUtil.getInt(args[1]));
                    sender.sendMessage(Util.formatMsg("&6Game speed set to&8: &5" + gm.getSpeed()));
                    return true;
                }


                //##########################################################################################################################
                //################################################### /dvz dragon [type] ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("dragon")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (!player.isOp() && !player.hasPermission("dvz.admin")) {
                        player.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (!gm.isDwarves() && gm.getState() != GameState.NIGHT_TWO) {
                        sender.sendMessage(Util.formatMsg("&cYou can only set the dragon when the game is started."));
                        //return true;
                    }

                    if (args.length > 1) {
                        DvzClass dragonType = DvzClass.fromString(args[1]);
                        if (dragonType == null || dragonType.getType() != ClassType.DRAGON) {
                            player.sendMessage(Util.formatMsg("&cInvalid dragon type specified."));
                            return true;
                        }
                        gm.setDragonType(dragonType);
                    }

                    gm.setDragonPlayer(player.getUniqueId());
                    if (gm.getState() == GameState.NIGHT_TWO) {
                        gm.createDragon();
                    } else {
                        player.sendMessage(Util.formatMsg("&6You will be the " + CWUtil.capitalize(gm.getDragonType().toString().toLowerCase()) + " when the second night falls."));
                    }
                    return true;
                }


                //##########################################################################################################################
                //################################################ /dvz loc {type} [block] #################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("loc")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (!player.isOp() && !player.hasPermission("dvz.admin")) {
                        player.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (args.length < 2) {
                        sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/" + label + " " + args[0] + " {type/name}"));
                        sender.sendMessage(Util.formatMsg("&4Names&8: &c" + CWUtil.implode(dvz.getMM().getLocationNames(), "&8, &c")));
                        return true;
                    }

                    if (dvz.getMM().getActiveMap() == null) {
                        sender.sendMessage(Util.formatMsg("&cThere is no map active right now."));
                        return true;
                    }

                    boolean locMatch = false;
                    for (String locName : dvz.getMM().getLocationNames()) {
                        if (locName.equalsIgnoreCase(args[1])) {
                            locMatch = true;
                            break;
                        }
                    }
                    if (!locMatch) {
                        sender.sendMessage(Util.formatMsg("&cInvalid location name specified!"));
                        sender.sendMessage(Util.formatMsg("&4Names&8: &c" + CWUtil.implode(dvz.getMM().getLocationNames(), "&8, &c")));
                        return true;
                    }

                    Location loc = player.getLocation();
                    if (args.length > 2) {
                        if (args[2].equalsIgnoreCase("block") || args[2].equalsIgnoreCase("target")) {
                            Block block = player.getTargetBlock(null, 5);
                            if (block.getType() != Material.AIR) {
                                loc = block.getLocation();
                            }
                        }
                    }

                    dvz.getMM().getActiveMap().setLocation(args[1], loc);
                    player.sendMessage(Util.formatMsg("&6Set location &8'&5" + args[1] + "&8' &6at: &8(&5" + loc.getBlockX() + "&8, &5" + loc.getBlockY() + "&8, &5" + loc.getBlockZ() + "&8)"));
                    return true;
                }



                //##########################################################################################################################
                //####################################################### /dvz save ########################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("save")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    dvz.getGameCfg().save();
                    dvz.getPlayerCfg().save();
                    dvz.getWSCfg().save();
                    dvz.getMapCfg().save();
                    sender.sendMessage(Util.formatMsg("&6All game data saved."));
                    return true;
                }


                //##########################################################################################################################
                //################################################## /dvz reload [force] ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("reload")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    boolean force = false;
                    if (args.length > 1 && (args[1].equalsIgnoreCase("force") || args[1].equalsIgnoreCase("true"))) {
                        force = true;
                    }

                    if (!force) {
                        dvz.getGameCfg().save();
                        dvz.getPlayerCfg().save();
                        dvz.getWSCfg().save();
                        dvz.getMapCfg().save();
                    }

                    dvz.getGameCfg().load();
                    dvz.getPlayerCfg().load();
                    dvz.getWSCfg().load();
                    dvz.getAbilityCfg().load();
                    dvz.getClassesCfg().load();
                    dvz.getMapCfg().load();

                    sender.sendMessage(Util.formatMsg("&6All configs reloaded" + (force ? " with force. &8(&4Not saved!&8)" : ".")));
                    return true;
                }


                //##########################################################################################################################
                //####################################################### /dvz test ########################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("test")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    for (int i = 0; i < (args.length > 2 && CWUtil.getInt(args[2]) > 0 ? CWUtil.getInt(args[2]) : 100); i++) {
                        Map<DvzClass, BaseClass> classes = dvz.getCM().getRandomClasses(player, args.length > 1 && args[1].equalsIgnoreCase("monster") ? ClassType.MONSTER : ClassType.DWARF);
                        for (DvzClass dvzClass : classes.keySet()) {
                            if (dvz.getPM().fakePlayers.containsKey(dvzClass)) {
                                dvz.getPM().fakePlayers.put(dvzClass, dvz.getPM().fakePlayers.get(dvzClass) + 1);
                            } else {
                                dvz.getPM().fakePlayers.put(dvzClass, 1);
                            }
                        }
                    }
                    player.sendMessage(Util.formatMsg("&4&lTest results&8&l:"));
                    for (DvzClass dvzClass : dvz.getPM().fakePlayers.keySet()) {
                        player.sendMessage(CWUtil.integrateColor("&6" + dvzClass.toString() + ": &5" + dvz.getPM().fakePlayers.get(dvzClass)));
                    }
                    dvz.getPM().fakePlayers.clear();
                    return true;
                }
            }

            sender.sendMessage(CWUtil.integrateColor("&8========== &4&lDvZ Game Information &8=========="));
            sender.sendMessage(CWUtil.integrateColor("&6Not yet available."));
            sender.sendMessage(CWUtil.integrateColor("&8======= &4Use &c/dvz help &4for all commands &8======="));
            return true;
        }
        return false;
    }
}
