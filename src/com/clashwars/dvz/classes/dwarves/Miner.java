package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.MinerWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Miner extends DwarfClass {

    private List<Material> mineableMaterials = new ArrayList<Material>(Arrays.asList(new Material[] {Material.STONE, Material.DIAMOND_ORE, Material.IRON_ORE, Material.GOLD_ORE}));

    public Miner() {
        super();
        dvzClass = DvzClass.MINER;
        classItem = new DvzItem(Material.DIAMOND_PICKAXE, 1, (byte)0, "&8&lMiner", 20, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&8&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        DvzItem pickaxe = new DvzItem(Material.DIAMOND_PICKAXE, 1, -1, -1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
        equipment.add(pickaxe);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!mineableMaterials.contains(block.getType())) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.MINER) {
            return;
        }
        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws == null || !(ws instanceof MinerWorkshop)) {
            return;
        }
        final MinerWorkshop mws = (MinerWorkshop)ws;
        if (!mws.getMineableBlocks().contains(block)) {
            return;
        }

        event.setCancelled(false);

        //Particle effect for breaking blocks and drop ores/stone.
        final Material mat = block.getType();
        if (mat == Material.STONE) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(mat, getIntOption("stone-drops")));
            ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, block.getLocation().add(0.5f,0.5f,0.5f));
        } else if (mat == Material.DIAMOND_ORE) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(mat, getIntOption("ore-drops")));
            ParticleEffect.CRIT_MAGIC.display(0.5f, 0.5f, 0.5f, 0.0001f, 20, block.getLocation().add(0.5f,0.5f,0.5f));
        } else if (mat == Material.GOLD_ORE) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(mat, getIntOption("ore-drops")));
            ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 15, block.getLocation().add(0.5f,0.5f,0.5f));
        } else if (mat == Material.IRON_ORE) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(mat, getIntOption("ore-drops")));
            ParticleEffect.CRIT.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, block.getLocation().add(0.5f,0.5f,0.5f));
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                //Get a list of all blocks that are currently air.
                List<Block> airBlocks = new ArrayList<Block>();
                List<Block> stoneblocks = new ArrayList<Block>();
                for (Block block : mws.getMineableBlocks()) {
                    if (block.getType() == Material.AIR) {
                        airBlocks.add(block);
                    } else if (block.getType() == Material.STONE) {
                        stoneblocks.add(block);
                    }
                }

                //If there are only 5 blocks mined swap the block with a stone block randomly.
                //Else miners can just not mine stone at all and the ores will always respawn at same places.
                Block block = null;
                if (airBlocks.size() > 10) {
                    block = CWUtil.random(airBlocks);
                } else {
                    block = CWUtil.random(stoneblocks);
                }

                //Always try place it on the lowest block possible as long as it's in the workshop. (this stops randomly scattered stone)
                while (block.getRelative(BlockFace.DOWN).getType() == Material.AIR && block.getLocation().getBlockY() > mws.getCuboid().getMinY()) {
                    block = block.getRelative(BlockFace.DOWN);
                }

                //Set new block but save the type if we need to swap.
                Material originalBlock = block.getType();
                block.setType(mat);

                //Swap blocks functionality
                if (originalBlock != Material.AIR) {
                    Block swapBlock = CWUtil.random(airBlocks);
                    while (swapBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && swapBlock.getLocation().getBlockY() > mws.getCuboid().getMinY()) {
                        swapBlock = block.getRelative(BlockFace.DOWN);
                    }
                    swapBlock.setType(originalBlock);
                }
            }
        }.runTaskLater(dvz, CWUtil.random(CWUtil.getInt("min-respawn-time"), getIntOption("max-respawn-time")));
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.WORKBENCH) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.MINER) {
            return;
        }

        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws == null || !(ws instanceof MinerWorkshop)) {
            return;
        }
        if (!ws.getCuboid().contains(event.getClickedBlock())) {
            return;
        }



        //Custom crafting. (first try crafting with items in hand then go through all items in inv)
        ItemStack hand = player.getItemInHand();
        Location dropLoc = event.getClickedBlock().getLocation().add(0.5f, 1f, 0.5f);
        if (hand.getAmount() > 2) {
            if (tryCraft(hand, dropLoc)) {
                CWUtil.removeItemsFromHand(player, 2);
                ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
                player.updateInventory();
                return;
            }
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (tryCraft(inv.getItem(i), dropLoc)) {
                CWUtil.removeItemsFromSlot(inv, i, 2);
                ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
                player.updateInventory();

                dvz.getPM().getPlayer(player).addClassExp(50);
                return;
            }
        }
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need at least 2 iron, gold or diamond to craft! &4&l<<"));
    }

    //Try craft a item with the given itemstack.
    private boolean tryCraft(ItemStack item, Location dropLoc) {
        if (item == null) {
            return false;
        }
        if (item.getAmount() < 2) {
            return false;
        }
        if (Product.DIAMOND.getItem().getType() == item.getType()) {
            dropLoc.getWorld().dropItem(dropLoc, Product.DIAMOND_SWORD.getItem());
            return true;
        } else if (Product.GOLD_INGOT.getItem().getType() == item.getType()) {
            dropLoc.getWorld().dropItem(dropLoc, Product.GOLD_SWORD.getItem());
            return true;
        } else if (Product.IRON_INGOT.getItem().getType() == item.getType()) {
            dropLoc.getWorld().dropItem(dropLoc, Product.IRON_SWORD.getItem());
            return true;
        }
        return false;
    }


    public List<Material> getMineableMaterials() {
        return mineableMaterials;
    }

}
