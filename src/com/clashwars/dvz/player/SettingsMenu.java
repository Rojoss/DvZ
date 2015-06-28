package com.clashwars.dvz.player;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.PlayerSettings;
import com.clashwars.dvz.util.ItemMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class SettingsMenu implements Listener {

    private DvZ dvz;
    public ItemMenu menu;

    public SettingsMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("settings_menu", 18, CWUtil.integrateColor("&4&lYour Settings"));

        menu.setSlot(new CWItem(Material.PAPER).setName("&6&lAutomatic Help").setLore(new String[]{"&7Toggle automatic help messages &aon&7/&coff&7.", "&7When enabled and you ask something in chat,", "&7you might receive an answer to your question."}), 0, null);
        menu.setSlot(new CWItem(Material.PAPER).setName("&6&lWebsite Account Warning").setLore(new String[] {"&7Toggle the website login warning &aon&7/&coff&7.", "&7When disabled you will no longer get a", "&7warning if your acount isn't synced with the website."}), 1, null);
        menu.setSlot(new CWItem(Material.PAPER).setName("&6&l/stats Menu").setLore(new String[] {"&7Choose what menu you want to display with /stats&7.", "&7By default it will open the main filter menu.", "&7But you can also set the stat menu as default.", "&7When you do this, your last filter settings will be used."}), 2, null);
        menu.setSlot(new CWItem(Material.PAPER).setName("&6&lDwarf death messages").setLore(new String[] {"&7Choose what dwarf death message you want to see.", "&7By default you will see all dwarf deaths.", "&7Options are: all, none, personal, personal/assists."}), 3, null);
        menu.setSlot(new CWItem(Material.PAPER).setName("&6&lMonster death messages").setLore(new String[] {"&7Choose what monster death message you want to see.", "&7By default you will see all monster deaths.", "&7Options are: all, none, personal, personal/assists."}), 4, null);
    }


    public void showMenu(Player player) {
        player.closeInventory();
        menu.show(player);

        updateTips(player, false);
        updateEnjinWarnings(player, false);
        updateStatsMode(player, false);
        updateDwarfDeathMessages(player , false);
        updateMonsterDeathMessages(player, false);
    }


    public void updateTips(Player player, boolean toggle) {
        PlayerSettings settings = dvz.getSettingsCfg().getSettings(player.getUniqueId());
        if (toggle) {
            settings.tips = !settings.tips;
            dvz.getSettingsCfg().setSettings(player.getUniqueId(), settings);
        }

        if (settings.tips) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte)10).setName("&a&lENABLED").setLore(new String[]{"&7You will receive automatic answers", "&7when you ask questions in chat!", "&cClick to &4disable&c!"}), 9, player);
        } else {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 8).setName("&4&lDISABLED").setLore(new String[]{"&7You won't receive automatic answers", "&7when you ask questions in chat!", "&aClick to &2enable&a!"}), 9, player);
        }
    }

    public void updateEnjinWarnings(Player player, boolean toggle) {
        PlayerSettings settings = dvz.getSettingsCfg().getSettings(player.getUniqueId());
        if (toggle) {
            settings.enjinWarning = !settings.enjinWarning;
            dvz.getSettingsCfg().setSettings(player.getUniqueId(), settings);
        }

        if (settings.enjinWarning) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte)10).setName("&a&lENABLED").setLore(new String[]{"&7You will receive a warning", "&7when your account isn't synced properly!", "&cClick to &4disable&c!"}), 10, player);
        } else {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 8).setName("&4&lDISABLED").setLore(new String[]{"&7You won't receive a warning", "&7when your account isn't synced properly!", "&7Use &6/enjinprofile &7to check the status!", "&aClick to &2enable&a!"}), 10, player);
        }
    }

    public void updateStatsMode(Player player, boolean toggle) {
        PlayerSettings settings = dvz.getSettingsCfg().getSettings(player.getUniqueId());
        if (toggle) {
            settings.statsDirect = !settings.statsDirect;
            dvz.getSettingsCfg().setSettings(player.getUniqueId(), settings);
        }

        if (settings.statsDirect) {
            menu.setSlot(new CWItem(Material.NETHER_STAR).setName("&5&lSTATS DISPLAY").setLore(new String[]{"&7When you type &6/stats &7you will", "&7see the stats display menu!", "&7It will use your last used filter settings", "&aClick to switch to &2stats filter&a!"}), 11, player);
        } else {
            menu.setSlot(new CWItem(Material.HOPPER).setName("&d&lSTATS FILTER MENU").setLore(new String[]{"&7When you type &6/stats &7you will", "&7see the stats filter menu! &8(default)", "&aClick to switch to &2stats display&a!"}), 11, player);
        }
    }

    public void updateDwarfDeathMessages(Player player, boolean toggle) {
        PlayerSettings settings = dvz.getSettingsCfg().getSettings(player.getUniqueId());
        if (toggle) {
            settings.dwarfDeathMessages++;
            if (settings.dwarfDeathMessages > 3) {
                settings.dwarfDeathMessages = 0;
            }
            dvz.getSettingsCfg().setSettings(player.getUniqueId(), settings);
        }

        if (settings.dwarfDeathMessages == 0) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte)8).setName("&4&lNone").setLore(new String[]{"&7You won't see any dwarf death messages."}), 12, player);
        } else if (settings.dwarfDeathMessages == 1) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 10).setName("&a&lAll").setLore(new String[]{"&7You will see all dwarf death messages."}), 12, player);
        } else if (settings.dwarfDeathMessages == 2) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 5).setName("&a&lPersonal kills/deaths and assists").setLore(new String[]{"&7You will only see dwarf death messages",
                    "&7in which you were killed or where you got a kill.", "&7It will also show death messages of kills you assisted with."}), 12, player);
        } else if (settings.dwarfDeathMessages == 3) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 13).setName("&a&lPersonal kills/deaths").setLore(new String[]{"&7You will only see dwarf death messages",
                    "&7in which you were killed or where you got a kill."}), 12, player);
        }
    }

    public void updateMonsterDeathMessages(Player player, boolean toggle) {
        PlayerSettings settings = dvz.getSettingsCfg().getSettings(player.getUniqueId());
        if (toggle) {
            settings.monsterDeathMessages++;
            if (settings.monsterDeathMessages > 3) {
                settings.monsterDeathMessages = 0;
            }
            dvz.getSettingsCfg().setSettings(player.getUniqueId(), settings);
        }

        if (settings.monsterDeathMessages == 0) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte)8).setName("&4&lNone").setLore(new String[]{"&7You won't see any monster death messages."}), 13, player);
        } else if (settings.monsterDeathMessages == 1) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 10).setName("&a&lAll").setLore(new String[]{"&7You will see all monster death messages."}), 13, player);
        } else if (settings.monsterDeathMessages == 2) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 5).setName("&a&lPersonal kills/deaths and assists").setLore(new String[]{"&7You will only see monster death messages",
                    "&7in which you were killed or where you got a kill.", "&7It will also show death messages of kills you assisted with."}), 13, player);
        } else if (settings.monsterDeathMessages == 3) {
            menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 13).setName("&a&lPersonal kills/deaths").setLore(new String[]{"&7You will only see monster death messages",
                    "&7in which you were killed or where you got a kill."}), 13, player);
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

        int slot = event.getSlot();
        if (slot == 9) {
            updateTips(player, true);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
        } else if (slot == 10) {
            updateEnjinWarnings(player, true);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
        } else if (slot == 11) {
            updateStatsMode(player, true);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
        } else if (slot == 12) {
            updateDwarfDeathMessages(player, true);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
        } else if (slot == 13) {
            updateMonsterDeathMessages(player, true);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
        }
    }

}
