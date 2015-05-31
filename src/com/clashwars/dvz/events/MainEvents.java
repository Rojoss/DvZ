package com.clashwars.dvz.events;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.*;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
        cwp.sendMessage(Util.formatMsg("&a&lLEVEL UP! &8[&7Dwarf abilities will be available soon&8]"));
        //TODO: Give dwarf ability to player.
    }


    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        Location spawnLoc = dvz.getGM().getUsedWorld().getSpawnLocation();

        String titleStr = "&6Welcome to &6&lDvZ&6!";
        String subtitleStr = "";
        if (cwp.getClassOptions() != null && !cwp.getClassOptions().isEmpty()) {
            titleStr = "&6Welcome back to &6&lDvZ&6!";
            subtitleStr = "&9You have joined dvz as a " + cwp.getPlayerClass().getClassClass().getDisplayName() + "&9!";
            player.sendMessage(Util.formatMsg("&6Welcome back!"));
        } else {
            cwp.reset();
            cwp.resetData();
            if (gm.isDwarves()) {
                player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &8Dwarf&6!"));
                cwp.setPlayerClass(DvzClass.DWARF);
                cwp.giveClassItems(ClassType.DWARF, false);
                if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("lobby") != null) {
                    spawnLoc = dvz.getMM().getActiveMap().getLocation("lobby");
                }
                subtitleStr = "&9You have joined DvZ as a &8Dwarf&9!";
            } else if (gm.isMonsters()) {
                player.sendMessage(Util.formatMsg("&6You have joined DvZ as a &4Monster&6!"));
                player.sendMessage(Util.formatMsg("&6This is because the dragon has been killed already."));
                cwp.setPlayerClass(DvzClass.MONSTER);
                cwp.giveClassItems(ClassType.MONSTER, false);
                if (dvz.getMM().getActiveMap() != null && dvz.getMM().getActiveMap().getLocation("monsterlobby") != null) {
                    spawnLoc = dvz.getMM().getActiveMap().getLocation("monsterlobby");
                }
                subtitleStr = "&9You have joined DvZ as a &4Monster&9!";
            } else if (gm.getState() == GameState.OPENED || gm.getState() == GameState.SETUP) {
                player.sendMessage(Util.formatMsg("&6The game hasn't started yet but it will start soon."));
                subtitleStr = "&9The game hasn't started yet but it will start soon.";
            } else if (gm.getState() == GameState.CLOSED) {
                player.sendMessage(Util.formatMsg("&cThere is no &4DvZ &cright now!"));
                subtitleStr = "&c&lThere is &4&lno DvZ &c&lright now.";
            }
        }

        Title title = new Title(titleStr, subtitleStr, 10, 100, 30);
        title.setTimingsToTicks();
        title.send(player);

        CWUtil.setTab(player, " &8======== &6&lDwarves &2VS &c&lZombies &8========", " &6INFO &8>>> &9&lclashwars.com/info &8<<< &6INFO");

        final Location spawnLocFinal = spawnLoc;

        new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(spawnLocFinal);
            }
        }.runTaskLater(dvz, 10);
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
        String prefix = "&8";
        if (cwp.getPlayerClass() != null) {
            if (cwp.getPlayerClass().getType() == ClassType.DWARF) {
                prefix = "&6";
            } else {
                prefix = "&c";
            }
        }
        if (killer != null) {
            if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(killer.getName())) {
                event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7was killed by the dragon!"));
            } else {
                event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7was killed by " + killer.getName() + "!"));
            }
        } else {
            event.setDeathMessage(CWUtil.integrateColor(prefix + player.getName() + " &7died!"));
        }

        //Reset witch/villager data
        if (cwp.getPlayerClass() != null && (cwp.getPlayerClass() == DvzClass.WITCH || cwp.getPlayerClass() == DvzClass.VILLAGER)) {
            cwp.getPlayerData().setbombUsed(false);
            cwp.getPlayerData().setBuffUsed(false);
        }

        //Dragon died.
        if (dvz.getGM().getState() == GameState.DRAGON && dvz.getGM().getDragonPlayer().getName().equalsIgnoreCase(player.getName())) {

            Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe dragon has been killed! &7======="));
            if (killer != null) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &3" + killer.getName() + " &7is the &bDragonSlayer&7!"));
                gm.setDragonSlayer(killer);
            } else {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Couldn't find the killer so there is no DragonSlayer."));
            }
            dvz.getGM().releaseMonsters(false);
        }

        //DragonSlayer died.
        if (dvz.getGM().getDragonSlayer() != null && dvz.getGM().getDragonSlayer().getName().equalsIgnoreCase(player.getName())) {
            dvz.getGM().resetDragonSlayer();
            dvz.getServer().broadcastMessage(Util.formatMsg("&d&lThe DragonSlayer died!"));
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
                        player.sendMessage(Util.formatMsg("&4&lYou have turned into a monster!!!"));
                    }

                    boolean suicide = false;
                    if (dvz.getPM().suicidePlayers.contains(player.getUniqueId())) {
                        suicide = true;
                        dvz.getPM().suicidePlayers.remove(player.getUniqueId());
                    }
                    if (cwp.getPlayerData().isBuffed()) {
                        cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 999999, 0));
                    }
                    cwp.reset();
                    cwp.setPlayerClass(DvzClass.MONSTER);
                    cwp.giveClassItems(ClassType.MONSTER, suicide);
                }
            }
        }.runTaskLater(dvz, 15);
    }


    @EventHandler
    private void damage(EntityDamageEvent event) {
        //No durability loss
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            for (ItemStack armor : ((Player)event.getEntity()).getInventory().getArmorContents()) {
                armor.setDurability((short)0);
            }
            if (dvz.getPM().getPlayer(player).getPlayerClass() == DvzClass.DWARF  || dvz.getPM().getPlayer(player).getPlayerClass() == DvzClass.MONSTER)  {
                event.setCancelled(true);
            }
        }

        //No fall damage during dwarf time.
        if (dvz.getGM().isDwarves()) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    private void onDamageByEntity(EntityDamageByEntityEvent event) {
        //Custom block enchantment.
        //Per level block 0.5 hearth extra while blocking and 1.0 per level if also sneaking.
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damaged = (Player)event.getEntity();

        if (!damaged.isBlocking()) {
            return;
        }

        if (!damaged.getItemInHand().getEnchantments().containsKey(Enchantment.DURABILITY)) {
            return;
        }

        int enchantLvl = damaged.getItemInHand().getEnchantmentLevel(Enchantment.DURABILITY);
        boolean sneaking = damaged.isSneaking();
        event.setDamage(event.getDamage() - enchantLvl * (sneaking ? 2 : 1));
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
                Sign sign = (Sign)block.getState();
                for (String str : sign.getLines()) {
                    if (CWUtil.removeColour(str).equalsIgnoreCase("&9[PARKOUR]")) {
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
                        }
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        if (item == null) {
            return;
        }

        //No durability loss
        if (item.getType().getMaxDurability() > 0) {
            item.setDurability((short)0);
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

    @EventHandler
    private void FoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player)event.getEntity();
        if (dvz.getPM().getPlayer(player).getPlayerClass().getType() == ClassType.MONSTER || dvz.getGM().getState() == GameState.DAY_ONE
                || dvz.getPM().getPlayer(player).getPlayerClass() == DvzClass.DWARF || dvz.getPM().getPlayer(player).getPlayerClass() == DvzClass.MONSTER) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setFoodLevel(20);
                }
            }.runTaskLater(dvz, 5);
        }
    }
    @EventHandler
    private void entityInteract(PlayerInteractEntityEvent event) {
        String name = "";
        if (event.getRightClicked() instanceof LivingEntity) {
            LivingEntity npc = (LivingEntity) event.getRightClicked();
            name = npc.getCustomName();
        }

        if (name == null || name.isEmpty()) {
            return;
        }
        name = CWUtil.removeColour(name.toLowerCase());

        for (DvzClass dvzClass : DvzClass.values()) {
            if (dvzClass.toString().toLowerCase().equalsIgnoreCase(name)) {
                event.setCancelled(true);
                event.getPlayer().performCommand("dvz class " + name);
            }
        }
    }

    @EventHandler
    private void interactAtEntity(PlayerInteractAtEntityEvent event) {
        String name = "";
        if (event.getRightClicked() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) event.getRightClicked();
            name = stand.getCustomName();
        }
        if (name == null || name.isEmpty()) {
            return;
        }
        name = CWUtil.removeColour(name.toLowerCase());

        for (DvzClass dvzClass : DvzClass.values()) {
            if (dvzClass.toString().toLowerCase().equalsIgnoreCase(name)) {
                event.setCancelled(true);
                event.getPlayer().performCommand("dvz class " + name);
            }
        }
    }

    @EventHandler
    private void chat(final AsyncPlayerChatEvent event) {
        //Tips based on keywords
        final String tip = dvz.getTM().getTipFromChat(event.getMessage().replaceAll("[^a-zA-Z ]", ""), dvz.getPM().getPlayer(event.getPlayer()));
        if (event.getMessage().startsWith("!") || event.getMessage().startsWith("?") && event.getMessage().length() > 3) {
            event.setCancelled(true);
            if (tip == null || tip.isEmpty()) {
                event.getPlayer().sendMessage(Util.formatMsg("&cNo answer to this question. (Remove the ! or ? from the start of your message to chat normally)"));
            }
        }
        if (tip != null && !tip.isEmpty()) {
            event.setMessage(event.getMessage() + "*");
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().sendMessage(CWUtil.integrateColor("&2&lINFO&8&l: &7" + tip));
                }
            }.runTaskLater(dvz, 5);
        }
    }


}
