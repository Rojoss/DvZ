package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.AlchemistWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Alchemist extends DwarfClass {

    public Alchemist() {
        super();
        dvzClass = DvzClass.ALCHEMIST;
        classItem = new DvzItem(Material.POTION, 1, (byte)0, "&5&lAlchemist", 60, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&5&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
    }

    //Check for sneaking inside the pot to give player upward velocity to get out.
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        for (WorkShop ws : dvz.getPM().getWorkShops().values()) {
            if (!(ws instanceof AlchemistWorkshop)) {
                continue;
            }
            AlchemistWorkshop aws = (AlchemistWorkshop)ws;
            Location loc = player.getLocation();
            Location min = aws.getPotMin();
            Location max = aws.getPotMax();
            if (loc.getX() >= min.getX() && loc.getX() <= max.getX() && loc.getY() >= min.getY() && loc.getY() <= max.getY()+1 && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
                player.setVelocity(player.getVelocity().setY(1.3f));
            }
        }
    }

}
