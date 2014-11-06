package com.clashwars.dvz.structures;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
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

            //TODO: Check limit with player amounts.

            int amtToChange = 1;
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                amtToChange = Math.max(item.getMaxStackSize() / 2, 1);
            }
            if (amtToChange > storageItem.getAmt()) {
                amtToChange = storageItem.getAmt();
            }

            storageItem.changeAmt(-amtToChange);
            updateItem(storageItem);

            CWItem itemToGive = storageItem.getItem().clone();
            itemToGive.setAmount(amtToChange);
            itemToGive.giveToPlayer(player);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 1.6f);

        } else {
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

            int amtToAdd = 1;
            if (event.getClick() == ClickType.SHIFT_LEFT) {
                amtToAdd = item.getAmount();
            } else if (event.getClick() == ClickType.RIGHT) {
                amtToAdd = item.getAmount() >= 5 ? 5 : item.getAmount();
            } else if (event.getClick() == ClickType.SHIFT_RIGHT) {
                amtToAdd = item.getAmount() >= 16 ? 16 : item.getAmount();
            }

            storageItem.changeAmt(amtToAdd);
            updateItem(storageItem);
            CWUtil.removeItemsFromSlot(player.getInventory(), event.getSlot(), amtToAdd);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 1.6f);
        }
    }


    private void updateItem(StorageItem item) {
        int displayAmt = 1;
        if (item.getAmt() > 1) {
            displayAmt = item.getAmt() > 64 ? 64 : item.getAmt();
        }
        menu.setSlot(new CWItem(item.getItem().getType(), displayAmt, item.getItem().getData().getData(), item.getName())
                .addLore("&a&lAvailable&8: &7" + item.getAmt()).addLore("&aLimit&8: &7" + (item.getLimit() >= 0 ? item.getLimit() : "Infinite")), item.getSlot(), null);
    }

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
        items.add(new StorageItem("&7Raw Porkchops", 14, Product.RAW_PORK.getItem(), DvzClass.FLETCHER, 32));
        items.add(new StorageItem("&cCooked Porkchops", 15, Product.COOKED_PORK.getItem(), DvzClass.FLETCHER, 32));

        items.add(new StorageItem("&3Helmets", 20, Product.HELMET.getItem(), DvzClass.TAILOR, 2));
        items.add(new StorageItem("&3Chestplates", 21, Product.CHESTPLATE.getItem(), DvzClass.TAILOR, 2));
        items.add(new StorageItem("&3Leggings", 22, Product.LEGGINGS.getItem(), DvzClass.TAILOR, 2));
        items.add(new StorageItem("&3Boots", 23, Product.BOOTS.getItem(), DvzClass.TAILOR, 2));

        items.add(new StorageItem("&dHealth Potions", 29, Product.HEAL_POTION.getItem(), DvzClass.ALCHEMIST, 2));
        items.add(new StorageItem("&bSpeed Potions", 30, Product.SPEED_POTION.getItem(), DvzClass.ALCHEMIST, 2));
    }

    @Override
    public String getRegion() {
        return data.getRegion();
    }
}
