package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.AlchemistWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

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

    //Check for using buckets/bottles on cauldrons.
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

    //Check for emptying bucket in pot.
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

        final WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws != null && ws instanceof AlchemistWorkshop) {
            if (((AlchemistWorkshop) ws).isPotFilled()) {
                player.sendMessage(Util.formatMsg("&7Pot is already filled. Add the ingredients now."));
            } else {
                final Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
                final Location min = ((AlchemistWorkshop)ws).getPotMin();
                final Location max = ((AlchemistWorkshop)ws).getPotMax();
                if (loc.getX() >= min.getX() && loc.getX() <= max.getX() && loc.getY() >= min.getY() && loc.getY() <= max.getY() && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
                    //Check if pot is filled with water aftter a little delay because of water spread.
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ((AlchemistWorkshop)ws).checkPotFilled();
                        }
                    }.runTaskLater(dvz, 30);
                    return;
                } else {
                    player.sendMessage(Util.formatMsg("&cPlace the water in your pot."));
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    //Block filling up buckets normally
    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(true);
    }

    //Check for dropping ingredients in the pot.
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        final Item item = event.getItemDrop();

        final Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        final WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws != null && ws instanceof AlchemistWorkshop) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (((AlchemistWorkshop) ws).isPotFilled()) {
                        AlchemistWorkshop aws = (AlchemistWorkshop)ws;
                        Location loc = item.getLocation();
                        Location min = aws.getPotMin();
                        Location max = aws.getPotMax();
                        if (loc.getX() >= min.getX() && loc.getX() <= max.getX() && loc.getY() >= min.getY() && loc.getY() <= max.getY()+2 && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
                            ItemStack itemStack = item.getItemStack();
                            if (itemStack.getType() == Material.MELON) {
                                if (aws.getSugar() > 0) {
                                    aws.wrongIngredientAdded();
                                    ParticleEffect.ANGRY_VILLAGER.display(item.getLocation(), 0.2f, 0.4f, 0.2f, 0.0001f, 5);
                                } else {
                                    item.remove();
                                    aws.addMelon(itemStack.getAmount());
                                    player.playSound(item.getLocation(), Sound.SPLASH2, 0.8f, 1.0f);
                                    ParticleEffect.WITCH_MAGIC.display(item.getLocation(), 0.2f, 0.4f, 0.2f, 0.0001f, 10);
                                }
                            } else if (itemStack.getType() == Material.SUGAR) {
                                if (aws.getMelons() > 0) {
                                    aws.wrongIngredientAdded();
                                    ParticleEffect.ANGRY_VILLAGER.display(item.getLocation(), 0.2f, 0.4f, 0.2f, 0.0001f, 5);
                                } else {
                                    item.remove();
                                    aws.addSugar(itemStack.getAmount());
                                    player.playSound(item.getLocation(), Sound.SPLASH2, 0.8f, 1.0f);
                                    ParticleEffect.WITCH_MAGIC.display(item.getLocation(), 0.2f, 0.4f, 0.2f, 0.0001f, 10);
                                }
                            } else {
                                aws.wrongIngredientAdded();
                                ParticleEffect.ANGRY_VILLAGER.display(item.getLocation(), 0.2f, 0.4f, 0.2f, 0.0001f, 5);
                            }
                        }
                    }
                }
            }.runTaskLater(dvz, 10);
        }
    }
}
