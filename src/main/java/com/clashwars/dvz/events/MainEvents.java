package com.clashwars.dvz.events;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.*;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class MainEvents implements Listener {

    private final DvZ dvz;
    private GameManager gm;

    public MainEvents(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
    }

    @EventHandler
    private void levelUp(CWPlayer.ClassLevelupEvent event) {
        CWPlayer cwp = event.getCWPlayer();
        //TODO: Complete event tasks
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();
        if (cwp.getClassOptions() != null && !cwp.getClassOptions().isEmpty()) {
            player.sendMessage(Util.formatMsg("&6Welcome back!"));
            return;
        }

        if (gm.isDwarves()) {
            player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &8Dwarf&6!"));
            cwp.setPlayerClass(DvzClass.DWARF);
            cwp.giveClassItems(ClassType.DWARF, false);
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
            }
        } else if (gm.isMonsters()) {
            player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &4Monster&6!"));
            player.sendMessage(Util.formatMsg("&6This is because the dragon has been killed already."));
            cwp.setPlayerClass(DvzClass.MONSTER);
            cwp.giveClassItems(ClassType.MONSTER, false);
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
            }
        } else if (gm.getState() == GameState.OPENED || gm.getState() == GameState.SETUP) {
            player.sendMessage(Util.formatMsg("&6The game hasn't started yet but it will start soon."));
        } else if (gm.getState() == GameState.CLOSED) {
            player.sendMessage(Util.formatMsg("&cThere is no &4DvZ &cright now!"));
        }

        player.teleport(spawnLoc);
    }


    @EventHandler
    private void playerQuit(PlayerQuitEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }

    @EventHandler
    private void playerKick(PlayerKickEvent event) {
        dvz.getPM().getPlayer(event.getPlayer()).savePlayer();
    }


    @EventHandler
    private void death(PlayerDeathEvent event) {
        Player player = event.getEntity();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        Player killer = player.getKiller();

        event.setDroppedExp(0);

        //Death message
        if (killer != null) {
            if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(killer.getName())) {
                event.setDeathMessage(CWUtil.integrateColor((cwp.getPlayerClass() != null ? cwp.getPlayerClass().getClassClass().getColor() : "&8") + player.getName() + " &7was killed by the dragon!"));
            } else {
                event.setDeathMessage(CWUtil.integrateColor((cwp.getPlayerClass() != null ? cwp.getPlayerClass().getClassClass().getColor() : "&8") + player.getName() + " &7was killed by " + killer.getName() + "!"));
            }
        } else {
            event.setDeathMessage(CWUtil.integrateColor((cwp.getPlayerClass() != null ? cwp.getPlayerClass().getClassClass().getColor() : "&8") + player.getName() + " &7died!"));
        }

        //Dragon died.
        if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(player.getName())) {

            Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe dragon has been killed! &7======="));
            if (killer != null) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &3" + killer.getName() + " &7is the &bDragonSlayer&7!"));
                //TODO: Set DragonSlayer.
            } else {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Couldn't find the killer so there is no DragonSlayer."));
            }
            dvz.getGM().releaseMonsters(false);
        }
    }

    @EventHandler
    private void respawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();
        if (!dvz.getGM().isStarted()) {
            if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
            }
            event.setRespawnLocation(spawnLoc);
            return;
        }
        final CWPlayer cwp = dvz.getPM().getPlayer(player);

        //Death during first day.
        if (cwp.isDwarf() && dvz.getGM().getState() == GameState.DAY_ONE) {
            player.sendMessage(Util.formatMsg("&6You're alive again as Dwarf because it hasn't been night yet."));
            event.setRespawnLocation(dvz.getMM().getActiveMap().getLocation("dwarf"));
            return;
        }

        //Spawn at monster lobby.
        if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
            spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
        }
        event.setRespawnLocation(spawnLoc);

        new BukkitRunnable() {
            public void run() {
                if (dvz.getGM().isStarted()) {
                    if (cwp.isDwarf()) {
                        player.sendMessage(Util.formatMsg("&4&lYou have turned in to a monster!!!"));
                    }

                    boolean suicide = false;
                    if (dvz.getPM().suicidePlayers.contains(player.getUniqueId())) {
                        suicide = true;
                        dvz.getPM().suicidePlayers.remove(player.getUniqueId());
                    }

                    cwp.reset();
                    cwp.setPlayerClass(DvzClass.MONSTER);
                    cwp.giveClassItems(ClassType.MONSTER, suicide);
                }
            }
        }.runTaskLater(dvz, 5);
    }


    @EventHandler
    private void interact(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        //Check for class item usage.
        for (DvzClass dvzClass : DvzClass.values()) {
            BaseClass c = dvzClass.getClassClass();
            if (c == null || c.getClassItem() == null || c.getClassItem().getType() != item.getType()) {
                continue;
            }
            if ((c.getClassItem().hasItemMeta() && !item.hasItemMeta()) || (!c.getClassItem().hasItemMeta() && item.hasItemMeta())) {
                continue;
            }
            if (item.hasItemMeta()) {
                if (!CWUtil.integrateColor(c.getDisplayName()).equalsIgnoreCase(CWUtil.integrateColor(item.getItemMeta().getDisplayName()))) {
                    continue;
                }
            }
            if (!dvz.getGM().isStarted()) {
                player.sendMessage(Util.formatMsg("&cThe game hasn't started yet!"));
                break;
            }
            if (dvzClass.getType() == ClassType.MONSTER && !dvz.getGM().isMonsters()) {
                player.sendMessage(Util.formatMsg("&cThe monsters haven't been released yet."));
                player.sendMessage(Util.formatMsg("&cSee &4/dvz &cfor more info."));
                break;
            }
            cwp.setClass(dvzClass);
            dvzClass.getClassClass().onEquipClass(player);
            event.setCancelled(true);
        }
    }


    @EventHandler
    private void blockPlace(BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.WEB) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.WEB) {
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, dvz.getCfg().WEB_REMOVAL_TIME);
    }

    @EventHandler
    private void fallingBlockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getTo() != Material.WEB) {
            return;
        }

        final Block block = event.getBlock();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.WEB) {
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, dvz.getCfg().WEB_REMOVAL_TIME);

    }



    @EventHandler
    private void invClose(InventoryCloseEvent event) {
        Player player = (Player)event.getPlayer();
        Inventory inv = event.getInventory();

        if (!dvz.getCM().switchMenus.containsKey(player.getUniqueId())) {
            return;
        }

        ItemMenu menu = dvz.getCM().switchMenus.get(player.getUniqueId());
        if (!inv.getTitle().equals(menu.getTitle()) || inv.getSize() != menu.getSize() || !inv.getHolder().equals(player)) {
            return;
        }

        for (int i = 9; i < menu.getSize(); i++) {
            if (menu.getItems()[i] != null && menu.getItems()[i].getType() != Material.AIR) {
                if (player.getInventory().getItem(i - 9) == null || player.getInventory().getItem(i - 9).getType() == Material.AIR) {
                    player.getInventory().setItem(i - 9, menu.getItems()[i]);
                } else {
                    player.getInventory().addItem(menu.getItems()[i]);
                }
                menu.setSlot(new CWItem(Material.AIR), i, null);
            }
        }
        player.updateInventory();

        player.sendMessage(Util.formatMsg("&6You stopped switching to " + DvzClass.fromString(menu.getData())));
        player.sendMessage(Util.formatMsg("&7All items placed in the switch menu have been given back."));
        player.sendMessage(Util.formatMsg("&a&lTIP&8: &7Click in your inv to fix invisible items."));
        return;
    }


    @EventHandler
    private void menuClick(final ItemMenu.ItemMenuClickEvent event) {
        ItemMenu menu = event.getItemMenu();
        final Player player = (Player)event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (menu.getName().equals("switch")) {
            //Switch menu (check for clicking on classes)

            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            for (final DvzClass dvzClass : dvz.getCM().getClasses(ClassType.DWARF).keySet()) {
                if (dvzClass.getClassClass().getClassItem().equals(event.getCurrentItem())) {
                    player.closeInventory();
                    player.sendMessage(Util.formatMsg("&6In a few seconds a menu GUI will appear."));
                    player.sendMessage(Util.formatMsg("&6You can then modify which items you want to keep."));
                    player.sendMessage(Util.formatMsg("&6After you did that click the green button to switch!"));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            dvz.getCM().showSwitchMenu((Player) player, dvzClass);
                        }
                    }.runTaskLater(dvz, 100);
                    return;
                }
            }

        } else if (menu.getName().contains("switch-")) {
            //Switch menu (Modify items to keep after switching)

            event.setCancelled(true);

            CWItem empty = new CWItem(Material.AIR);
            int rawSlot = event.getRawSlot();
            if (rawSlot < menu.getSize()) {
                //Top menu (Items to keep)
                if (rawSlot == 0) {
                    for (int i = 9; i < menu.getSize(); i++) {
                        if (menu.getItems()[i] != null && menu.getItems()[i].getType() != Material.AIR) {
                            if (player.getInventory().getItem(i - 9) == null || player.getInventory().getItem(i - 9).getType() == Material.AIR) {
                                player.getInventory().setItem(i - 9, menu.getItems()[i]);
                            } else {
                                player.getInventory().addItem(menu.getItems()[i]);
                            }
                            menu.setSlot(empty, i, null);
                        }
                    }
                    player.closeInventory();
                    player.sendMessage(Util.formatMsg("&6You stopped switching to " + DvzClass.fromString(menu.getData())));
                    player.sendMessage(Util.formatMsg("&7All items placed in the switch menu have been given back."));
                    return;
                }
                if (rawSlot == 8) {
                    player.sendMessage(Util.formatMsg("&6You will be switched to " + DvzClass.fromString(menu.getData())));
                    player.closeInventory();
                    dvz.getPM().getPlayer(player).switchClass(DvzClass.fromString(menu.getData()), menu);
                    return;
                }

                //Move item from top inv to player inv.
                if (rawSlot >= 9 && rawSlot <= 44) {
                    if (player.getInventory().getItem(rawSlot-9) == null || player.getInventory().getItem(rawSlot-9).getType() == Material.AIR) {
                        player.getInventory().setItem(rawSlot-9, item);
                    } else {
                        player.getInventory().addItem(item);
                    }
                    menu.setSlot(empty, rawSlot, null);
                }
            } else {
                //Bottom menu (Player inventory)
                if (item == null || item.getType() == Material.AIR) {
                    return;
                }
                if (!Product.canKeep(item.getType())) {
                    player.sendMessage(Util.formatMsg("&cThis item can't be kept."));
                    return;
                }

                //First try put item in same spot in menu as it is in inventory else just add it to first available slot.
                if (menu.getItems().length >= event.getSlot() + 9 && (menu.getItems()[event.getSlot() + 9] == null || menu.getItems()[event.getSlot() + 9].getType() == Material.AIR)) {
                    menu.setSlot(new CWItem(item), event.getSlot() + 9, null);
                    player.getInventory().setItem(event.getSlot(), empty);
                    return;
                } else {
                    for (int i = 9; i < menu.getSize() - 9; i++) {
                        if (menu.getItems()[i] == null || menu.getItems()[i].getType() == Material.AIR) {
                            menu.setSlot(new CWItem(item), i, null);
                            player.getInventory().setItem(event.getSlot(), empty);
                            return;
                        }
                    }
                }
                player.sendMessage(Util.formatMsg("&cCan't store more items."));
            }
        }
    }

}
