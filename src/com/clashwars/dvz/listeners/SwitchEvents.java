package com.clashwars.dvz.listeners;

import com.clashwars.cwcore.ItemMenu;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class SwitchEvents implements Listener {

    private DvZ dvz;

    public SwitchEvents(DvZ dvz) {
        this.dvz = dvz;
    }

    @EventHandler
    private void menuClick(final ItemMenu.ItemMenuClickEvent event) {
        Long t = System.currentTimeMillis();
        final ItemMenu menu = event.getItemMenu();
        final Player player = (Player) event.getWhoClicked();
        final ItemStack item = event.getCurrentItem();

        if (menu.getName().equals("switch")) {
            //Switch menu (check for clicking on classes)

            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }
            Set<DvzClass> dvzClasses = dvz.getCM().getClasses(ClassType.DWARF).keySet();
            for (final DvzClass dvzClass : dvzClasses) {
                if (dvzClass.getClassClass().getClassItem().equals(event.getCurrentItem())) {
                    player.closeInventory();
                    player.sendMessage(CWUtil.integrateColor("&7-----"));
                    player.sendMessage(Util.formatMsg("&6In a few seconds a menu GUI will appear."));
                    player.sendMessage(Util.formatMsg("&6You can then modify which items you want to keep."));
                    player.sendMessage(Util.formatMsg("&6After you did that click the &agreen button &6to switch!"));
                    player.sendMessage(CWUtil.integrateColor("&7-----"));

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            dvz.getCM().showSwitchMenu((Player) player, dvzClass);
                        }
                    }.runTaskLater(dvz, 60);
                    dvz.logTimings("SwitchEvents.menuClick()[class selected]", t);
                    return;
                }
            }

        } else if (menu.getName().contains("switch-")) {
            //Switch menu (Modify items to keep after switching)

            event.setCancelled(true);

            final CWItem empty = new CWItem(Material.AIR);
            final int rawSlot = event.getRawSlot();
            if (rawSlot < menu.getSize()) {
                //Top menu (Items to keep)

                //Cancel switching
                if (rawSlot == 0) {
                    player.closeInventory();
                    dvz.logTimings("SwitchEvents.menuClick()[cancel]", t);
                    return;
                }

                //Switch
                if (rawSlot == 8) {
                    player.sendMessage(Util.formatMsg("&6You will be switched to " + DvzClass.fromString(menu.getData())));
                    menu.setPage(10);
                    player.closeInventory();
                    dvz.getPM().getPlayer(player).switchClass(DvzClass.fromString(menu.getData()), menu);
                    dvz.logTimings("SwitchEvents.menuClick()[switch]", t);
                    return;
                }

                //Move item from top inv to player inv.
                if (rawSlot >= 9 && rawSlot <= 44) {
                    if (player.getInventory().getItem(rawSlot - 9) == null || player.getInventory().getItem(rawSlot - 9).getType() == Material.AIR) {
                        player.getInventory().setItem(rawSlot - 9, item);
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
                    boolean isCastItem = false;
                    for (Ability ability : Ability.values()) {
                        if (ability.getAbilityClass().isCastItem(item)) {
                            isCastItem = true;
                            break;
                        }
                    }
                    if (!isCastItem) {
                        player.sendMessage(Util.formatMsg("&cThis item can't be kept."));
                        dvz.logTimings("SwitchEvents.menuClick()[can't keep]", t);
                    }
                    return;
                }

                //First try put item in same spot in menu as it is in inventory else just add it to first available slot.
                if (menu.getItems().length >= event.getSlot() + 9 && (menu.getItems()[event.getSlot() + 9] == null || menu.getItems()[event.getSlot() + 9].getType() == Material.AIR)) {
                    menu.setSlot(new CWItem(item), event.getSlot() + 9, null);
                    player.getInventory().setItem(event.getSlot(), empty);
                    dvz.logTimings("SwitchEvents.menuClick()[keep item1]", t);
                    return;
                } else {
                    for (int i = 9; i < menu.getSize() - 9; i++) {
                        if (menu.getItems()[i] == null || menu.getItems()[i].getType() == Material.AIR) {
                            menu.setSlot(new CWItem(item), i, null);
                            player.getInventory().setItem(event.getSlot(), empty);
                            dvz.logTimings("SwitchEvents.menuClick()[keep item2]", t);
                            return;
                        }
                    }
                }
                player.sendMessage(Util.formatMsg("&cCan't store more items."));
            }
            dvz.logTimings("SwitchEvents.menuClick()", t);
        }
    }


    @EventHandler
    private void invClose(InventoryCloseEvent event) {
        Long t = System.currentTimeMillis();
        Player player = (Player) event.getPlayer();
        Inventory inv = event.getInventory();

        if (!dvz.getCM().switchMenus.containsKey(player.getUniqueId())) {
            return;
        }

        ItemMenu menu = dvz.getCM().switchMenus.get(player.getUniqueId());
        if (!inv.getTitle().equals(menu.getTitle()) || inv.getSize() != menu.getSize() || !inv.getHolder().equals(player)) {
            return;
        }

        if (menu.getPage() == 10) {
            menu.setPage(0);
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
        dvz.logTimings("SwitchEvents.invClose()", t);
        return;
    }

}
