package com.clashwars.dvz.events;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EventListener;
import java.util.Set;

public class LobbyEvents implements Listener {

    private DvZ dvz;

    public LobbyEvents(DvZ dvz) {
        this.dvz = dvz;
    }


    @EventHandler
    private void interact(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        ItemStack item = event.getItem();

        //Parkour sign
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if ((block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN) && block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                for (String str : sign.getLines()) {
                    if (CWUtil.removeColour(str).equalsIgnoreCase("&9[PARKOUR]")) {
                        event.setCancelled(true);
                        if (cwp.hasCompletedParkour()) {
                            player.sendMessage(Util.formatMsg("&cYou already completed the parkour this game!"));
                        } else {
                            dvz.getServer().broadcastMessage(Util.formatMsg("&5&l" + player.getName() + " &6completed the parkour!"));
                            cwp.setParkourCompleted(true);

                            block.getWorld().playSound(block.getLocation(), Sound.ORB_PICKUP, 100.0f, 0.5f);

                            new BukkitRunnable() {
                                int ticks = 0;

                                @Override
                                public void run() {
                                    ticks++;
                                    if (ticks > 300) {
                                        cancel();
                                    }
                                    ParticleEffect.SPELL_WITCH.display(0.2f, 1.0f, 0.2f, 0.05f, 10, player.getLocation(), 50);
                                }
                            }.runTaskTimer(dvz, 1, 2);

                            //Give extra class item because game already started.
                            if (dvz.getGM().isStarted() && dvz.getGM().getState() != GameState.OPENED) {
                                if (cwp.getPlayerClass() == null || cwp.getPlayerClass() == DvzClass.DWARF) {
                                    Set<DvzClass> classOptions = cwp.getClassOptions();
                                    if (classOptions.size() >= dvz.getCM().getClasses(ClassType.DWARF).size()) {
                                        player.sendMessage(Util.formatMsg("&cYou already received all classes. (No reward)"));
                                        return;
                                    }
                                    cwp.giveClassItems(ClassType.DWARF, false, 1);
                                }
                            }
                        }
                        return;
                    }
                }
            }
        }

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
                if ((c.getClassItem().getItemMeta().hasDisplayName() && !item.getItemMeta().hasDisplayName()) || (!c.getClassItem().getItemMeta().hasDisplayName() && item.getItemMeta().hasDisplayName())) {
                    continue;
                }
                if (item.getItemMeta().hasDisplayName()) {
                    if (!CWUtil.integrateColor(c.getDisplayName()).equalsIgnoreCase(CWUtil.integrateColor(item.getItemMeta().getDisplayName()))) {
                        continue;
                    }
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

            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                player.getInventory().clear();
                player.updateInventory();
                CWUtil.removeItemsFromHand(player, 1);
                cwp.setClass(dvzClass, true);
            } else {
                player.sendMessage(Util.formatMsg("&cLeft click while holding the class item to select it!"));
            }
        }
    }


    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        //Trigger class selection when clicking on the items in inventory.
        String itemName = "";
        if (event.getCurrentItem().hasItemMeta()) {
            ItemMeta meta = event.getCurrentItem().getItemMeta();
            if (meta.hasDisplayName()) {
                itemName = CWUtil.removeColour(meta.getDisplayName());
            }
        }
        for (DvzClass dvzClass : DvzClass.values()) {
            if (dvzClass == null || dvzClass.getClassClass() == null || dvzClass.getClassClass().getClassItem() == null) {
                continue;
            }
            if (CWUtil.removeColour(dvzClass.getClassClass().getClassItem().getName()).equals(itemName)) {
                Player player = (Player)event.getWhoClicked();
                player.getInventory().clear();
                player.updateInventory();
                CWUtil.removeItemsFromHand(player, 1);
                dvz.getPM().getPlayer(player).setClass(dvzClass, true);
                break;
            }
        }
    }


    @EventHandler
    private void entityInteract(PlayerInteractEntityEvent event) {
        //Show information when interacting with mobs in lobby.
        String name = "";
        if (event.getRightClicked() instanceof LivingEntity) {
            LivingEntity npc = (LivingEntity) event.getRightClicked();
            name = npc.getCustomName();
        }

        if (showNpcInformation(event.getPlayer(), name)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void interactAtEntity(PlayerInteractAtEntityEvent event) {
        String name = "";
        if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) event.getRightClicked();
            name = stand.getCustomName();
        }

        if (showNpcInformation(event.getPlayer(), name)) {
            event.setCancelled(true);
        }
    }

    private boolean showNpcInformation(Player player, String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        name = CWUtil.removeColour(name.toLowerCase());

        for (DvzClass dvzClass : DvzClass.values()) {
            if (dvzClass.toString().toLowerCase().equalsIgnoreCase(name)) {
                player.performCommand("dvz class " + name);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    private void serverPing(ServerListPingEvent event) {
        GameState state = dvz.getGM().getState();
        if (state == GameState.CLOSED) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&cNo DvZ right now!"));
        }
        if (state == GameState.SETUP) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&6Setting up..."));
        }
        if (state == GameState.OPENED) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aOpened! &8(&7Join now!&8)"));
        }
        if (state == GameState.DAY_ONE) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7First day!&8)"));
        }
        if (state == GameState.NIGHT_ONE) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7First night!&8)"));
        }
        if (state == GameState.DAY_TWO) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7Second day!&8)"));
        }
        if (state == GameState.NIGHT_TWO) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7Second night!&8)"));
        }
        if (state == GameState.DRAGON) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7Dragon!&8)"));
        }
        if (state == GameState.MONSTERS) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7Monsters released!&8)"));
        }
        if (state == GameState.MONSTERS_WALL) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7Monsters have taken the wall!&8)"));
        }
        if (state == GameState.MONSTERS_KEEP) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&aStarted! &8(&7Monsters have taken the keep!&8)"));
        }
        if (state == GameState.ENDED) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&cEnded! &8(&7There might be another round!&8)"));
        }
    }

}
