package com.clashwars.dvz.classes.dwarves;

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

        //Particle effect for breaking blocks.
        final Material mat = block.getType();
        if (mat == Material.STONE) {
            ParticleEffect.SMOKE.display(block.getLocation().add(0.5f,0.5f,0.5f), 0.5f, 0.5f, 0.5f, 0.0001f, 10);
        } else if (mat == Material.DIAMOND_ORE) {
            ParticleEffect.MAGIC_CRIT.display(block.getLocation().add(0.5f,0.5f,0.5f), 0.5f, 0.5f, 0.5f, 0.0001f, 20);
        } else if (mat == Material.GOLD_ORE) {
            ParticleEffect.FLAME.display(block.getLocation().add(0.5f,0.5f,0.5f), 0.5f, 0.5f, 0.5f, 0.0001f, 15);
        } else if (mat == Material.IRON_ORE) {
            ParticleEffect.CRIT.display(block.getLocation().add(0.5f,0.5f,0.5f), 0.5f, 0.5f, 0.5f, 0.0001f, 10);
        }

        event.setCancelled(false);
        new BukkitRunnable() {
            @Override
            public void run() {
                //Get a list of all blocks that are currently air.
                List<Block> airBlocks = new ArrayList<Block>();
                for (Block block : mws.getMineableBlocks()) {
                    if (block.getType() == Material.AIR) {
                        airBlocks.add(block);
                    }
                }
                Block block = CWUtil.random(airBlocks);
                //Always try place it on the lowest block possible as long as it's in the workshop. (this stops randomly scattered stone)
                while (block.getRelative(BlockFace.DOWN).getType() == Material.AIR && block.getLocation().getBlockY() > mws.getCuboid().getMinY()) {
                    block = block.getRelative(BlockFace.DOWN);
                }
                block.setType(mat);
            }
        }.runTaskLater(dvz, CWUtil.random(200, 800));
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
                ParticleEffect.WITCH_MAGIC.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 20);
                return;
            }
        }

        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getSize(); i++) {
            if (tryCraft(inv.getItem(i), dropLoc)) {
                CWUtil.removeItemsFromSlot(inv, i, 2);
                ParticleEffect.WITCH_MAGIC.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 20);
                return;
            }
        }
        player.sendMessage(CWUtil.formatCWMsg("&cNothing to craft."));
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
