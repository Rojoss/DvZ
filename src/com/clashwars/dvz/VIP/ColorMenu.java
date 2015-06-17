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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ColorMenu implements Listener {

    private DvZ dvz;
    public ItemMenu menu;

    private HashMap<UUID, Color> colors = new HashMap<UUID, Color>();
    private HashMap<UUID, Integer> presetIds = new HashMap<UUID, Integer>();

    CWItem[] armorItems = new CWItem[] {
            new CWItem(Material.LEATHER_HELMET).setName("&6PREVIEW").setLore(new String[] {"&7This is how the new preset looks!"}),
            new CWItem(Material.LEATHER_CHESTPLATE).setName("&6PREVIEW").setLore(new String[] {"&7This is how the new preset looks!"}),
            new CWItem(Material.LEATHER_LEGGINGS).setName("&6PREVIEW").setLore(new String[] {"&7This is how the new preset looks!"}),
            new CWItem(Material.LEATHER_BOOTS).setName("&6PREVIEW").setLore(new String[] {"&7This is how the new preset looks!"})
    };

    public ColorMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("color_menu", 36, CWUtil.integrateColor("&4&lColor Editing"));

        menu.setSlot(new CWItem(Material.PAPER).setName("&4&lINFORMATION").setLore(new String[] {"&6The &4red&6, &agreen &6and &9blue &6wool...", "&6can be used to modify the &4co&alo&9r.", "&6Left clicking will increase the color.", "&6Right clicking will decrease the color.", "&6On the armor you can see the new color you created.", "&6Make sure to &a&lSAVE &6By pressing the &agreen button&6!"}), 0, null);

        menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).setName("&4&lBACK").setLore(new String[] {"&cYour changes won't be saved!"}), 8, null);
        menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte)10).setName("&a&lSAVE").setLore(new String[] {"&6Save all changes made to this preset!"}), 17, null);

        menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte)0).setName("&a&lRESET").setLore(new String[] {"&6Remove color!"}), 35, null);

        menu.setSlot(new CWItem(Material.WOOL, 1, (byte)14).setName("&4&lRED 1").setLore(new String[] {"&7Change red value by 1!"}), 9, null);
        menu.setSlot(new CWItem(Material.WOOL, 5, (byte)14).setName("&4&lRED 5").setLore(new String[] {"&7Change red value by 5!"}), 10, null);
        menu.setSlot(new CWItem(Material.WOOL, 10, (byte)14).setName("&4&lRED 10").setLore(new String[] {"&7Change red value by 10!"}), 11, null);
        menu.setSlot(new CWItem(Material.WOOL, 25, (byte)14).setName("&4&lRED 25").setLore(new String[] {"&7Change red value by 25!"}), 12, null);
        menu.setSlot(new CWItem(Material.WOOL, 50, (byte)14).setName("&4&lRED 50").setLore(new String[] {"&7Change red value by 50!"}), 13, null);

        menu.setSlot(new CWItem(Material.WOOL, 1, (byte)5).setName("&a&lGREEN 1").setLore(new String[] {"&7Change green value by 1!"}), 18, null);
        menu.setSlot(new CWItem(Material.WOOL, 5, (byte)5).setName("&a&lGREEN 5").setLore(new String[] {"&7Change green value by 5!"}), 19, null);
        menu.setSlot(new CWItem(Material.WOOL, 10, (byte)5).setName("&a&lGREEN 10").setLore(new String[] {"&7Change green value by 10!"}), 20, null);
        menu.setSlot(new CWItem(Material.WOOL, 25, (byte)5).setName("&a&lGREEN 25").setLore(new String[] {"&7Change green value by 25!"}), 21, null);
        menu.setSlot(new CWItem(Material.WOOL, 50, (byte)5).setName("&a&lGREEN 50").setLore(new String[] {"&7Change green value by 50!"}), 22, null);

        menu.setSlot(new CWItem(Material.WOOL, 1, (byte)11).setName("&9&lBLUE 1").setLore(new String[] {"&7Change blue value by 1!"}), 27, null);
        menu.setSlot(new CWItem(Material.WOOL, 5, (byte)11).setName("&9&lBLUE 5").setLore(new String[] {"&7Change blue value by 5!"}), 28, null);
        menu.setSlot(new CWItem(Material.WOOL, 10, (byte)11).setName("&9&lBLUE 10").setLore(new String[] {"&7Change blue value by 10!"}), 29, null);
        menu.setSlot(new CWItem(Material.WOOL, 25, (byte)11).setName("&9&lBLUE 25").setLore(new String[] {"&7Change blue value by 25!"}), 30, null);
        menu.setSlot(new CWItem(Material.WOOL, 50, (byte)11).setName("&9&lBLUE 50").setLore(new String[] {"&7Change blue value by 50!"}), 31, null);
    }

    public void showMenu(final Player player, int presetID) {
        UUID uuid = player.getUniqueId();
        presetIds.put(uuid, presetID);

        player.closeInventory();
        menu.show(player);

        //Get preset
        ArmorPresetData presetData = dvz.getPresetCfg().getPreset(uuid);
        colors.put(uuid, presetData.getPresets().get(presetID));
        updateColor(player);
    }

    private void updateColor(Player player) {
        UUID uuid = player.getUniqueId();
        if (colors.containsKey(uuid)) {
            menu.setSlot(armorItems[0].setLeatherColor(colors.get(uuid)), 6, player);
            menu.setSlot(armorItems[1].setLeatherColor(colors.get(uuid)), 15, player);
            menu.setSlot(armorItems[2].setLeatherColor(colors.get(uuid)), 24, player);
            menu.setSlot(armorItems[3].setLeatherColor(colors.get(uuid)), 33, player);
        }
    }

    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
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

        if (event.getRawSlot() >= menu.getSize()) {
            return;
        }

        //Modify RGB values.
        if (item.getType() == Material.WOOL) {
            Color clr = colors.get(uuid);

            int amt = item.getAmount();
            if (event.isRightClick()) {
                amt *= -1;
            }

            if (item.getData().getData() == 14) {
                clr = clr.setRed(Math.min(Math.max(clr.getRed() + amt, 0), 255));
            } else if (item.getData().getData() == 5) {
                clr = clr.setGreen(Math.min(Math.max(clr.getGreen() + amt, 0), 255));
            } else if (item.getData().getData() == 11) {
                clr = clr.setBlue(Math.min(Math.max(clr.getBlue() + amt, 0), 255));
            }

            colors.put(uuid, clr);
            updateColor(player);
            return;
        }

        //Reset color
        if (item.getType() == Material.INK_SACK && item.getData().getData() == 0) {
            colors.put(uuid, Color.BLACK);
            updateColor(player);
            player.getWorld().playSound(player.getLocation(), Sound.SWIM, 0.5f, 1.5f);
            return;
        }

        //Quit menu without saving
        if (item.getType() == Material.REDSTONE_BLOCK) {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 0.8f, 1);
            dvz.getArmorMenu().showMenu(player);
            return;
        }

        //Save preset and quit menu
        if (item.getType() == Material.INK_SACK && item.getData().getData() == 10) {
            ArmorPresetData presetData = dvz.getPresetCfg().getPreset(uuid);
            List<Color> presets = presetData.getPresets();
            presets.set(presetIds.get(uuid), colors.get(uuid));
            presetData.setPresets(presets);
            dvz.getPresetCfg().setPreset(uuid, presetData);

            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.8f, 2);
            dvz.getArmorMenu().showMenu(player);
            return;
        }
    }

}
