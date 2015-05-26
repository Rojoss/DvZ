package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.AlchemistWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Alchemist extends DwarfClass {

    public Alchemist() {
        super();
        dvzClass = DvzClass.ALCHEMIST;
        classItem = new DvzItem(Material.POTION, 1, (byte)0, "&5&lAlchemist", 60, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&5&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        equipment.add(new DvzItem(Material.BUCKET, 1, -1, -1));
    }


    //Check for sneaking inside the pot to give player upward velocity to get out.
    @EventHandler
    private void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            return;
        }
        for (WorkShop ws : dvz.getPM().getWorkShops().values()) {
            if (!(ws instanceof AlchemistWorkshop)) {
                continue;
            }
            AlchemistWorkshop aws = (AlchemistWorkshop)ws;
            if (aws.getPot().contains(player)) {
                player.setVelocity(player.getVelocity().setY(1.3f));
            }
        }
    }

    //Check for using buckets/bottles on cauldrons.
    @EventHandler(priority = EventPriority.HIGH)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType() != Material.CAULDRON && block.getType() != Material.CHEST) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        if (!dvz.getPM().getWorkshop(player).getCuboid().contains(block.getLocation())) {
            if (block.getType() == Material.CAULDRON) {
                player.sendMessage(Util.formatMsg("&cThis is not your cauldron."));
            }
            return;
        }

        event.setCancelled(false);
        if (block.getType() == Material.CAULDRON) {
            if (event.getItem() == null || event.getItem().getType() != Material.BUCKET) {
                return;
            }
            if (block.getData() != 3) {
                return;
            }

            player.playSound(block.getLocation(), Sound.SPLASH2, 1.0f, 2.0f);
            player.getItemInHand().setType(Material.WATER_BUCKET);
            block.setData((byte)0);
        }
    }

    //Check for emptying bucket in pot.
    @EventHandler(priority = EventPriority.HIGH)
    private void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.WATER_BUCKET) {
            return;
        }

        final Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        final WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws != null && ws instanceof AlchemistWorkshop) {
            final Location loc = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
            if (((AlchemistWorkshop)ws).getPot().contains(loc)) {
                //Check if pot is filled with water aftter a little delay because of water spread.
                if (((AlchemistWorkshop)ws).isPotFilled()) {
                    player.sendMessage(Util.formatMsg("&7Pot is already filled. Add melons or sugar now."));
                } else {
                    event.setCancelled(false);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            ((AlchemistWorkshop)ws).checkPotFilled();
                        }
                    }.runTaskLater(dvz, 30);
                }
            } else {
                player.sendMessage(Util.formatMsg("&cPlace the water in your pot."));
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.MELON_BLOCK && block.getType() != Material.SUGAR_CANE_BLOCK) {
            return;
        }

        if (block.getData() != 0) {
            return;
        }
        final Material originalType = block.getType();

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.ALCHEMIST) {
            return;
        }

        if (block.getType() == Material.MELON_BLOCK) {
            event.setCancelled(false);
            block.getWorld().dropItem(block.getLocation(), Product.MELON.getItem());

            new BukkitRunnable() {
                @Override
                public void run()   {
                    if (block.getType() == Material.AIR) {
                        block.setType(originalType);
                        ParticleEffect.VILLAGER_HAPPY.display(0.7f, 0.7f, 0.7f, 0.0001f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                        block.getWorld().playSound(block.getLocation(),Sound.DIG_GRASS, 1.0f, 1.3f);
                    }
                }

            }.runTaskLater(dvz, getIntOption("melon-respawn-time"));
        } else if (block.getType() == Material.SUGAR_CANE_BLOCK) {
            event.setCancelled(true);
            Block currentBlock = block;
            List<Block> sugarcane = new ArrayList<Block>();
            sugarcane.add(currentBlock);

            while (currentBlock.getRelative(BlockFace.UP).getType() == Material.SUGAR_CANE_BLOCK) {
                currentBlock = currentBlock.getRelative(BlockFace.UP);
                sugarcane.add(currentBlock);
            }

            CWItem sugar = Product.SUGAR.getItem();
            sugar.setAmount(sugarcane.size());
            block.getWorld().dropItem(block.getLocation(), sugar);

            for (Block sugarcaneBlock : sugarcane) {
                sugarcaneBlock.setType(Material.AIR);
                sugarcaneBlock.getWorld().playEffect(sugarcaneBlock.getLocation(), Effect.STEP_SOUND, 83);
                //ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(sugarcaneBlock.getType(), sugarcaneBlock.getData()), 0.5f, 0.5f, 0.5f, 0.2f, 20, sugarcaneBlock.getLocation().add(0.5f, 0f, 0.5f));
                //sugarcaneBlock.getWorld().playSound(sugarcaneBlock.getLocation(), Sound.DIG_GRASS, 1.0f, 1.3f);
            }

            final Block floorBlock;
            currentBlock = block;
            while (currentBlock.getRelative(BlockFace.DOWN).getType() != Material.SAND && currentBlock.getRelative(BlockFace.DOWN).getType() != Material.GRASS)   {
                currentBlock = currentBlock.getRelative(BlockFace.DOWN);
            }
            floorBlock = currentBlock.getRelative(BlockFace.DOWN);

            new BukkitRunnable() {
                @Override
                public void run()   {
                    for (int i = 0; i < 3; i++) {
                        Block sugarcaneBlock = floorBlock.getRelative(0,i+1,0);
                        if (sugarcaneBlock.getType() == Material.AIR) {
                            sugarcaneBlock.setType(originalType);
                            ParticleEffect.VILLAGER_HAPPY.display(0.7f, 0.7f, 0.7f, 0.0001f, 5, sugarcaneBlock.getLocation().add(0.5f, 0.5f, 0.5f));
                            sugarcaneBlock.getWorld().playSound(sugarcaneBlock.getLocation(), Sound.DIG_GRASS, 1.0f, 1.3f);
                            return;
                        }
                    }
                    cancel();
                }
            }.runTaskTimer(dvz, getIntOption("sugarcane-respawn-time"), getIntOption("sugarcane-respawn-time"));
        }
    }


    //Check for dropping ingredients in the pot.
    @EventHandler(priority = EventPriority.HIGH)
    private void onItemDrop(PlayerDropItemEvent event) {
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
                        Cuboid potClone = aws.getPot().clone();
                        potClone.expand(Cuboid.Dir.UP, 3);
                        if (potClone.contains(loc)) {
                            ItemStack itemStack = item.getItemStack();
                            if (itemStack.getType() == Material.MELON) {
                                if (aws.getSugar() > 0) {
                                    aws.wrongIngredientAdded();
                                    ParticleEffect.VILLAGER_ANGRY.display(0.2f, 0.4f, 0.2f, 0.0001f, 5, item.getLocation());
                                } else {
                                    item.remove();
                                    aws.addMelon(itemStack.getAmount());
                                    player.playSound(item.getLocation(), Sound.SPLASH2, 0.8f, 1.0f);
                                    ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.0001f, 10, item.getLocation());
                                }
                            } else if (itemStack.getType() == Material.SUGAR) {
                                if (aws.getMelons() > 0) {
                                    aws.wrongIngredientAdded();
                                    ParticleEffect.VILLAGER_ANGRY.display(0.2f, 0.4f, 0.2f, 0.0001f, 5, item.getLocation());
                                } else {
                                    item.remove();
                                    aws.addSugar(itemStack.getAmount());
                                    player.playSound(item.getLocation(), Sound.SPLASH2, 0.8f, 1.0f);
                                    ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.0001f, 10, item.getLocation());
                                }
                            } else {
                                aws.wrongIngredientAdded();
                                ParticleEffect.VILLAGER_ANGRY.display(0.2f, 0.4f, 0.2f, 0.0001f, 5, item.getLocation());
                            }
                        }
                    }
                }
            }.runTaskLater(dvz, 10);
        }
    }
}
