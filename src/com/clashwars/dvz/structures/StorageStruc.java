package com.clashwars.dvz.structures;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.ExpUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.events.custom.GameResetEvent;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.structures.data.StorageData;
import com.clashwars.dvz.structures.extra.StorageItem;
import com.clashwars.dvz.structures.internal.Structure;
import com.clashwars.dvz.structures.internal.StructureType;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class StorageStruc extends Structure {

    private StorageData data;
    private List<StorageItem> items = new ArrayList<StorageItem>();
    private ItemMenu menu;

    public Map<UUID, BukkitTask> expDrainRunnables = new HashMap<UUID, BukkitTask>();

    public StorageStruc() {
        if (dvz.getStrucCfg().getStorageData() == null) {
            dvz.getStrucCfg().setStorageData(new StorageData());
        }
        data = dvz.getStrucCfg().getStorageData();
        menu = new ItemMenu("storage", data.getGuiSize(), CWUtil.integrateColor(data.getGuiTitle()));

        populateStorageItems();
        for (StorageItem item : items) {
            updateItem(item);
        }
        menu.setSlot(new CWItem(Material.DIAMOND_PICKAXE).setName("&8&lMiner Items"), 0, null);
        menu.setSlot(new CWItem(Material.BOW).setName("&2&lFletcher Items"), 9, null);
        menu.setSlot(new CWItem(Material.SHEARS).setName("&3&lTailor Items"), 18, null);
        menu.setSlot(new CWItem(Material.POTION).setName("&5&lAlchemist Items"), 27, null);
        menu.setSlot(new CWItem(Material.WHEAT).setName("&6&lBaker Items"), 36, null);

        //Load in items from config
        Set<String> storageNames = dvz.getGameCfg().STORAGE_PRODUCTS.keySet();
        for (String name : storageNames) {
            StorageItem storageItem = getStorageItem(name);
            if (storageItem != null) {
                storageItem.setAmt(dvz.getGameCfg().STORAGE_PRODUCTS.get(name));
                updateItem(storageItem);
            }
        }

        //Save amount of products every 30 seconds.
        new BukkitRunnable() {
            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                for (StorageItem storageItem : items) {
                    dvz.getGameCfg().STORAGE_PRODUCTS.put(storageItem.getName(), storageItem.getAmt());
                }
                dvz.getGameCfg().save();
                dvz.logTimings("StorageStruc.StorageStruc[saving items]", t);
            }
        }.runTaskTimer(dvz, 600, 600);
    }


    @Override
    public void onUse(final Player player) {
        Long t = System.currentTimeMillis();
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.STAINED_GLASS) {
            //Xp draining.
            if (!expDrainRunnables.containsKey(player.getUniqueId())) {
                expDrainRunnables.put(player.getUniqueId(), new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player == null || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.STAINED_GLASS) {
                            expDrainRunnables.remove(player.getUniqueId());
                            cancel();
                            return;
                        }

                        ExpUtil expUtil = new ExpUtil(player);
                        if (expUtil.getCurrentExp() <= 0) {
                            CWUtil.sendActionBar(player, CWUtil.integrateColor("&2&l>> &aNo more xp to drain! &2&l<<"));
                            expDrainRunnables.remove(player.getUniqueId());
                            cancel();
                            return;
                        }

                        expUtil.changeExp(-5);
                        StorageItem xpStorage = getStorageItem("&a&lExperience");
                        xpStorage.changeAmt(5);
                        updateItem(xpStorage);

                        ParticleEffect.ENCHANTMENT_TABLE.display(0.2f, 0.5f, 0.2f, 0, 2, player.getLocation().add(0, 0.5f, 0));
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.2f, 2);
                    }
                }.runTaskTimer(dvz, 0, 2));
            }
        } else {
            menu.show(player);
        }
        dvz.logTimings("StorageStruc.onUse()", t);
    }

    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
        Long t = System.currentTimeMillis();
        if (menu == null) {
            return;
        }

        ItemMenu itemMenu = event.getItemMenu();
        if (!itemMenu.getName().equals(menu.getName())) {
            return;
        }
        if (itemMenu.getID() != menu.getID()) {
            return;
        }
        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        Player player = (Player)event.getWhoClicked();
        if (event.getRawSlot() < menu.getSize()) {
            //Top inventory (Storage)
            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
                dvz.logTimings("StorageStruc.menuclick()[not storage item]", t);
                return;
            }
            StorageItem storageItem = getStorageItem(CWUtil.removeColour(item.getItemMeta().getDisplayName()));
            if (storageItem == null) {
                dvz.logTimings("StorageStruc.menuclick()[storageitem null]", t);
                return;
            }

            if (storageItem.getAmt() <= 0) {
                player.sendMessage(Util.formatMsg("&cThere are no more " + storageItem.getName() + " &cavailable."));
                dvz.logTimings("StorageStruc.menuclick()[out of stock]", t);
                return;
            }

            //Get the amount of items to transfer depending on click type.
            int amtToChange = 1;
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                amtToChange = Math.max(item.getMaxStackSize() / 2, 1);
            }
            if (amtToChange > storageItem.getAmt()) {
                amtToChange = storageItem.getAmt();
            }

            //Check item limits with products taken by player.
            CWPlayer cwp = dvz.getPM().getPlayer(player);
            if (!cwp.productsTaken.containsKey(storageItem.getName())) {
                cwp.productsTaken.put(storageItem.getName(), 0);
            }
            int remainingTillLimit = storageItem.getLimit() - cwp.productsTaken.get(storageItem.getName());
            if (storageItem.getItem().getType() == Material.EXP_BOTTLE) {
                remainingTillLimit -= dvz.getSM().getLocalStatVal(player, StatType.DWARF_XP_EARNED);
            }
            if (remainingTillLimit >= amtToChange || storageItem.getLimit() == -1) {
                cwp.productsTaken.put(storageItem.getName(), cwp.productsTaken.get(storageItem.getName()) + amtToChange);
            } else if (remainingTillLimit > 0) {
                amtToChange = remainingTillLimit;
                cwp.productsTaken.put(storageItem.getName(), storageItem.getLimit());
            } else {
                player.sendMessage(Util.formatMsg("&cYou can not take more of this item."));
                dvz.logTimings("StorageStruc.menuclick()[limit reached]", t);
                return;
            }

            //Take out item(s) from menu.
            storageItem.changeAmt(-amtToChange);
            updateItem(storageItem);

            //Give item(s) to player
            CWItem itemToGive = storageItem.getItem().clone();
            itemToGive.setAmount(amtToChange);
            if (itemToGive.getType() == Material.LEATHER_HELMET || itemToGive.getType() == Material.LEATHER_CHESTPLATE
                    || itemToGive.getType() == Material.LEATHER_LEGGINGS || itemToGive.getType() == Material.LEATHER_BOOTS) {
                itemToGive.setLeatherColor(cwp.getColor());
            }
            if (itemToGive.getType() == Material.EXP_BOTTLE) {
                new ExpUtil(player).changeExp(itemToGive.getAmount());
            } else {
                itemToGive.giveToPlayer(player);
            }
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 1.6f);

        } else {
            if (item.getEnchantments().size() > 0) {
                player.sendMessage(Util.formatMsg("&cCan't store enchanted items."));
                dvz.logTimings("StorageStruc.menuclick()[enchanted]", t);
                return;
            }

            //Make sure a storage item is clicked.
            StorageItem storageItem = null;
            for (StorageItem si : items) {
                if (si.getItem().getType() == item.getType() && si.getItem().getData().getData() == item.getData().getData()) {
                    storageItem = si;
                    break;
                }
            }
            if (storageItem == null) {
                player.sendMessage(Util.formatMsg("&cThis item can't be stored."));
                dvz.logTimings("StorageStruc.menuclick()[not storable]", t);
                return;
            }

            //Get the amount to transfer depending on click type.
            int amtToAdd = 1;
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                amtToAdd = item.getAmount();
            } else if (event.getClick() == ClickType.RIGHT) {
                amtToAdd = item.getAmount() >= 5 ? 5 : item.getAmount();
            } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                amtToAdd = item.getAmount() >= 16 ? 16 : item.getAmount();
            }

            //Players can also add items back by accident so decrease the amount they took.
            CWPlayer cwp = dvz.getPM().getPlayer(player);
            if (cwp.productsTaken.containsKey(storageItem.getName())) {
                cwp.productsTaken.put(storageItem.getName(), Math.max(0, cwp.productsTaken.get(storageItem.getName()) - amtToAdd));
            }

            //Add item(s) to menu.
            storageItem.changeAmt(amtToAdd);
            updateItem(storageItem);

            //Take item(s) from player.
            CWUtil.removeItemsFromSlot(player.getInventory(), event.getSlot(), amtToAdd);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 1.6f);
        }
        dvz.logTimings("StorageStruc.menuclick()", t);
    }

    @EventHandler
    private void gameReset(GameResetEvent event) {
        for (StorageItem item : items) {
            item.setAmt(0);
            updateItem(item);
        }
        dvz.getGameCfg().STORAGE_PRODUCTS.clear();
    }



    //Update a item in the menu with the new amount.
    private void updateItem(StorageItem item) {
        int displayAmt = 1;
        if (item.getAmt() > 1) {
            displayAmt = item.getAmt() > 64 ? 64 : item.getAmt();
        }
        menu.setSlot(new CWItem(item.getItem().getType(), displayAmt, item.getItem().getData().getData(), item.getName())
                .addLore("&a&lAvailable&8: &7" + item.getAmt()).addLore("&aLimit&8: &7" + (item.getLimit() >= 0 ? item.getLimit() : "Infinite")), item.getSlot(), null);
    }


    //Get a StorageItem by name.
    private StorageItem getStorageItem(String name) {
        for (StorageItem item : items) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }


    private void populateStorageItems() {
        items.add(new StorageItem("&bBattle axes", 2, Product.BATTLEAXE.getItem(), DvzClass.MINER, 1));
        items.add(new StorageItem("&7Greatswords", 3, Product.GREATSWORD.getItem(), DvzClass.MINER, 1));
        items.add(new StorageItem("&6Fiery flails", 4, Product.FIERY_FLAIL.getItem(), DvzClass.MINER, 1));
        items.add(new StorageItem("&8Reinforced Bricks", 6, Product.STONE_BRICK.getItem(), DvzClass.MINER, -1));

        items.add(new StorageItem("&6Bows", 11, Product.BOW.getItem(), DvzClass.FLETCHER, 1));
        items.add(new StorageItem("&7Arrows", 12, Product.ARROW.getItem(), DvzClass.FLETCHER, 256));

        items.add(new StorageItem("&3Helmets", 20, Product.HELMET.getItem(), DvzClass.TAILOR, 1));
        items.add(new StorageItem("&3Tunics", 21, Product.CHESTPLATE.getItem(), DvzClass.TAILOR, 1));
        items.add(new StorageItem("&3Greaves", 22, Product.LEGGINGS.getItem(), DvzClass.TAILOR, 1));
        items.add(new StorageItem("&3Boots", 23, Product.BOOTS.getItem(), DvzClass.TAILOR, 1));

        items.add(new StorageItem("&dHealth Potions", 29, Ability.HEAL_POTION.getAbilityClass().getCastItem(), DvzClass.ALCHEMIST, 1));
        items.add(new StorageItem("&bSpeed Potions", 30, Ability.SPEED_POTION.getAbilityClass().getCastItem(), DvzClass.ALCHEMIST, 1));

        items.add(new StorageItem("&6Bread", 38, Product.BREAD.getItem(), DvzClass.BAKER, 32));

        items.add(new StorageItem("&a&lExperience", 44, Product.XP.getItem(), DvzClass.BASE, 1900));
    }



    @Override
    public String getRegion() {
        return data.getRegion();
    }
}
