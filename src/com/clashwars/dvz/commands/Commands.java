package com.clashwars.dvz.commands;

import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.cuboid.SelectionStatus;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassManager;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.player.PlayerManager;
import com.clashwars.dvz.structures.internal.StructureType;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

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
        Long t = System.currentTimeMillis();


        if (label.equalsIgnoreCase("settings") || label.equalsIgnoreCase("prefs")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }

            dvz.getSettingsMenu().showMenu((Player) sender);
            return true;
        }


        if (label.equalsIgnoreCase("storage") || label.equalsIgnoreCase("chests") || label.equalsIgnoreCase("chest")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }
            Player player = (Player)sender;

            if (!dvz.getRewardsCfg().hasStorageCmd(player.getUniqueId()) && !dvz.getRewardsCfg().hasStructureCmds(player.getUniqueId())) {
                if (!player.isOp() && !player.hasPermission("cmd.storage")) {
                    sender.sendMessage(Util.formatMsg("&3Purchase &a&lVIP &3to be able to use the virtual storage."));
                    return true;
                }
            }

            if (!dvz.getPM().getPlayer(player).isDwarf() || !dvz.getGM().isStarted() || (Util.isTest() && !Util.canTest(player))) {
                sender.sendMessage(Util.formatMsg("&cCan only be used as a dwarf!"));
                return true;
            }

            StructureType.STORAGE.getStrucClass().onUse(player);
            dvz.logTimings("Commands.onCommand()[/storage]", t);
            return true;
        }

        if (label.equalsIgnoreCase("furnace") || label.equalsIgnoreCase("furnaces") || label.equalsIgnoreCase("cook") || label.equalsIgnoreCase("smelt")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }
            Player player = (Player)sender;

            if (!dvz.getRewardsCfg().hasFurnaceCmd(player.getUniqueId()) && !dvz.getRewardsCfg().hasStructureCmds(player.getUniqueId())) {
                if (!player.isOp() && !player.hasPermission("cmd.furnace")) {
                    sender.sendMessage(Util.formatMsg("&3Purchase &a&lVIP &3to be able to use the virtual furnace."));
                    return true;
                }
            }

            if (!dvz.getPM().getPlayer(player).isDwarf() || !dvz.getGM().isStarted() || (Util.isTest() && !Util.canTest(player))) {
                sender.sendMessage(Util.formatMsg("&cCan only be used as a dwarf!"));
                return true;
            }

            StructureType.FURNACE.getStrucClass().onUse(player);
            dvz.logTimings("Commands.onCommand()[/furnace]", t);
            return true;
        }

        if (label.equalsIgnoreCase("enchant") || label.equalsIgnoreCase("enchanting")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }
            Player player = (Player)sender;

            if (!dvz.getRewardsCfg().hasEnchantCmd(player.getUniqueId()) && !dvz.getRewardsCfg().hasStructureCmds(player.getUniqueId())) {
                if (!player.isOp() && !player.hasPermission("cmd.enchant")) {
                    sender.sendMessage(Util.formatMsg("&3Purchase &a&lVIP &3to be able to use virtual enchanting."));
                    return true;
                }
            }

            if (!dvz.getPM().getPlayer(player).isDwarf() || !dvz.getGM().isStarted() || (Util.isTest() && !Util.canTest(player))) {
                sender.sendMessage(Util.formatMsg("&cCan only be used as a dwarf!"));
                return true;
            }

            StructureType.ENCHANT.getStrucClass().onUse(player);
            return true;
        }

        if (label.equalsIgnoreCase("disposal") || label.equalsIgnoreCase("dispose") || label.equalsIgnoreCase("clear")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                return true;
            }
            Player player = (Player)sender;

            if (!dvz.getPM().getPlayer(player).isDwarf() || !dvz.getGM().isStarted() || (Util.isTest() && !Util.canTest(player))) {
                sender.sendMessage(Util.formatMsg("&cCan only be used as a dwarf!"));
                return true;
            }

            StructureType.DEPOSIT.getStrucClass().onUse(player);
            dvz.logTimings("Commands.onCommand()[/disposal]", t);
            return true;
        }


        if (label.equalsIgnoreCase("dvz") || label.equalsIgnoreCase("dvz:dvz") || label.equalsIgnoreCase("d") || label.equalsIgnoreCase("dz") || label.equalsIgnoreCase("dvsz")) {
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
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " {keep|wall|shrine|ws} &8- &5TP to these locations."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " classes [dwarf|monster] &8- &5List all classes."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " class [name] &8- &5Detailed class info."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " abilities [class] &8- &5List all abilities."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " ability [name] &8- &5Detailed ability info."));
                    dvz.logTimings("Commands.onCommand()[/dvz help]", t);
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
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " end [reason] &8- &5Force end the game."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " test [player] &8- &5Toggle test mode/add test players."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " speed [value] &8- &5Set the game speed (def:0)"));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " dragon [type] &8- &5Set yourself to be the dragon."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " power {-100<>100} &8- &5Add/remove extra monster power."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " setclass {type} [player] &8- &5Force set your class."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " product [name] [amt] &8- &5Give yourself [amt] products."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " castitem [name] [amt] &8- &5Give yourself [amt] cast items."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " fixws [player] &8- &5Rebuild someone his workshop."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " loc {name} [block] &8- &5Set a location at ur location."));
                    sender.sendMessage(CWUtil.integrateColor("&7(Or at the block on cursor within 5 blocks if 'block' is specified)"));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " cub {name} &8- &5Save a cuboid selection. (wand = /arw)"));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " save &8- &5Save everything."));
                    sender.sendMessage(CWUtil.integrateColor("&6/" + label + " reload [force] &8- &5Reload configs."));
                    sender.sendMessage(CWUtil.integrateColor("&7(If you use force it wont first save the configs. &4Careful&7!)"));
                    dvz.logTimings("Commands.onCommand()[/dvz admin]", t);
                    return true;
                }


                //keep|wall|shrine|ws
                //##########################################################################################################################
                //######################################################## /dvz keep #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("keep") || args[0].equalsIgnoreCase("dwarf")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;
                    CWPlayer cwp = pm.getPlayer(player);

                    if (!cwp.isDwarf()) {
                        sender.sendMessage(Util.formatMsg("&cYou have to be a dwarf to use this command."));
                        return true;
                    }

                    if (!gm.isStarted() || (Util.isTest() && !Util.canTest(player))) {
                        sender.sendMessage(Util.formatMsg("&cThe game isn't started or has already ended!"));
                        return true;
                    }

                    cwp.timedTeleport(dvz.getMM().getActiveMap().getLocation("dwarf").add(0,1,0), 5, "the keep");
                    dvz.logTimings("Commands.onCommand()[/dvz keep]", t);
                    return true;
                }

                //##########################################################################################################################
                //####################################################### /dvz shrine ######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("shrine")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;
                    CWPlayer cwp = pm.getPlayer(player);

                    if (!cwp.isDwarf()) {
                        sender.sendMessage(Util.formatMsg("&cYou have to be a dwarf to use this command."));
                        return true;
                    }

                    if (!gm.isStarted() || (Util.isTest() && !Util.canTest(player))) {
                        sender.sendMessage(Util.formatMsg("&cThe game isn't started or has already ended!"));
                        return true;
                    }

                    cwp.timedTeleport(dvz.getMM().getActiveMap().getCuboid("shrine2keep").getCenterLoc().add(0, 3, 0), 5, "the shrine");
                    dvz.logTimings("Commands.onCommand()[/dvz shrine]", t);
                    return true;
                }

                //##########################################################################################################################
                //######################################################## /dvz wall #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("wall")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;
                    CWPlayer cwp = pm.getPlayer(player);

                    if (!cwp.isDwarf()) {
                        sender.sendMessage(Util.formatMsg("&cYou have to be a dwarf to use this command."));
                        return true;
                    }

                    if (!gm.isStarted() || (Util.isTest() && !Util.canTest(player))) {
                        sender.sendMessage(Util.formatMsg("&cThe game isn't started or has already ended!"));
                        return true;
                    }

                    if (!gm.isDwarves() && gm.getState() != GameState.MONSTERS && gm.getState() != GameState.DRAGON) {
                        sender.sendMessage(Util.formatMsg("&cThe monsters captured the wall!"));
                        return true;
                    }

                    cwp.timedTeleport(dvz.getMM().getActiveMap().getLocation("wall").add(0,1,0), 5, "the wall");
                    dvz.logTimings("Commands.onCommand()[/dvz wall]", t);
                    return true;
                }

                //##########################################################################################################################
                //######################################################### /dvz ws ########################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("ws") || args[0].equalsIgnoreCase("workshop")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;
                    CWPlayer cwp = pm.getPlayer(player);

                    if (!cwp.isDwarf()) {
                        sender.sendMessage(Util.formatMsg("&cYou have to be a dwarf to use this command."));
                        return true;
                    }

                    if (!dvz.getWM().hasWorkshop(player.getUniqueId()) || !dvz.getWM().getWorkshop(player.getUniqueId()).isBuild()) {
                        sender.sendMessage(Util.formatMsg("&cYou have no workshop."));
                        return true;
                    }

                    Location loc = dvz.getWM().getWorkshop(player.getUniqueId()).getCuboid().getMinLoc().add(2, 2, -1);
                    cwp.timedTeleport(loc, 5, "your workshop");
                    dvz.logTimings("Commands.onCommand()[/dvz ws]", t);
                    return true;
                }


                //##########################################################################################################################
                //####################################################### /dvz switch ######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("switch")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;
                    CWPlayer cwp = pm.getPlayer(player);

                    if (!cwp.isDwarf() || (Util.isTest() && !Util.canTest(player))) {
                        sender.sendMessage(Util.formatMsg("&cYou have to be a dwarf to use this command."));
                        return true;
                    }
                    
                    if (cwp.getClassOptions().size() <= 0) {
                        sender.sendMessage(Util.formatMsg("&cyou have no more class options available."));
                        return true;
                    }

                    dvz.getCM().showSwitchOptionsMenu(player);
                    sender.sendMessage(Util.formatMsg("&6Choose a class to switch to."));
                    dvz.logTimings("Commands.onCommand()[/dvz switch]", t);
                    return true;
                }


                //##########################################################################################################################
                //############################################## /dvz classes [dwarf|monster] ##############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("classes") || args[0].equalsIgnoreCase("classlist") || args[0].equalsIgnoreCase("cl")) {
                    Map<DvzClass, BaseClass> classes = null;
                    Map<ClassType, String> classStrings = new HashMap<ClassType, String>();
                    if (args.length > 1) {
                        ClassType type = ClassType.fromString(args[1]);
                        if (type != null) {
                            classes = cm.getClasses(type);
                            classStrings.put(type, type.getColor() + CWUtil.capitalize(type.toString().toLowerCase()) + "&8: &7");
                        }
                    }
                    if (classes == null || classes.size() < 1) {
                        classes = cm.getClasses(null);
                        for (ClassType type : ClassType.values()) {
                            if (type == ClassType.BASE) {
                                continue;
                            }
                            classStrings.put(type, type.getColor() + CWUtil.capitalize(type.toString().toLowerCase()) + "&8: &7");
                        }
                    }

                    for (DvzClass dvzClass : classes.keySet()) {
                        if (dvzClass.isBaseClass()) {
                            continue;
                        }
                        String classString = classStrings.get(dvzClass.getType());
                        classString += dvzClass.getClassClass().getDisplayName() + "&8, ";
                        classStrings.put(dvzClass.getType(), classString);
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========== &4&lALL DVZ CLASSES &8=========="));
                    for (String str : classStrings.values()) {
                        sender.sendMessage(CWUtil.integrateColor(str.substring(0, str.length() - 2)));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz classes]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################### /dvz class [class] ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("class") || args[0].equalsIgnoreCase("c")) {
                    //Try get player class if none specified.
                    DvzClass dvzClass = null;
                    if (args.length < 2) {
                        if (sender instanceof Player) {
                            Player player = (Player)sender;
                            CWPlayer cwp = pm.getPlayer(player);
                            dvzClass = cwp.getPlayerClass();
                        }
                        if (dvzClass == null || dvzClass.isBaseClass()) {
                            sender.sendMessage(Util.formatMsg("&cYou have no class. To check a specific class add a class name as last arg."));
                            return true;
                        }
                    }

                    //Get class name from cmd arg.
                    if (args.length >= 2) {
                        dvzClass = DvzClass.fromString(args[1]);
                        if (dvzClass == null || dvzClass.isBaseClass()) {
                            sender.sendMessage(Util.formatMsg("&cInvalid class specified. See &4/dvz classes &cfor a list."));
                            return true;
                        }
                    }

                    BaseClass baseClass = dvzClass.getClassClass();
                    sender.sendMessage(CWUtil.integrateColor("&8========== &4&lCLASS DETAILS &8=========="));
                    sender.sendMessage(CWUtil.integrateColor("&6Class&8: &5" + baseClass.getDisplayName()));
                    sender.sendMessage(CWUtil.integrateColor("&6Type&8: &5" + CWUtil.capitalize(dvzClass.getType().toString().toLowerCase())));
                    sender.sendMessage(CWUtil.integrateColor("&6Rarity&8: &5" + baseClass.getWeight()));
                    sender.sendMessage(CWUtil.integrateColor("&6Health&8: &5" + baseClass.getHealth()));
                    sender.sendMessage(CWUtil.integrateColor("&6Speed&8: &5" + baseClass.getSpeed() + "&7(&80.2 = default&7)"));
                    if (dvzClass.getType() == ClassType.DWARF) {
                        sender.sendMessage(CWUtil.integrateColor("&6Task&8: &5" + baseClass.getTask()));
                        sender.sendMessage(CWUtil.integrateColor("&6Production&8: &5" + baseClass.getProduce()));
                    }

                    List<String> abilities = new ArrayList<String>();
                    Set<Ability> abilitiesList = baseClass.getAbilities();
                    for (Ability ability : abilitiesList) {
                        abilities.add(CWUtil.capitalize(ability.toString().toLowerCase()));
                    }
                    sender.sendMessage(CWUtil.integrateColor("&6Abilities&8: &7" + CWUtil.implode(abilities, "&8, &7")));
                    dvz.logTimings("Commands.onCommand()[/dvz class]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################# /dvz abilities [class] #################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("abilities") || args[0].equalsIgnoreCase("abilitylist") || args[0].equalsIgnoreCase("al")) {
                    DvzClass[] dvzClasses = DvzClass.values();

                    if (args.length > 1) {
                        DvzClass dvzClass = DvzClass.fromString(args[1]);
                        if (dvzClass != null) {
                            dvzClasses = new DvzClass[] {dvzClass};
                        }
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========== &4&lALL DVZ ABILITIES &8=========="));
                    for (DvzClass dvzClass : dvzClasses) {
                        if (dvzClass == DvzClass.BASE || dvzClass == DvzClass.DWARF || dvzClass == DvzClass.MONSTER || dvzClass == DvzClass.DRAGON) {
                            continue;
                        }

                        String name = dvzClass.getType().getColor() + CWUtil.stripAllColor(dvzClass.getClassClass().getDisplayName());
                        String str = "";
                        Set<Ability> abilities = dvzClass.getClassClass().getAbilities();
                        for (Ability ability : abilities) {
                            String abilityString = ability.getAbilityClass().getDisplayName().isEmpty() ? ability.toString().toLowerCase().replace("_", "") : ability.getAbilityClass().getDisplayName();
                            if (ability.getAbilityClass().getDvzClasses().size() > 1) {
                                str += "&8&o" + CWUtil.stripAllColor(abilityString) + "&8, ";
                            } else {
                                str += "&7" + CWUtil.stripAllColor(abilityString) + "&8, ";
                            }
                        }
                        sender.sendMessage(CWUtil.integrateColor(name + "&8: &7" + str.substring(0, str.length() - 2)));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz abilties]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################# /dvz ability [ability] #################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("ability") || args[0].equalsIgnoreCase("a")) {
                    //Try get player ability if none specified.
                    Ability ability = null;
                    if (args.length < 2 && sender instanceof Player) {
                        Player player = (Player)sender;
                        CWPlayer cwp = pm.getPlayer(player);
                        DvzClass dvzClass = cwp.getPlayerClass();

                        if (dvzClass == null || dvzClass.isBaseClass()) {
                            sender.sendMessage(Util.formatMsg("&cYou have no class. To check a specific ability add an ability name as last arg."));
                            return true;
                        }

                        //Check castitem with holding item.
                        Set<Ability> abilities = dvzClass.getClassClass().getAbilities();
                        for (Ability a : abilities) {
                            if (CWUtil.compareItems(player.getItemInHand(), a.getAbilityClass().getCastItem(),true, false)) {
                                ability = a;
                                break;
                            }
                        }

                        if (ability == null || ability == Ability.BASE) {
                            sender.sendMessage(Util.formatMsg("&cYou're not holding an ability cast item. To check a specific ability add an ability name as last arg."));
                            return true;
                        }
                    }

                    //Get ability from cmd arg.
                    if (args.length >= 2) {
                        ability = Ability.fromString(args[1]);
                        if (ability == null || ability == Ability.BASE) {
                            sender.sendMessage(Util.formatMsg("&cInvalid ability specified. See &4/dvz abilities &cfor a list."));
                            return true;
                        }
                    }

                    BaseAbility baseAbility = ability.getAbilityClass();
                    sender.sendMessage(CWUtil.integrateColor("&8========== &4&lABILITY DETAILS &8=========="));
                    sender.sendMessage(CWUtil.integrateColor("&7&o" + baseAbility.getDesc()));
                    sender.sendMessage(CWUtil.integrateColor("&6Ability&8: &5" + baseAbility.getDisplayName()));
                    List<String> dvzClasses = new ArrayList<String>();
                    for (DvzClass dvzClass : DvzClass.values()) {
                        if (!dvzClass.isBaseClass() && dvzClass.getClassClass().getAbilities().contains(ability)) {
                            dvzClasses.add(CWUtil.capitalize(dvzClass.toString().toLowerCase()));
                        }
                    }
                    sender.sendMessage(CWUtil.integrateColor("&6Classes&8: &5" + CWUtil.implode(dvzClasses, "&8, &5")));
                    if (baseAbility.getCastItem() != null) {
                        sender.sendMessage(CWUtil.integrateColor("&6Cast item&8: &5" + CWCore.inst().getMaterials().getDisplayName(baseAbility.getCastItem().getType(), baseAbility.getCastItem().getDurability())));
                        sender.sendMessage(CWUtil.integrateColor("&6Usage&8: &5" + baseAbility.getUsage().replace("this item", CWCore.inst().getMaterials().getDisplayName(baseAbility.getCastItem().getType(), baseAbility.getCastItem().getDurability()))));
                    } else {
                        sender.sendMessage(CWUtil.integrateColor("&6Usage&8: &5" + baseAbility.getUsage()));
                    }

                    if (baseAbility.getCooldown() <= 0) {
                        sender.sendMessage(CWUtil.integrateColor("&6Cooldown&8: &cNo cooldown"));
                    } else {
                        String cdPlayer = "";
                        long timeLeft = 0;
                        if (sender instanceof Player) {
                            CWPlayer cwp = pm.getPlayer((Player)sender);
                            String tag = cwp.getPlayerClass().toString().toLowerCase() + "-" + ability.toString().toLowerCase();
                            CooldownManager.Cooldown cd = cwp.getCDM().getCooldown(tag);
                            if (cd != null) {
                                timeLeft = cd.getTimeLeft();
                            }
                        }
                        if (timeLeft <= 0) {
                            cdPlayer = "&20s";
                        } else {
                            if (timeLeft > 60000) {
                                cdPlayer = CWUtil.formatTime(timeLeft, "&4%M&8:&4%S&cm");
                            } else {
                                cdPlayer = CWUtil.formatTime(timeLeft, "&4%S&8.&4%%%&cs");
                            }
                        }
                        sender.sendMessage(CWUtil.integrateColor("&6Cooldown&8: &5" + cdPlayer + "&8/" + CWUtil.formatTime((long)baseAbility.getCooldown(), "&5%S&ds")));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz ability]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################ /dvz product [name] [amt] ###############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("product")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (args.length <= 1 || Product.fromString(args[1]) == null) {
                        sender.sendMessage(Util.formatMsg("&cInvalid product name specified."));
                        String products = "&c";
                        for (Product p : Product.values()) {
                            products += p.toString().toLowerCase().replace("_","") + "&8, &c";
                        }
                        sender.sendMessage(Util.formatMsg("&4Products&8: &c" + products));
                        return true;
                    }
                    Product product = Product.fromString(args[1]);

                    int amount = 1;
                    if (args.length > 2) {
                        amount = Math.max(CWUtil.getInt(args[2]), 1);
                    }

                    product.getItem(amount).giveToPlayer(player);
                    dvz.logTimings("Commands.onCommand()[/dvz product]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################ /dvz castitem [name] [amt] ###############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("castitem")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (args.length <= 1 || Ability.fromString(args[1]) == null) {
                        sender.sendMessage(Util.formatMsg("&cInvalid ability specified."));
                        String abilities = "&c";
                        for (Ability a : Ability.values()) {
                            abilities += a.toString().toLowerCase().replace("_","") + "&8, &c";
                        }
                        sender.sendMessage(Util.formatMsg("&4Abilities&8: &c" + abilities));
                        return true;
                    }
                    Ability ability = Ability.fromString(args[1]);

                    int amount = 1;
                    if (args.length > 2) {
                        amount = Math.max(CWUtil.getInt(args[2]), 1);
                    }

                    if (ability.getAbilityClass().getCastItem() == null) {
                        sender.sendMessage(Util.formatMsg("This ability has no cast item!"));
                        return true;
                    }

                    CWItem item = ability.getAbilityClass().getCastItem().clone();
                    item.setAmount(amount);
                    item.giveToPlayer(player);
                    dvz.logTimings("Commands.onCommand()[/dvz castitem]", t);
                    return true;
                }



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
                    dvz.logTimings("Commands.onCommand()[/dvz reset]", t);
                    return true;
                }


                //##########################################################################################################################
                //######################################################## /dvz open #######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("open")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (gm.getState() != GameState.SETUP) {
                        sender.sendMessage(Util.formatMsg("&cReset the game first before opening!"));
                        //return true;
                    }

                    gm.openGame((Player)sender);
                    dvz.logTimings("Commands.onCommand()[/dvz open]", t);
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
                    dvz.logTimings("Commands.onCommand()[/dvz start]", t);
                    return true;
                }


                //##########################################################################################################################
                //######################################################## /dvz end ########################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("end")) {
                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (!gm.isStarted()) {
                        sender.sendMessage(Util.formatMsg("&cThe game has to be started before it can be ended!"));
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
                    sender.sendMessage(Util.formatMsg("&6You have ended the game!"));
                    dvz.logTimings("Commands.onCommand()[/dvz end]", t);
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
                    dvz.logTimings("Commands.onCommand()[/dvz speed]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################# /dvz power [-100<>100] #################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("power")) {
                    if (args.length < 2) {
                        sender.sendMessage(Util.formatMsg("&6Extra monster power&8: &5" + dvz.getGameCfg().EXTRA_MONSTER_POWER + "%"));
                        sender.sendMessage(Util.formatMsg("&5Specify a value between -100 and 100 to modify it. &7(default:0)"));
                        return true;
                    }

                    if (!sender.isOp() && !sender.hasPermission("dvz.admin")) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    dvz.getGameCfg().EXTRA_MONSTER_POWER = Math.min(Math.max(CWUtil.getInt(args[1]), -100), 100);
                    sender.sendMessage(Util.formatMsg("&6Extra monster power set to&8: &5" + dvz.getGameCfg().EXTRA_MONSTER_POWER + "%"));
                    dvz.logTimings("Commands.onCommand()[/dvz power]", t);
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
                        gm.createDragon(false);
                    } else {
                        player.sendMessage(Util.formatMsg("&6You will be the " + CWUtil.capitalize(gm.getDragonType().toString().toLowerCase()) + " when the second night falls."));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz dragon]", t);
                    return true;
                }



                //##########################################################################################################################
                //############################################# /dvz setclass {class} [player] #############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("setclass")) {
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
                        sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/" + label + " " + args[0] + " {type/name} [player]"));
                        return true;
                    }

                    if (!gm.isStarted()) {
                        sender.sendMessage(Util.formatMsg("&cThe game has to be started first."));
                        return true;
                    }

                    if (args.length > 2) {
                        player = dvz.getServer().getPlayer(args[2]);
                        if (player == null) {
                            player.sendMessage(Util.formatMsg("&cInvalid player specified!"));
                            return true;
                        }
                    }

                    DvzClass dvzClass = DvzClass.fromString(args[1]);
                    if (dvzClass == null) {
                        player.sendMessage(Util.formatMsg("&cInvalid class specified."));
                        return true;
                    }

                    CWPlayer cwp = dvz.getPM().getPlayer(player);

                    cwp.resetData();
                    cwp.undisguise();
                    if (dvz.getWM().hasWorkshop(player.getUniqueId())) {
                        dvz.getWM().getWorkshop(player.getUniqueId()).destroy();
                    }
                    dvz.getWM().removeWorkshop(player.getUniqueId(), true);

                    if (dvzClass.getType() == ClassType.DRAGON) {
                        dvz.getGM().setDragonPlayer(player.getUniqueId());
                        dvz.getGM().setDragonType(dvzClass);
                        dvz.getGM().createDragon(true);
                    } else {
                        cwp.setClass(dvzClass);
                    }
                    player.sendMessage(Util.formatMsg("&6Your class has been force set to &5" + args[1]));
                    if (!player.equals(sender)) {
                        player.sendMessage(Util.formatMsg("&6You have set &5" + player.getName() + " &6his class to &5" + args[1]));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz setclass]", t);
                    return true;
                }



                //##########################################################################################################################
                //################################################## /dvz fixws {player} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("fixws")) {
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
                        sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/" + label + " " + args[0] + " {player}"));
                        return true;
                    }

                    Player target = dvz.getServer().getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(Util.formatMsg("&cInvalid player specified!"));
                        return true;
                    }

                    if (!dvz.getWM().hasWorkshop(target.getUniqueId())) {
                        player.sendMessage(Util.formatMsg("&cThis player doesn't have a workshop!"));
                        return true;
                    }

                    WorkShop ws = dvz.getWM().getWorkshop(target.getUniqueId());
                    if (ws.isBuild()) {
                        ws.destroy();
                    }
                    ws.build(null);

                    target.sendMessage(Util.formatMsg("&6Your has been recreated by &5" + player.getName()));
                    if (!player.equals(target)) {
                        player.sendMessage(Util.formatMsg("&6You rebuild &5" + target.getName() + " &6his workshop!"));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz fixws]", t);
                    return true;
                }




                //##########################################################################################################################
                //################################################### /dvz test [player] ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("test")) {
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
                        dvz.getGameCfg().TEST_MODE = !dvz.getGameCfg().TEST_MODE;
                        if (dvz.getGameCfg().TEST_MODE) {
                            dvz.getServer().broadcastMessage(CWUtil.integrateColor("&6&lThe server is now in test mode."));
                            player.sendMessage(CWUtil.integrateColor("&cDon't forget to turn in back off!"));
                        } else {
                            dvz.getServer().broadcastMessage(CWUtil.integrateColor("&6&lThe server is no longer in test mode."));
                        }
                        dvz.logTimings("Commands.onCommand()[/dvz test]", t);
                        return true;
                    }

                    Player target = dvz.getServer().getPlayer(args[1]);
                    if (target == null) {
                        player.sendMessage(Util.formatMsg("&cInvalid player specified!"));
                        return true;
                    }

                    CWPlayer cwp = dvz.getPM().getPlayer(target);
                    cwp.setTesting(!cwp.isTesting());

                    if (cwp.isTesting()) {
                        sender.sendMessage(Util.formatMsg("&6Added &a" + target.getName() + " &6to the test game/mode."));
                        cwp.sendMessage(CWUtil.integrateColor("&6&lYou have been added to the test game!"));
                        cwp.sendMessage(CWUtil.integrateColor("&4&lThis is a test game so don't expect to be able to play normally!"));
                        if (dvz.getGM().isStarted() && (cwp.getPlayerClass() == null || cwp.getPlayerClass().isBaseClass())) {
                            cwp.giveClassItems(ClassType.DWARF, false, 10);
                        }
                    } else {
                        sender.sendMessage(Util.formatMsg("&cRemoved &4" + target.getName() + " &cfrom the test game/mode."));
                        cwp.sendMessage(CWUtil.integrateColor("&c&lYou have been removed from the test game!"));
                        cwp.undisguise();
                        cwp.reset();
                        cwp.resetData();
                        dvz.getPM().removePlayer(cwp, true);
                        target.teleport(dvz.getCfg().getDefaultWorld().getSpawnLocation().add(0,1,0));
                    }
                    dvz.logTimings("Commands.onCommand()[/dvz test {player}]", t);
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
                    String[] locNames = dvz.getMM().getLocationNames();
                    for (String locName : locNames) {
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
                            Block block = player.getTargetBlock((Set<Material>)null, 5);
                            if (block.getType() != Material.AIR) {
                                loc = block.getLocation();
                            }
                        }
                    }

                    dvz.getMM().getActiveMap().setLocation(args[1], loc);
                    player.sendMessage(Util.formatMsg("&6Set location &8'&5" + args[1] + "&8' &6at: &8(&5" + loc.getBlockX() + "&8, &5" + loc.getBlockY() + "&8, &5" + loc.getBlockZ() + "&8)"));
                    dvz.logTimings("Commands.onCommand()[/dvz loc]", t);
                    return true;
                }



                //##########################################################################################################################
                //#################################################### /dvz cub {type} #####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("cub") || args[0].equalsIgnoreCase("cuboid")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("&cPlayer command only."));
                        return true;
                    }
                    final Player player = (Player)sender;

                    if (!player.isOp() && !player.hasPermission("dvz.admin")) {
                        player.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (args.length < 2) {
                        sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/" + label + " " + args[0] + " {type/name}"));
                        sender.sendMessage(Util.formatMsg("&4Names&8: &c" + CWUtil.implode(dvz.getMM().getCuboidNames(), "&8, &c")));
                        return true;
                    }

                    if (dvz.getMM().getActiveMap() == null) {
                        sender.sendMessage(Util.formatMsg("&cThere is no map active right now."));
                        return true;
                    }

                    boolean cubMatch = false;
                    String[] cubNames = dvz.getMM().getCuboidNames();
                    for (String cubName : cubNames) {
                        if (cubName.equalsIgnoreCase(args[1])) {
                            cubMatch = true;
                            break;
                        }
                    }
                    if (!cubMatch) {
                        sender.sendMessage(Util.formatMsg("&cInvalid cuboid name specified!"));
                        sender.sendMessage(Util.formatMsg("&4Names&8: &c" + CWUtil.implode(dvz.getMM().getCuboidNames(), "&8, &c")));
                        return true;
                    }

                    if (CWCore.inst().getSel().getStatus(player) == SelectionStatus.NONE) {
                        sender.sendMessage(Util.formatMsg("&cYou haven't selected a cuboid."));
                        sender.sendMessage(Util.formatMsg("&cUse &4/arw &cto get the wand and select two points."));
                        return true;
                    }

                    if (CWCore.inst().getSel().getStatus(player) == SelectionStatus.POS2) {
                        sender.sendMessage(Util.formatMsg("&cYou haven't selected position1."));
                        return true;
                    }

                    if (CWCore.inst().getSel().getStatus(player) == SelectionStatus.POS1) {
                        sender.sendMessage(Util.formatMsg("&cYou haven't selected position2."));
                        return true;
                    }

                    final Cuboid cuboid = CWCore.inst().getSel().getSelection(player);
                    if (cuboid == null) {
                        sender.sendMessage(Util.formatMsg("&cFailed at loading your selected cuboid. Did you select it?"));
                        return true;
                    }

                    dvz.getMM().getActiveMap().setCuboid(args[1], cuboid);
                    player.sendMessage(Util.formatMsg("&6Set cuboid &8'&5" + args[1] + "&8'!"));
                    player.sendMessage(Util.formatMsg("&7Look at the particles to make sure the cuboid is set properly."));

                    new BukkitRunnable() {
                        int particles = 0;
                        Vector halfBlock = new Vector(0.5f, 0.5f, 0.5f);

                        @Override
                        public void run() {
                            particles++;
                            List<Vector> edgeVectors = cuboid.getEdgeVectors();
                            for (Vector vector : edgeVectors) {
                                ParticleEffect.CLOUD.display(0.3f, 0.3f, 0.3f, 0f, 5, vector.add(halfBlock).toLocation(player.getWorld()), 300);
                            }
                            if (particles > 10) {
                                cancel();
                            }
                        }
                    }.runTaskTimer(dvz, 0, 20);
                    dvz.logTimings("Commands.onCommand()[/dvz cub]", t);
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
                    dvz.logTimings("Commands.onCommand()[/dvz save]", t);
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
                    dvz.getStrucCfg().load();
                    dvz.getAbilityCfg().load();
                    dvz.getClassesCfg().load();
                    dvz.getMapCfg().load();

                    sender.sendMessage(Util.formatMsg("&6All configs reloaded" + (force ? " with force. &8(&4Not saved!&8)" : ".")));
                    dvz.logTimings("Commands.onCommand()[/dvz reload]", t);
                    return true;
                }


                //##########################################################################################################################
                //###################################################### /dvz timings ######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("timings")) {
                    if (sender instanceof Player && !sender.isOp()) {
                        sender.sendMessage(Util.formatMsg("Insufficient permissions."));
                        return true;
                    }

                    if (args.length < 2) {
                        sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/" + label + " " + args[0] + " {start|stop|pause|resume}"));
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("start")) {
                        if (dvz.startTimings()) {
                            sender.sendMessage(Util.formatMsg("&aTimings are now being logged!"));
                        } else {
                            sender.sendMessage(Util.formatMsg("&cTimings already running!  &7(Use: /dvz timings stop)"));
                        }
                        dvz.logTimings("Commands.onCommand()[/dvz timings start]", t);
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("stop")) {
                        if (dvz.stopTimings()) {
                            sender.sendMessage(Util.formatMsg("&aNo longer logging timings!"));
                        } else {
                            sender.sendMessage(Util.formatMsg("&cTimings already stopped. &7(Use: /dvz timings start)"));
                        }
                        dvz.logTimings("Commands.onCommand()[/dvz timings stop]", t);
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("pause")) {
                        if (dvz.pauseTimings()) {
                            sender.sendMessage(Util.formatMsg("&aPaused timings logging! &7(When resuming it will log in the same file again)"));
                        } else {
                            sender.sendMessage(Util.formatMsg("&cTimings already paused. &7(Use: /dvz timings resume)"));
                        }
                        dvz.logTimings("Commands.onCommand()[/dvz timings pause]", t);
                        return true;
                    }

                    if (args[1].equalsIgnoreCase("resume")) {
                        if (dvz.resumeTimings()) {
                            sender.sendMessage(Util.formatMsg("&aResumed timings logging!"));
                        } else {
                            sender.sendMessage(Util.formatMsg("&cTimings already running. &7(Use: /dvz timings pause)"));
                        }
                        dvz.logTimings("Commands.onCommand()[/dvz timings resume]", t);
                        return true;
                    }

                    sender.sendMessage(Util.formatMsg("&cInvalid usage. &7/" + label + " " + args[0] + " {start|stop|pause|resume}"));
                    dvz.logTimings("Commands.onCommand()[/dvz timings]", t);
                    return true;
                }
            }

            ShrineType[] shrineTypes = new ShrineType[] {ShrineType.WALL, ShrineType.KEEP_1, ShrineType.KEEP_2};
            String shrineMsg = "";
            for (ShrineType shrineType : shrineTypes) {
                shrineMsg += " &5" + shrineType.toString().toLowerCase().replace("_", "") + "&8:";
                Set<ShrineBlock> shrineBlocks = gm.getShrineBlocks(shrineType);
                for (ShrineBlock shrine : shrineBlocks) {
                    if (shrine.isDestroyed()) {
                        shrineMsg += "&c|";
                    } else {
                        shrineMsg += "&a|";
                    }
                }
            }


            sender.sendMessage(CWUtil.integrateColor("&8========== &4&lDvZ Game Information &8=========="));
            if (gm.getState() == GameState.CLOSED) {
                sender.sendMessage(CWUtil.integrateColor("&6Game State&8: &cThere is no game right now!"));
                sender.sendMessage(CWUtil.integrateColor("&6Next game&8: " + CWUtil.formatTime(Util.timeTillGame(), "&4&l%H&c:&4&l%M&c:&4&l%S", true)));
                sender.sendMessage(CWUtil.integrateColor("&7Check out the wiki for more information about the game!"));
                sender.sendMessage(CWUtil.integrateColor("&9&lwiki.clashwars.com"));
            } else {
                if (Util.isTest()) {
                    sender.sendMessage(CWUtil.integrateColor("&6Game State&8: &c&lServer is in test mode!"));
                } else {
                    sender.sendMessage(CWUtil.integrateColor("&6Game State&8: &5" + gm.getState().getColor() + gm.getState().getName() + " &8(&7Speed: &a" + gm.getSpeed() + "&7)"));
                }

                if (gm.getState() == GameState.DRAGON) {
                    sender.sendMessage(CWUtil.integrateColor("&6Dragon power&8: &d" + gm.getDragonPower() + "&8/&53"));
                }
                sender.sendMessage(CWUtil.integrateColor("&6Monster power&8: &d" + ((int)gm.getMonsterPower(10)) + "&8/&510"));
                sender.sendMessage(CWUtil.integrateColor("&6Shrines&8:" + shrineMsg));
                sender.sendMessage(CWUtil.integrateColor("&6Players&8: &a&l" + dvz.getPM().getPlayers(ClassType.DWARF, true, true).size() + " &2Dwarves &6&lVS &c&l"
                        + dvz.getPM().getPlayers(ClassType.MONSTER, true, true).size() + " &4Zombies &8(&7" + dvz.getServer().getOnlinePlayers().size() + "&8)"));
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    CWPlayer cwp = dvz.getPM().getPlayer(player);
                    sender.sendMessage(CWUtil.integrateColor("&8============== &4&lPersonal Info &8=============="));
                    sender.sendMessage(CWUtil.integrateColor("&6Class&8: &5" + cwp.getPlayerClass().getClassClass().getDisplayName()));
                    if (cwp.isDwarf()) {
                        sender.sendMessage(CWUtil.integrateColor("&6Class LVL/XP&8: &a&l" + cwp.getPlayerData().getClassLvl() + "&8[&2" + cwp.getClassExp() + "&8]"));
                    }
                }
                sender.sendMessage(CWUtil.integrateColor("&8======= &4Use &c/dvz help &4for all commands &8======="));
            }
            dvz.logTimings("Commands.onCommand()[/dvz]", t);
            return true;
        }
        return false;
    }
}
