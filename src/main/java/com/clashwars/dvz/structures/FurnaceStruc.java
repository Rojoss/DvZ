package com.clashwars.dvz.structures;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.structures.data.FurnaceData;
import com.clashwars.dvz.structures.extra.FurnaceItem;
import com.clashwars.dvz.structures.internal.Structure;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FurnaceStruc extends Structure {

    private FurnaceData data;
    private List<FurnaceItem> furnaceItems = new ArrayList<FurnaceItem>();
    private Map<UUID, ItemMenu> menus = new HashMap<UUID, ItemMenu>();

    public FurnaceStruc() {
        if (dvz.getStrucCfg().getFurnaceData() == null) {
            dvz.getStrucCfg().setFurnaceData(new FurnaceData());
        }
        data = dvz.getStrucCfg().getFurnaceData();
        populateFurnaceItems();
    }


    @Override
    public void onUse(Player player) {
        UUID uuid = player.getUniqueId();

        //Get or create furnace menu
        ItemMenu menu = null;
        if (!menus.containsKey(uuid)) {
            menu = new ItemMenu("furnace-" + uuid.toString(), data.getGuiSize(), CWUtil.integrateColor(data.getGuiTitle()));
            menus.put(uuid, menu);
            for (int i = menu.getSize() - 9; i < menu.getSize(); i++) {
                menu.setSlot(new CWItem(Material.LAVA_BUCKET).setName("&6Fuel"), i, null);
            }
        } else {
            menu = menus.get(uuid);
        }

        //Show the furnace menu/gui
        menu.show(player);
    }


    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        final ItemMenu menu = menus.get(uuid);
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
        if (item == null) {
            return;
        }

        final FurnaceItem furnaceItem = getFurnaceItem(item.getType());
        if (furnaceItem == null) {
            return;
        }

        //Check if clicked in furnace inv or own inv.
        if (event.getRawSlot() < menu.getSize()) {
            if (furnaceItem.getResult().getType() == item.getType()) {
                //Take item out of furnace.
                player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.5f, 1.3f);
                ParticleEffect.SMOKE.display(player.getLocation(), 0.5f, 0.5f, 0.5f, 0.0001f, 15);
                ParticleEffect.FLAME.display(player.getLocation(), 0.5f, 0.5f, 0.5f, 0.0001f, 5);
                menu.setSlot(new CWItem(Material.AIR), event.getSlot(), null);
                furnaceItem.getResult().giveToPlayer(player);
            } else {
                player.sendMessage(Util.formatMsg("&cThis item isn't done yet."));
            }
        } else {
            if (furnaceItem.getOriginal().getType() == item.getType()) {
                //Try look for a empty slot.
                int availableSlot = -1;
                for (int i = 0; i < menu.getSize() - 9; i++) {
                    if (menu.getItems()[i] == null || menu.getItems()[i].getType() == Material.AIR) {
                        availableSlot = i;
                        break;
                    }
                }

                //if there is a empty slot add item in furnace else not.
                if (availableSlot >= 0) {
                    player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.3f, 2.0f);
                    ParticleEffect.FLAME.display(player.getLocation(), 0.5f, 0.5f, 0.5f, 0.0001f, 5);
                    menu.setSlot(new CWItem(furnaceItem.getOriginal()), availableSlot, null);
                    CWUtil.removeItemsFromSlot(player.getInventory(), event.getSlot(), 1);

                    final int slot = availableSlot;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            menu.setSlot(new CWItem(furnaceItem.getResult()), slot, null);
                        }
                    }.runTaskLater(dvz, furnaceItem.getCookDuration());

                } else {
                    player.sendMessage(Util.formatMsg("&cYour furnace is full right now."));
                }
            }
        }
    }



    private void populateFurnaceItems() {
        furnaceItems.add(new FurnaceItem(new CWItem(Material.PORK), new CWItem(Material.GRILLED_PORK), 200));
        furnaceItems.add(new FurnaceItem(new CWItem(Material.DIAMOND_ORE), new CWItem(Material.DIAMOND), 400));
        furnaceItems.add(new FurnaceItem(new CWItem(Material.IRON_ORE), new CWItem(Material.IRON_INGOT), 400));
        furnaceItems.add(new FurnaceItem(new CWItem(Material.GOLD_ORE), new CWItem(Material.GOLD_INGOT), 400));
    }

    public FurnaceItem getFurnaceItem(Material type) {
        for (FurnaceItem item : furnaceItems) {
            if (item.getOriginal().getType() == type || item.getResult().getType() == type) {
                return item;
            }
        }
        return null;
    }


    @Override
    public String getRegion() {
        return data.getRegion();
    }
}
