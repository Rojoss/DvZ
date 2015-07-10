package com.clashwars.dvz.damage.log;

import com.clashwars.cwcore.ItemMenu;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.text.SimpleDateFormat;
import java.util.*;

public class LogMenu implements Listener {

    private DvZ dvz;
    public ItemMenu menu;
    private CWItem air = new CWItem(Material.AIR);
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private HashMap<UUID, Integer> playerMenuState = new HashMap<UUID, Integer>();

    public LogMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("log_menu", 54, CWUtil.integrateColor("&4&lCombat Logs"));

        Integer[] dividers = new Integer[] {9,10,11,12,13,14,15,16,17};
        for (int dividerSlot : dividers) {
            menu.setSlot(new CWItem(Material.STAINED_GLASS_PANE, 1, (byte)15).hideTooltips().setName("&8-----").setLore(new String[] {"&7Hover over the paper", "&7for information!"}), dividerSlot, null);
        }
    }

    public void showMenu(Player player) {
        player.closeInventory();
        menu.show(player);
        showLogs(player);
    }

    public void showLogs(Player player) {
        playerMenuState.put(player.getUniqueId(), 1);
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        menu.setSlot(new CWItem(Material.PAPER).hideTooltips().setName("&6&lINfORMATION").setLore(new String[] {"&7These are your combat logs.",
                "&7All damage you have done and taken is logged here.", "&7All the heads below are your deaths.", "&7Logs can be displayed per death.", "&aClick &7on one of the heads to view the logs.",
                "&7Logs are &cNOT saved &7they should be checked during the game."}), 0, player);
        menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).hideTooltips().setName("&4&lCLOSE").setLore(new String[] {"&7Close this combat log menu."}), 8, player);

        if (cwp.damageLogs == null || cwp.damageLogs.isEmpty()) {
            for (int i = 18; i < menu.getSize(); i++) {
                menu.setSlot(new CWItem(Material.REDSTONE).hideTooltips().setName("&4&lNO LOG").setLore(new String[] {"&cYou haven't dealt or taken any damage yet!"}), i, player);
            }
            return;
        }

        menu.setSlot(air, 1, player);

        //Current log
        menu.setSlot(new CWItem(Material.SKULL_ITEM).hideTooltips().makeGlowing().setName("&a&lCurrent Life").setSkullOwner(getSkullName(cwp.getName(), cwp.getPlayerClass()))
                .setLore(new String[]{"&7Click to view the damage log", "&7of your current life!"}), 18, player);

        //Previous logs
        int menuIndex = 20;
        if (cwp.damageLogs.size() > 1) {
            for (int i = cwp.damageLogs.size()-2; i >= 0 && menuIndex < menu.getSize(); i--) {
                DamageLog dmgLog = cwp.damageLogs.get(i);
                if (dmgLog == null) {
                    continue;
                }
                menu.setSlot(new CWItem(Material.SKULL_ITEM).hideTooltips().setName("&c&l" + dmgLog.deathMsg.replace(player.getName(), "you")).setSkullOwner(getSkullName(cwp.getName(), dmgLog.deathClass))
                        .setLore(new String[]{"&7Click to view the damage log of this life!", "&7Death time&8: &c" + sdf.format(new Date(dmgLog.deathTime)), "&0" + i}), menuIndex, player);
                menuIndex++;
            }
        }
        for (; menuIndex < menu.getSize(); menuIndex++) {
            menu.setSlot(air, menuIndex, player);
        }
    }

    public void showLogMessages(Player player, int logIndex) {
        playerMenuState.put(player.getUniqueId(), 2);
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        DamageLog dmgLog = null;
        if (logIndex < cwp.damageLogs.size()) {
            dmgLog = cwp.damageLogs.get(logIndex);
        }

        if (dmgLog == null) {
            player.sendMessage(Util.formatMsg("&cNo log found for this life."));
            player.closeInventory();
            return;
        }

        if (dmgLog.getDmgMessages() == null || dmgLog.getDmgMessages().size() == 0) {
            player.sendMessage(Util.formatMsg("&cNo damage dealt/taken during this life."));
            player.closeInventory();
            return;
        }

        menu.setSlot(new CWItem(Material.PAPER).hideTooltips().setName("&6&lINfORMATION").setLore(new String[] {"&7These are your combat log messages.",
                "&7All damage you have done and taken is logged here.", "&7All the delicious cookies below show the combat messages.", "&7Hover over a cookie to see the damage taken/dealt.",
                "&7On the left you see the damage, then the damage message ", "&7and on the end you see the health change. [from>to]", "&7Each cookie shows max 15 messages."}), 0, player);
        menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).hideTooltips().setName("&4&lBACK").setLore(new String[] {"&7Go back to the main damage log menu."}), 8, player);

        if (dmgLog.deathMsg == null) {
            menu.setSlot(new CWItem(Material.SKULL_ITEM).hideTooltips().setName("&a&lCurrent Life").setSkullOwner(getSkullName(cwp.getName(), cwp.getPlayerClass())), 1, player);
        } else {
            menu.setSlot(new CWItem(Material.SKULL_ITEM).hideTooltips().setName("&c&l" + dmgLog.deathMsg.replace(player.getName(), "you")).setSkullOwner(getSkullName(cwp.getName(), dmgLog.deathClass))
                    .setLore(new String[]{"&7Death time&8: &c" + sdf.format(new Date(dmgLog.deathTime))}), 1, player);
        }

        List<String> dmgMessages = dmgLog.getDmgMessages();
        int items = (int)Math.ceil((float)dmgMessages.size() / 15);

        int added = 0;
        int menuIndex = 0;
        for (; menuIndex < items && menuIndex < menu.getSize(); menuIndex++) {
            List<String> messages = new ArrayList<String>();
            for (int i = 0; i < 15; i++) {
                if (added >= dmgMessages.size()) {
                    break;
                }
                messages.add(dmgMessages.get(added));
                added++;
            }
            menu.setSlot(new CWItem(Material.COOKIE).hideTooltips().setName("&c&lDamage Log " + (menuIndex + 1)).setLore(messages), menuIndex + 18, player);
        }
        for (; menuIndex < menu.getSize() - 18; menuIndex++) {
            menu.setSlot(air, menuIndex + 18, player);
        }
    }


    private String getSkullName(String name, DvzClass dvzClass) {
        if (dvzClass.getType() == ClassType.DWARF || dvzClass.isBaseClass()) {
            return name;
        }
        return "MHF_" + CWUtil.capitalize(dvzClass.toString().toLowerCase());
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

        Player player = (Player) event.getWhoClicked();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        UUID uuid = player.getUniqueId();
        CWItem item = new CWItem(event.getCurrentItem());

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (event.getRawSlot() > menu.getSize()) {
            return;
        }

        event.setCancelled(true);
        int slot = event.getRawSlot();

        int menuState = 1;
        if (playerMenuState.containsKey(uuid)) {
            menuState = playerMenuState.get(uuid);
        }

        if (slot == 8) {
            if (menuState == 1) {
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
            } else if (menuState == 2) {
                showLogs(player);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
            }
        } else if (menuState == 1 && slot == 18) {
            showLogMessages(player, cwp.damageLogs.size() - 1);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
        } else if (menuState == 1 && slot >= 20) {
            String lore = item.getLore(2);
            if (lore != null && CWUtil.getInt(CWUtil.stripAllColor(lore)) >= 0) {
                showLogMessages(player, CWUtil.getInt(CWUtil.stripAllColor(lore)));
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 2);
            }
        }
    }

}
