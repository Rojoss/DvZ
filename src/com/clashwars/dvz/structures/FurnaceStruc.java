package com.clashwars.dvz.structures;

import com.clashwars.cwcore.ItemMenu;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.listeners.custom.GameResetEvent;
import com.clashwars.dvz.structures.data.FurnaceData;
import com.clashwars.dvz.structures.extra.FurnaceItem;
import com.clashwars.dvz.structures.internal.Structure;
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
        Long t = System.currentTimeMillis();
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
        dvz.logTimings("FurnaceStruc.onUse()", t);
    }


    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
        Long t = System.currentTimeMillis();
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

        final FurnaceItem furnaceItem = getFurnaceItem(item);
        if (furnaceItem == null) {
            dvz.logTimings("FurnaceStruc.menuClick()[Not furnace item]", t);
            return;
        }

        //Check if clicked in furnace inv or own inv.
        if (event.getRawSlot() < menu.getSize()) {
            if (event.isShiftClick()) {
                //Try take all items out of furnace.
                for (int i = 0; i < menu.getSize() - 9; i++) {
                    if (menu.getItems()[i] == null) {
                        continue;
                    }
                    int count = 0;
                    for (FurnaceItem fItem : furnaceItems) {
                        if (menu.getItems()[i].getType() == fItem.getResult().getType()) {
                            if (count < 5) {
                                player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.5f, 1.3f);
                                ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.5f, 0.5f, 0.0001f, 15, player.getLocation());
                                ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 5, player.getLocation());
                            }
                            menu.setSlot(new CWItem(Material.AIR), i, null);
                            fItem.getResult().giveToPlayer(player);
                            dvz.getPM().getPlayer(player).addClassExp(fItem.getXP());

                            if (fItem.getResult().getType() == Material.BREAD) {
                                dvz.getSM().changeLocalStatVal(player, StatType.BAKER_BREAD_BAKED, 1);
                            }
                            count++;
                        }
                    }
                }
            } else {
                //Take item out of furnace.
                if (furnaceItem.getResult().getType() == item.getType()) {
                    player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.5f, 1.3f);
                    ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.5f, 0.5f, 0.0001f, 15, player.getLocation());
                    ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 5, player.getLocation());
                    menu.setSlot(new CWItem(Material.AIR), event.getSlot(), null);
                    furnaceItem.getResult().giveToPlayer(player);
                    dvz.getPM().getPlayer(player).addClassExp(furnaceItem.getXP());

                    if (furnaceItem.getResult().getType() == Material.BREAD) {
                        dvz.getSM().changeLocalStatVal(player, StatType.BAKER_BREAD_BAKED, 1);
                    }
                } else {
                    player.sendMessage(Util.formatMsg("&cThis item isn't done yet."));
                }
            }

        } else {
            if (furnaceItem.getOriginal().getType() == item.getType()) {
                //Try look for a empty slot.
                List<Integer> availableSlots = new ArrayList<Integer>();
                for (int i = 0; i < menu.getSize() - 9; i++) {
                    if (menu.getItems()[i] == null || menu.getItems()[i].getType() == Material.AIR) {
                        availableSlots.add(i);
                    }
                }

                //if there is a empty slot add item in furnace else not.
                if (availableSlots.size() >= 0) {
                    if (event.isShiftClick()) {
                        final int itemsToAdd = Math.min(item.getAmount(), availableSlots.size());
                        for (int i = 0; i < itemsToAdd; i++) {
                            player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.3f, 2.0f);
                            ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 5, player.getLocation());
                            menu.setSlot(new CWItem(furnaceItem.getOriginal()), availableSlots.get(i), null);
                            CWUtil.removeItemsFromSlot(player.getInventory(), event.getSlot(), 1);

                            final int slot = availableSlots.get(i);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    menu.setSlot(new CWItem(furnaceItem.getResult()), slot, null);
                                }
                            }.runTaskLater(dvz, furnaceItem.getCookDuration());
                        }
                    } else {
                        player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 0.3f, 2.0f);
                        ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 5, player.getLocation());
                        menu.setSlot(new CWItem(furnaceItem.getOriginal()), availableSlots.get(0), null);
                        CWUtil.removeItemsFromSlot(player.getInventory(), event.getSlot(), 1);

                        final int slot = availableSlots.get(0);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                menu.setSlot(new CWItem(furnaceItem.getResult()), slot, null);
                            }
                        }.runTaskLater(dvz, furnaceItem.getCookDuration());
                    }
                } else {
                    player.sendMessage(Util.formatMsg("&cYour furnace is full right now."));
                }
            }
        }
        dvz.logTimings("FurnaceStruc.menuClick()", t);
    }


    @EventHandler
    private void gameReset(GameResetEvent event) {
        for (ItemMenu menu : menus.values()) {
            menu.clear(null);
        }
        menus.clear();
    }


    private void populateFurnaceItems() {
        furnaceItems.add(new FurnaceItem(Product.DIAMOND_ORE.getItem(), Product.DIAMOND.getItem(), 400, 8));
        furnaceItems.add(new FurnaceItem(Product.IRON_ORE.getItem(), Product.IRON_INGOT.getItem(), 400, 8));
        furnaceItems.add(new FurnaceItem(Product.GOLD_ORE.getItem(), Product.GOLD_INGOT.getItem(), 400, 8));
        furnaceItems.add(new FurnaceItem(Product.FLOUR.getItem(), Product.BREAD.getItem(), 120, 3));
    }

    public FurnaceItem getFurnaceItem(ItemStack item) {
        for (FurnaceItem furnaceItem : furnaceItems) {
            if (CWUtil.compareItems(item, furnaceItem.getOriginal(), true, false) || CWUtil.compareItems(item, furnaceItem.getResult(), true, false)) {
                return furnaceItem;
            }
        }
        return null;
    }


    @Override
    public String getRegion() {
        return data.getRegion();
    }
}
