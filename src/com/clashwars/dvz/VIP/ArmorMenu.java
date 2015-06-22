package com.clashwars.dvz.VIP;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.util.ItemMenu;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ArmorMenu implements Listener {

    private DvZ dvz;
    public ItemMenu menu;

    private HashMap<UUID, Integer> activeSlots = new HashMap<UUID, Integer>();

    private HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
    private List<Material> colorableItems = new ArrayList<Material>();

    public ArmorMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("armor_menu", 45, CWUtil.integrateColor("&4&lArmor Coloring"));

        menu.setSlot(new CWItem(Material.PAPER).setName("&4&lINFORMATION").setLore(new String[] {"&6In the top row you can see your color presets.", "&6And on the bottom rows the armor from your inventory.", "&6To &a&lapply &6a preset to your armor:", "&aFirst click on the armor and then on the preset!", "&6To &9&lmodify &6a preset: &9just click on it."}), 0, null);
        byte[] glassColors = new byte[] {13,5,4,1,14,6,10,11,3};
        for (int i = 0; i < 9; i++) {
            menu.setSlot(new CWItem(Material.STAINED_GLASS_PANE, 1, glassColors[i], "&4&lARMOR COLORING!", new String[] {"&9&l^^^ &9Your presets! &9&l^^^", "&a&lvvv &aYour items! &a&lvvv", "&8Hover over the paper for info!"}), i + 9, null);
        }

        colorableItems.add(Material.LEATHER_HELMET);
        colorableItems.add(Material.LEATHER_CHESTPLATE);
        colorableItems.add(Material.LEATHER_LEGGINGS);
        colorableItems.add(Material.LEATHER_BOOTS);
    }

    public void showMenu(final Player player) {
        Long t = System.currentTimeMillis();
        //Get the allowed amount of presets by permissions
        int presetCount = 0;
        for (int i = 8; i > 0; i--) {
            if (player.hasPermission("presets." + i)) {
                presetCount = i;
                break;
            }
        }
        if (presetCount < 1) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&3&l>> &bPurchase &b&lVIP &bto be able to color armor! &3&l<<"));
            return;
        }

        //Load colorable items.
        int menuIndex = 18;
        for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) {
                continue;
            }
            if (colorableItems.contains(item.getType())) {
                items.put(menuIndex, i);
                menuIndex++;
                if (items.size() >= 27) {
                    break;
                }
            }
        }

        //Load presets
        UUID uuid = player.getUniqueId();
        ArmorPresetData presetData;
        if (dvz.getPresetCfg().PRESETS.containsKey(uuid.toString())) {
            presetData = dvz.getPresetCfg().getPreset(uuid);
        } else {
            presetData = new ArmorPresetData();
            dvz.getPresetCfg().setPreset(uuid, presetData);
        }

        //Add presets if less than count and remove if more than count.
        List<Color> presets = presetData.getPresets();
        if (presets == null || presets.size() < presetCount) {
            int diff = presets == null ? presetCount : presetCount - presets.size();
            for (int i = 0; i < diff; i++) {
                presets.add(Color.BLACK);
            }
        } else if (presets.size() > presetCount) {
            int diff = presets.size() - presetCount;
            for (int i = 0; i <diff; i++) {
                presets.remove(presetCount);
            }
        }

        //Save it in case something changed.
        presetData.setPresets(presets);
        dvz.getPresetCfg().setPreset(uuid, presetData);


        //Show the menu
        player.closeInventory();
        menu.show(player);

        //Clear menu
        CWItem air = new CWItem(Material.AIR);
        for (int i = 1; i < menu.getSize(); i++) {
            if ((i >= 9 && i < 18))
                continue;
            menu.setSlot(air, i, player);
        }
        activeSlots.remove(uuid);

        final List<Color> colorPresets = presets;
        new BukkitRunnable() {
            @Override
            public void run() {
                //Load presets in menu
                int index = 1;
                for (Color preset : colorPresets) {
                    menu.setSlot(new CWItem(Material.LEATHER_HELMET).setLeatherColor(preset), index, player);
                    index++;
                }

                //Load colorable items in menu.
                for (Map.Entry<Integer, Integer> itemEntry : items.entrySet()) {
                    menu.setSlot(new CWItem(player.getInventory().getItem(itemEntry.getValue())), itemEntry.getKey(), player);
                }
            }
        }.runTaskLater(dvz, 1);
        dvz.logTimings("ArmorMenu.showMenu()", t);
    }

    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
        Long t = System.currentTimeMillis();
        if (menu == null) {
            return;
        }
        if (!event.getItemMenu().getName().equals(menu.getName())) {
            return;
        }
        if (event.getItemMenu().getID() != menu.getID()) {
            return;
        }

        Player player = (Player)event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        CWItem item = new CWItem(event.getCurrentItem());

        if (item == null) {
            return;
        }

        event.setCancelled(true);

        if (!colorableItems.contains(item.getType())) {
            return;
        }

        if (event.getRawSlot() >= menu.getSize()) {
            return;
        }

        if (event.getSlot() < 9) {
            //Clicking on presets.
            if (activeSlots.containsKey(uuid)) {
                //Check if slot is valid and if item is still valid in that slot.
                int activeSlot = activeSlots.get(uuid);
                if (activeSlot >= 18 && activeSlot <= 36) {
                    CWItem activeItem = new CWItem(event.getInventory().getItem(activeSlot));
                    if (activeItem != null && colorableItems.contains(activeItem.getType())) {
                        //Update inv and menu item.
                        menu.setSlot(activeItem.setLeatherColor(item.getLeatherColor()), activeSlot, player);
                        CWItem invItem = new CWItem(player.getInventory().getItem(items.get(activeSlot)));
                        invItem.setLeatherColor(item.getLeatherColor());
                        player.getInventory().setItem(items.get(activeSlot), invItem);

                        //Color applied! deactivate item...
                        player.getWorld().playSound(player.getLocation(), Sound.SWIM, 0.5f, 1.5f);
                        activeSlots.remove(uuid);
                        dvz.logTimings("ArmorMenu.menuClick()[click preset]", t);
                        return;
                    }
                }
            }
            //No active item so modify the preset.
            dvz.getColorMenu().showMenu(player, event.getSlot() - 1);
            dvz.logTimings("ArmorMenu.menuClick()[click preset]", t);
        } else {
            //Clicking on own items.
            if (activeSlots.containsKey(uuid) && activeSlots.get(uuid) == event.getSlot()) {
                activeSlots.remove(uuid);
                return;
            }
            dvz.logTimings("ArmorMenu.menuClick()[click own item]", t);
            activeSlots.put(player.getUniqueId(), event.getSlot());
        }
    }


}
