package com.clashwars.dvz.VIP;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BannerMenu implements Listener {

    private DvZ dvz;
    public ItemMenu menu;

    public HashMap<UUID, BannerData> tempBanners = new HashMap<UUID, BannerData>();

    public BannerMenu(DvZ dvz) {
        this.dvz = dvz;
        menu = new ItemMenu("banner_menu", 27, CWUtil.integrateColor("&4&lBanner Editing"));

        menu.setSlot(new CWItem(Material.PAPER).setName("&4&lINFORMATION").setLore(new String[]{"&6Click on the &ddye &6to set the &dbase color&6.", "&6Click on the &awool &6to &aadd &6layers.", "&9Left click &6on a layer to set the &9color&6.", "&9Right click &6on a layer to set the &9pattern&6.", "&aShift left click &6on a layer to &ainsert &6a layer left of it.", "&cShift right click &6on a layer to &cremove &6it!", "&6In the middle right you can see the result.", "&6Make sure to &a&lSAVE &6By pressing the &agreen button&6!"}), 0, null);

        menu.setSlot(new CWItem(Material.INK_SACK, 1, (byte) 10).setName("&a&lSAVE").setLore(new String[]{"&6Save all changes made to this banner!", "&7Changes will be saved if you close the menu...", "&7But &4&lNOT &7when the server restarts!", "&7So after you're done editing &aSAVE&7!"}), 8, null);
        menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).setName("&4&lDISCARD CHANGES").setLore(new String[]{"&cThis will undo &4all changes &cyou made.", "&cIt will load the last saved banner you made!"}), 7, null);

        menu.setSlot(new CWItem(Material.WOOL, 1, (byte) 8).setName("&7&lADD LAYER").setLore(new String[]{"&6Add a new layer to this banner.", "&6You can add up to &a16 &6layers!"}), 26, null);
    }

    public void showMenu(final Player player) {
        UUID uuid = player.getUniqueId();

        player.closeInventory();
        menu.show(player);

        //Get the banner of player
        BannerData banner;
        if (tempBanners.containsKey(uuid)) {
            banner = tempBanners.get(uuid);
        } else {
            if (dvz.getBannerCfg().BANNERS.containsKey(uuid.toString())) {
                banner = dvz.getBannerCfg().getBanner(uuid);
            } else {
                banner = new BannerData();
                banner.setBaseColor(DyeColor.WHITE);
                dvz.getBannerCfg().setBanner(uuid, banner);
            }
        }

        //Create temp banner so when modifying it it doesn't get saved.
        tempBanners.put(uuid, new BannerData(banner));

        new BukkitRunnable() {
            @Override
            public void run() {
                updateMenu(player);
            }
        }.runTaskLater(dvz, 1);
    }

    private void updateMenu(Player player) {
        BannerData tempBanner = tempBanners.get(player.getUniqueId());
        if (tempBanner == null) {
            tempBanner = dvz.getBannerCfg().getBanner(player.getUniqueId());
            if (tempBanner == null) {
                player.sendMessage(Util.formatMsg("&cSomething went wrong with loading your banner data."));
                player.closeInventory();
                return;
            } else {
                tempBanners.put(player.getUniqueId(), tempBanner);
            }
        }
        if (tempBanner.getBaseColor() == null) {
            tempBanner.setBaseColor(DyeColor.WHITE);
        }

        //Result banner
        menu.setSlot(new CWItem(Material.BANNER).setBaseColor(tempBanner.getBaseColor()).setPatterns(tempBanner.getPatterns()), 4, player);

        //Base color
        menu.setSlot(new CWItem(Material.INK_SACK, 1, tempBanner.getBaseColor().getDyeData(), "&6&lBASE COLOR", new String[]{"&6This is the base color of your banner.", "&6Click to modify it!"}), 9, player);

        //Clear layers
        for (int i = 0; i < 16; i++) {
            menu.setSlot(new CWItem(Material.AIR), i + 10, player);
        }

        //Set layers
        int layer = 0;
        List<Pattern> patterns = tempBanner.getPatterns();
        for (Pattern pat : patterns) {
            if (pat != null) {
                menu.setSlot(new CWItem(Material.BANNER).setBaseColor(tempBanner.getBaseColor()).setPattern(0, tempBanner.getPattern(layer)), layer + 10, player);
            }
            layer++;
        }
    }

    @EventHandler
    private void menuClick(final ItemMenu.ItemMenuClickEvent event) {
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

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        CWItem item = new CWItem(event.getCurrentItem());

        event.setCancelled(true);

        if (event.getRawSlot() >= menu.getSize()) {
            return;
        }

        BannerData tempBanner = tempBanners.get(uuid);

        //Add layers
        if (event.getSlot() == 26) {
            if (tempBanner.getPatternCount() >= 16) {
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1.6f);
                return;
            }
            tempBanner.addPattern(new Pattern(tempBanner.getBaseColor(), PatternType.BASE));
            tempBanners.put(uuid, tempBanner);
            player.playSound(player.getLocation(), Sound.DIG_WOOL, 1, 1.6f);
            updateMenu(player);
            return;
        }

        //Modify base color
        if (event.getSlot() == 9) {
            dvz.getPatternMenu().showMenu(player, -1, true);
            return;
        }

        //Modify banner patterns
        if (item.getType() == Material.BANNER && event.getSlot() > 8) {
            //Delete layer
            if (event.isRightClick() && event.isShiftClick()) {
                tempBanner.getPatterns().remove(event.getSlot() - 10);
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1.6f);
                updateMenu(player);
                return;
            }
            //Insert layer
            if (event.isLeftClick() && event.isShiftClick()) {
                if (tempBanner.getPatternCount() >= 16) {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 1.6f);
                    return;
                }
                tempBanner.getPatterns().add(event.getSlot() - 10, new Pattern(tempBanner.getBaseColor(), PatternType.BASE));
                player.playSound(player.getLocation(), Sound.DIG_WOOL, 1, 1.6f);
                updateMenu(player);
                return;
            }

            //Modify layer pattern/color
            dvz.getPatternMenu().showMenu(player, event.getSlot() - 10, event.isLeftClick());
            return;
        }

        //Save banner
        if (event.getSlot() == 8) {
            dvz.getBannerCfg().setBanner(uuid, tempBanner);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1.6f);
            player.closeInventory();

            //Update all placed banners.
            List<Vector> bannerLocs = tempBanner.getBannerLocations();
            for (Vector loc : bannerLocs) {
                Block block = player.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                if (block.getState() instanceof Banner) {
                    Banner bannerState = (Banner)block.getState();
                    bannerState.setBaseColor(tempBanner.getBaseColor());
                    bannerState.setPatterns(tempBanner.getPatterns());
                    bannerState.update(true);
                }
            }

            for (int i = 0; i < player.getInventory().getSize() + 4; i++) {
                ItemStack invItem = player.getInventory().getItem(i);
                if (invItem != null && invItem.getType() == Material.BANNER) {
                    CWItem cwi = new CWItem(invItem).setBaseColor(tempBanner.getBaseColor()).setPatterns(tempBanner.getPatterns());
                    player.getInventory().setItem(i, cwi);
                }
            }
            return;
        }

        //Discard changes
        if (event.getSlot() == 7) {
            player.playSound(player.getLocation(), Sound.FIZZ, 0.8f, 0f);
            tempBanners.remove(uuid);
            updateMenu(player);
            return;
        }
    }

}
