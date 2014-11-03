package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.AlchemistWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.BUCKET) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.CAULDRON) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        if (!dvz.getPM().getWorkshop(player).isLocWithinWorkShop(block.getLocation())) {
            player.sendMessage(Util.formatMsg("&cThis is not your cauldron."));
            return;
        }

        if (block.getData() != 3) {
            event.setCancelled(true);
            return;
        }

        player.playSound(block.getLocation(), Sound.SPLASH2, 1.0f, 2.0f);
        player.getItemInHand().setType(Material.WATER_BUCKET);
        block.setData((byte)4);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.WATER_BUCKET) {
            event.setCancelled(true);
            return;
        }

        final Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            event.setCancelled(true);
            return;
        }

        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws != null && ws instanceof AlchemistWorkshop) {
            final Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
            final Location min = ((AlchemistWorkshop)ws).getPotMin();
            final Location max = ((AlchemistWorkshop)ws).getPotMax();
            if (loc.getX() >= min.getX() && loc.getX() <= max.getX() && loc.getY() >= min.getY() && loc.getY() <= max.getY() && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
                //Check if pot is filled with water.
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        //TODO: Move this stuff to workshop and keep track of filled status for particles and so it only checks it once etc...
                        Set<Block> waterBlocks = CWUtil.findBlocksInArea(min, max, new Material[]{Material.WATER, Material.STATIONARY_WATER});
                        if (waterBlocks.size() >= 18) {
                            player.sendMessage(Util.formatMsg("&6Pot filled with water."));
                            player.sendMessage(Util.formatMsg("&7You can now start adding ingredients."));
                        }
                    }
                }.runTaskLater(dvz, 20);
                return;
            } else {
                player.sendMessage(Util.formatMsg("&cPlace the water in your pot."));
                event.setCancelled(true);
                return;
            }
        }

    }

}
