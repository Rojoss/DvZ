package com.clashwars.dvz.listeners;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Set;

public class LobbyEvents implements Listener {

    private DvZ dvz;

    public LobbyEvents(DvZ dvz) {
        this.dvz = dvz;
    }


    @EventHandler
    private void interact(DelayedPlayerInteractEvent event) {
        Long t = System.currentTimeMillis();
        final Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        ItemStack item = event.getItem();

        //Parkour and pvp class signs.
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if ((block.getType() == Material.SIGN || block.getType() == Material.WALL_SIGN) && block.getState() instanceof Sign) {
                Sign sign = (Sign) block.getState();
                String[] lines = sign.getLines();
                //Pvp class signs.
                if (CWUtil.removeColour(lines[0]).equalsIgnoreCase("&4&l[LEAVE PVP]")) {
                    player.sendMessage(CWUtil.integrateColor("&6You are no longer in &aPvP &6mode!"));
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 0);
                    player.teleport(dvz.getCfg().getDefaultWorld().getSpawnLocation().add(0,1,0));
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(new ItemStack[] {});
                    player.updateInventory();
                    cwp.setPvping(false);
                }
                if (CWUtil.removeColour(lines[0]).equalsIgnoreCase("&4&l[JOIN PVP]")) {
                    player.sendMessage(CWUtil.integrateColor("&6You are now in &4PvP &6mode!"));
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 0);
                    player.teleport(DvZ.pvpArenaSpawn);
                    cwp.setPvping(true);
                }
                if (CWUtil.removeColour(lines[0]).equalsIgnoreCase("&5[CLASS]")) {
                    cwp.setPvping(true);
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(new ItemStack[] {});

                    String className = CWUtil.stripAllColor(lines[1]);
                    if (className.equalsIgnoreCase("warrior")) {
                        CWItem sword = new CWItem(Material.DIAMOND_SWORD);
                        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
                        sword.giveToPlayer(player);

                        player.getInventory().setHelmet(new CWItem(Material.IRON_HELMET));
                        player.getInventory().setChestplate(new CWItem(Material.IRON_CHESTPLATE));
                        player.getInventory().setLeggings(new CWItem(Material.IRON_LEGGINGS));
                        player.getInventory().setBoots(new CWItem(Material.IRON_BOOTS));

                    } else if (className.equalsIgnoreCase("tank")) {
                        new CWItem(Material.STONE_SWORD).giveToPlayer(player);

                        player.getInventory().setHelmet(new CWItem(Material.DIAMOND_HELMET));
                        player.getInventory().setChestplate(new CWItem(Material.DIAMOND_CHESTPLATE));
                        player.getInventory().setLeggings(new CWItem(Material.DIAMOND_LEGGINGS));
                        player.getInventory().setBoots(new CWItem(Material.DIAMOND_BOOTS));

                    } else if (className.equalsIgnoreCase("archer")) {
                        CWItem bow = new CWItem(Material.BOW);
                        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 4);
                        bow.giveToPlayer(player);
                        new CWItem(Material.WOOD_SWORD).giveToPlayer(player);
                        new CWItem(Material.ARROW, 64).giveToPlayer(player);

                        CWItem armor = new CWItem(Material.LEATHER_HELMET);
                        armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        player.getInventory().setHelmet(armor);
                        armor = new CWItem(Material.LEATHER_CHESTPLATE);
                        armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        player.getInventory().setChestplate(armor);
                        armor = new CWItem(Material.LEATHER_LEGGINGS);
                        armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        player.getInventory().setLeggings(armor);
                        armor = new CWItem(Material.LEATHER_BOOTS);
                        armor.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                        player.getInventory().setBoots(armor);
                    }
                    new CWItem(PotionType.INSTANT_HEAL, true, 2).addPotionEffect(PotionEffectType.HEAL, 2, 1).giveToPlayer(player);

                    player.updateInventory();
                    player.sendMessage(CWUtil.integrateColor("&6Equiped the &a" + className + " &6class!"));
                }

                //Parkour sign
                for (String str : lines) {
                    if (CWUtil.removeColour(str).equalsIgnoreCase("&9[PARKOUR]")) {
                        event.setCancelled(true);
                        if (cwp.hasCompletedParkour()) {
                            player.sendMessage(Util.formatMsg("&cYou already completed the parkour this game!"));
                        } else {
                            Util.broadcast("&5&l" + player.getName() + " &6completed the parkour!");
                            cwp.setParkourCompleted(true);
                            dvz.getSM().changeLocalStatVal(player, StatType.GENERAL_TIMES_COMPLETED_PARKOUR, 1);

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
                                if (Util.isTest() && !Util.canTest(player)) {
                                    return;
                                }
                                if (cwp.getPlayerClass() == null || cwp.getPlayerClass() == DvzClass.DWARF) {
                                    Set<DvzClass> classOptions = cwp.getClassOptions();
                                    if (classOptions.size() >= dvz.getCM().getClasses(ClassType.DWARF).size()) {
                                        player.sendMessage(Util.formatMsg("&cYou already received all classes. (No reward)"));
                                        dvz.logTimings("LobbyEvents.interact()[already all classes]", t);
                                        return;
                                    }
                                    cwp.giveRandomClassItems(ClassType.DWARF, 1);
                                    player.updateInventory();
                                }
                            }
                        }
                        dvz.logTimings("LobbyEvents.interact()[parkour sign]", t);
                        return;
                    }
                }
            }
        }

        if (item == null) {
            return;
        }

        //Check for class item usage.
        if (cwp.getPlayerClass() == null || cwp.getPlayerClass().isBaseClass()) {
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
            dvz.logTimings("LobbyEvents.interact()", t);
        }
    }


    @EventHandler
    private void inventoryClick(InventoryClickEvent event) {
        Long t = System.currentTimeMillis();
        //Trigger class selection when clicking on the items in inventory.
        if (event.getInventory().getName().toLowerCase().contains("switch")) {
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }

        Player player = (Player)event.getWhoClicked();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (cwp.getPlayerClass() == null || cwp.getPlayerClass().isBaseClass()) {
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

                    player.getInventory().clear();
                    player.updateInventory();
                    CWUtil.removeItemsFromHand(player, 1);
                    dvz.getPM().getPlayer(player).setClass(dvzClass, true);
                    break;
                }
            }
        }
        dvz.logTimings("LobbyEvents.inventoryClick()", t);
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
        Long t = System.currentTimeMillis();
        if (name == null || name.isEmpty()) {
            return false;
        }
        name = CWUtil.removeColour(name.toLowerCase());

        for (DvzClass dvzClass : DvzClass.values()) {
            if (dvzClass.toString().toLowerCase().equalsIgnoreCase(name) || (name.equalsIgnoreCase("VillagerGolem") && dvzClass == DvzClass.IRON_GOLEM)) {
                player.performCommand("dvz class " + dvzClass.toString().toLowerCase().replace("_",""));
                dvz.logTimings("LobbyEvents.showNpcInformation()", t);
                return true;
            }
        }
        return false;
    }

    @EventHandler
    private void serverPing(ServerListPingEvent event) {
        Long t = System.currentTimeMillis();
        GameState state = dvz.getGM().getState();
        if (state == GameState.CLOSED || Util.isTest()) {
            event.setMotd(CWUtil.integrateColor("&4&lClashWars &6&lDwarves &2&lVS &c&lZombies\n&cNo DvZ right now! &4&l" +
                CWUtil.formatTime(Util.timeTillGame(), "%H&c:&4&l%M&c:&4&l%S", true) + " &ctill the next game!"));
            return;
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
        dvz.logTimings("LobbyEvents.serverPing()", t);
    }

}
