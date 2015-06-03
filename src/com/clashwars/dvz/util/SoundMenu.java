package com.clashwars.dvz.util;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class SoundMenu implements Listener {

    private DvZ dvz;
    public ItemMenu menu;

    public SoundMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("sound_menu", 54, CWUtil.integrateColor("&4&lSound Test"));
    }

    public void showMenu(Player player, int page) {
        showMenu(player, page, null);
    }

    public void showMenu(Player player, int page, CWSound.SoundCat category) {
        if (category != null) {
            List<CWSound> sounds = new ArrayList<CWSound>();
            for (CWSound sound : CWSound.values()) {
                if (sound.category == category) {
                    sounds.add(sound);
                }
            }

            int pageCount = (int)Math.ceil(sounds.size() / 45);
            page = Math.min(page, pageCount);
            page = Math.max(page, 0);

            for (int i = 0; i < 9; i++) {
                menu.setSlot(new CWItem(Material.AIR), i, player);
            }

            menu.setSlot(new CWItem(Material.PAPER, 1, (byte)0, "&5&lPREV PAGE", new String[] {"&0" + page, "&0" + category.toString()}), 0, player);
            menu.setSlot(new CWItem(Material.BOOK, 1, (byte)0, "&5&lCATEGORIES", new String[] {"&0" + page, "&0" + category.toString()}), 4, player);
            menu.setSlot(new CWItem(Material.PAPER, 1, (byte)0, "&5&lNEXT PAGE", new String[] {"&0" + page, "&0" + category.toString()}), 8, player);

            for (int i = 0; i < 45; i++) {
                if (sounds.size() <= page * 45 + i) {
                    menu.setSlot(new CWItem(Material.AIR), i + 9, player);
                    continue;
                }
                CWSound sound = sounds.get(page * 45 + i);
                menu.setSlot(new CWItem(sound.icon, 1, sound.data, "&a&l" + sound.toString(), new String[] {"&6Category&8: " + sound.category.toString().toLowerCase()}), i + 9, player);
            }
        } else {
            for (int i = 0; i < 54; i++) {
                menu.setSlot(new CWItem(Material.AIR), i, player);
            }

            int index = 0;
            for (CWSound.SoundCat cat : CWSound.SoundCat.values()) {
                menu.setSlot(new CWItem(cat.icon, 1, (byte)0, "&a&l" + cat.toString()), index, player);
                index++;
            }
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
        ItemMenu soundMenu = event.getItemMenu();
        Player player = (Player)event.getWhoClicked();

        CWItem item = new CWItem(event.getCurrentItem());
        if (item == null || item.getName() == null || item.getName().isEmpty()) {
            return;
        }

        event.setCancelled(true);

        CWSound sound = CWSound.fromString(CWUtil.stripAllColor(item.getName()));
        if (sound == null) {
            CWSound.SoundCat soundCat = CWSound.SoundCat.fromString(CWUtil.stripAllColor(item.getName()));
            if (soundCat == null) {
                String action = CWUtil.stripAllColor(item.getName()).toLowerCase();
                if (action.equals("next page")) {
                    int page = Integer.parseInt(CWUtil.stripAllColor(item.getLore(0)));
                    CWSound.SoundCat cat = CWSound.SoundCat.fromString(CWUtil.stripAllColor(item.getLore(1)));
                    showMenu(player, page + 1, cat);
                } else if (action.equals("prev page")) {
                    int page = Integer.parseInt(CWUtil.stripAllColor(item.getLore(0)));
                    CWSound.SoundCat cat = CWSound.SoundCat.fromString(CWUtil.stripAllColor(item.getLore(1)));
                    showMenu(player, page - 1, cat);
                } else if (action.equals("categories")) {
                    showMenu(player, 0, null);
                }
            } else {
                showMenu(player, 0, soundCat);
            }
            return;
        } else {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    player.playSound(player.getLocation(), sound.sound, 1, 2);
                } else {
                    player.playSound(player.getLocation(), sound.sound, 1, 0);
                }
            } else {
                player.playSound(player.getLocation(), sound.sound, 1, 1);
            }
        }
    }

}
