package com.clashwars.dvz.VIP;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.util.ItemMenu;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class PatternMenu implements Listener {

    private DvZ dvz;
    private ItemMenu menu;
    private ItemMenu color_menu;

    private HashMap<UUID, Integer> patternIndexes = new HashMap<UUID, Integer>();

    public PatternMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("pattern_menu", 45, CWUtil.integrateColor("&4&lLayer Pattern Editing"));
        color_menu = new ItemMenu("color_menu", 18, CWUtil.integrateColor("&4&lLayer Color Editing"));

        menu.setSlot(new CWItem(Material.PAPER).setName("&4&lINFORMATION").setLore(new String[]{"&6Click on one of the patterns to apply!"}), 0, null);
        color_menu.setSlot(new CWItem(Material.PAPER).setName("&4&lINFORMATION").setLore(new String[]{"&6Click on one of the dyes to apply!"}), 0, null);
        menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).setName("&4&lBACK").setLore(new String[]{"&cGo back to the banner editing menu!"}), 44, null);
        color_menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).setName("&4&lBACK").setLore(new String[]{"&cGo back to the banner editing menu!"}), 17, null);

        for (DyeColor clr : DyeColor.values()) {
            color_menu.setSlot(new CWItem(Material.WOOL, 1, clr.getWoolData()).setName("&a&l" + clr.toString()).setLore(new String[]{"&6Click to apply this color!"}), clr.getWoolData() + 1, null);
        }

        for (PatternType pattern : PatternType.values()) {
            menu.setSlot(new CWItem(Material.BANNER).setName("&a&l" + pattern.toString().replace("_"," ")).setLore(new String[]{"&6Click to apply this pattern!"}).setBaseColor(DyeColor.WHITE).setPattern(0, new Pattern(DyeColor.BLACK, pattern)), pattern.ordinal() + 1, null);
        }
    }

    public void showMenu(Player player, int patternIndex, boolean color) {
        player.closeInventory();

        patternIndexes.put(player.getUniqueId(), patternIndex);

        if (color) {
            color_menu.show(player);
        } else {
            menu.show(player);
        }
    }

    @EventHandler
    private void menuClick(final ItemMenu.ItemMenuClickEvent event) {
        if (menu == null) {
            return;
        }
        if (!event.getItemMenu().getName().equals(menu.getName()) && !event.getItemMenu().getName().equals(color_menu.getName())) {
            return;
        }
        if (event.getItemMenu().getID() != menu.getID() && event.getItemMenu().getID() != color_menu.getID()) {
            return;
        }

        final Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        CWItem item = new CWItem(event.getCurrentItem());

        event.setCancelled(true);

        if (event.getRawSlot() >= menu.getSize()) {
            return;
        }

        if (event.getSlot() == 0) {
            return;
        }

        BannerData tempBanner = dvz.getBannerMenu().tempBanners.get(uuid);
        int patternIndex = patternIndexes.get(uuid);

        //Pattern
        if (event.getItemMenu().getID() == menu.getID()) {
            if (event.getSlot() == 44) {
                dvz.getBannerMenu().showMenu(player);
                return;
            }
            Pattern itemPattern = item.getPattern(0);
            DyeColor color = tempBanner.getPattern(patternIndex).getColor();
            if (color == null) {
                color = DyeColor.BLACK;
            }
            tempBanner.setPattern(patternIndex, new Pattern(color, item.getPattern(0).getPattern()));
            dvz.getBannerMenu().tempBanners.put(uuid, tempBanner);
            dvz.getBannerMenu().showMenu(player);
            return;
        }

        //Color
        if (event.getItemMenu().getID() == color_menu.getID()) {
            if (event.getSlot() == 17) {
                dvz.getBannerMenu().showMenu(player);
                return;
            }

            //Edit base color
            if (patternIndex == -1) {
                tempBanner.setBaseColor(DyeColor.valueOf(CWUtil.stripAllColor(item.getName())));
            } else {
                PatternType patternType =  tempBanner.getPattern(patternIndex).getPattern();
                if (patternType == null) {
                    patternType = PatternType.BASE;
                }
                tempBanner.setPattern(patternIndex, new Pattern(DyeColor.valueOf(CWUtil.stripAllColor(item.getName())), patternType));
            }
            dvz.getBannerMenu().tempBanners.put(uuid, tempBanner);
            dvz.getBannerMenu().showMenu(player);
        }
    }
}
