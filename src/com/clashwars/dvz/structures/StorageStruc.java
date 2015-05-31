package com.clashwars.dvz.structures;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.structures.data.StorageData;
import com.clashwars.dvz.structures.extra.StorageItem;
import com.clashwars.dvz.structures.internal.Structure;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class StorageStruc extends Structure {

    private StorageData data;
    private List<StorageItem> items = new ArrayList<StorageItem>();
    private ItemMenu menu;

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
        for (String name : dvz.getGameCfg().STORAGE_PRODUCTS.keySet()) {
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
                for (StorageItem storageItem : items) {
                    dvz.getGameCfg().STORAGE_PRODUCTS.put(storageItem.getName(), storageItem.getAmt());
                }
            }
        }.runTaskTimer(dvz, 600, 600);
    }

    public void reset() {
        for (StorageItem item : items) {
            item.setAmt(0);
            updateItem(item);
        }
    }


    @Override
    public void onUse(Player player) {
        menu.show(player);
    }


    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
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
                return;
            }
            StorageItem storageItem = getStorageItem(CWUtil.removeColour(item.getItemMeta().getDisplayName()));
            if (storageItem == null) {
                return;
            }

            if (storageItem.getAmt() <= 0) {
                player.sendMessage(Util.formatMsg("&cThere are no more " + storageItem.getName() + " &cavailable."));
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
            if (remainingTillLimit >= amtToChange || storageItem.getLimit() == -1) {
                cwp.productsTaken.put(storageItem.getName(), cwp.productsTaken.get(storageItem.getName()) + amtToChange);
            } else if (remainingTillLimit > 0) {
                amtToChange = remainingTillLimit;
                cwp.productsTaken.put(storageItem.getName(), storageItem.getLimit());
            } else {
                player.sendMessage(Util.formatMsg("&cYou can not take more of this item."));
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
            itemToGive.giveToPlayer(player);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 1.6f);

        } else {
            if (item.getEnchantments().size() > 0) {
                player.sendMessage(Util.formatMsg("&cCan't store enchanted items."));
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
                cwp.productsTaken.put(storageItem.getName(), Math.min(0, cwp.productsTaken.get(storageItem.getName()) - amtToAdd));
            }

            //Add item(s) to menu.
            storageItem.changeAmt(amtToAdd);
            updateItem(storageItem);

            //Take item(s) from player.
            CWUtil.removeItemsFromSlot(player.getInventory(), event.getSlot(), amtToAdd);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 1.6f);
        }
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
        items.add(new StorageItem("&bDiamond Swords", 2, Product.DIAMOND_SWORD.getItem(), DvzClass.MINER, 2));
        items.add(new StorageItem("&7Iron Swords", 3, Product.IRON_SWORD.getItem(), DvzClass.MINER, 2));
        items.add(new StorageItem("&6Gold Swords", 4, Product.GOLD_SWORD.getItem(), DvzClass.MINER, 2));
        items.add(new StorageItem("&7Stone Blocks", 6, Product.STONE.getItem(), DvzClass.MINER, -1));
        items.add(new StorageItem("&8Stone Bricks", 7, Product.STONE_BRICK.getItem(), DvzClass.MINER, -1));

        items.add(new StorageItem("&6Bows", 11, Product.BOW.getItem(), DvzClass.FLETCHER, 2));
        items.add(new StorageItem("&7Arrows", 12, Product.ARROW.getItem(), DvzClass.FLETCHER, 256));

        items.add(new StorageItem("&3Helmets", 20, Product.HELMET.getItem(), DvzClass.TAILOR, 2));
        items.add(new StorageItem("&3Chestplates", 21, Product.CHESTPLATE.getItem(), DvzClass.TAILOR, 2));
        items.add(new StorageItem("&3Leggings", 22, Product.LEGGINGS.getItem(), DvzClass.TAILOR, 2));
        items.add(new StorageItem("&3Boots", 23, Product.BOOTS.getItem(), DvzClass.TAILOR, 2));

        items.add(new StorageItem("&dHealth Potions", 29, Ability.HEAL_POTION.getAbilityClass().getCastItem(), DvzClass.ALCHEMIST, 2));
        items.add(new StorageItem("&bSpeed Potions", 30, Ability.SPEED_POTION.getAbilityClass().getCastItem(), DvzClass.ALCHEMIST, 2));

        items.add(new StorageItem("&6Bread", 38, Product.BREAD.getItem(), DvzClass.BAKER, 32));
    }



    @Override
    public String getRegion() {
        return data.getRegion();
    }
}
