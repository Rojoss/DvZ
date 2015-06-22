package com.clashwars.dvz.structures;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.structures.data.DisposeData;
import com.clashwars.dvz.structures.internal.Structure;
import com.clashwars.dvz.util.ItemMenu;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class DisposeStruc extends Structure {

    private DisposeData data;
    private ItemMenu menu;

    public DisposeStruc() {
        if (dvz.getStrucCfg().getDisposeData() == null) {
            dvz.getStrucCfg().setDisposeData(new DisposeData());
        }
        data = dvz.getStrucCfg().getDisposeData();
        menu = new ItemMenu("dispose", data.getGuiSize(), CWUtil.integrateColor(data.getGuiTitle()));


        //Flashing warning icon.
        final ItemMenu finalMenu = menu;
        new BukkitRunnable() {
            @Override
            public void run() {
                byte data = 14;
                if (finalMenu.getItems()[0] != null && finalMenu.getItems()[0].getData().getData() == 14) {
                    data = 1;
                }
                finalMenu.setSlot(new CWItem(Material.STAINED_CLAY, 1, data).setName("&4&lWARNING").addLore("&cAll items you add in here will be &4removed&c!")
                        .addLore("&cOnly use it if you want to clear unwanted items."), 0, null);

            }
        }.runTaskTimer(dvz, 5, 5);
    }


    @Override
    public void onUse(Player player) {
        Long t = System.currentTimeMillis();
        menu.show(player);
        menu.clear(player);
        menu.setSlot(new CWItem(Material.REDSTONE_BLOCK).setName("&4&lWARNING").addLore("&cAll items you add in here will be &4removed&c!")
                .addLore("&cOnly use it if you want to clear unwanted items."), 0, player);
        player.sendMessage(Util.formatMsg("&c&lAll items you put in this GUI will be &4&ldeleted&c&l!"));
        player.sendMessage(Util.formatMsg("&c&lThere is no way possible to get the items back!"));
        dvz.logTimings("DisposeStruc.onUse()", t);
    }


    @EventHandler
    private void menuClick(ItemMenu.ItemMenuClickEvent event) {
        Long t = System.currentTimeMillis();
        ItemMenu itemMenu = event.getItemMenu();
        if (!itemMenu.getName().equals(menu.getName())) {
            return;
        }
        if (itemMenu.getID() != menu.getID()) {
            return;
        }
        Player player = (Player)event.getWhoClicked();

        if (event.getRawSlot() == 0) {
            player.sendMessage(Util.formatMsg("&c&lAll items you put in this GUI will be &4&ldeleted&c&l!"));
            player.sendMessage(Util.formatMsg("&c&lThere is no way possible to get the items back!"));
            event.setCancelled(true);
        }
        dvz.logTimings("DisposeStruc.menuClick()", t);
    }



    @Override
    public String getRegion() {
        return data.getRegion();
    }

}
