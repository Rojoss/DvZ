package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.MinerWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Miner extends DwarfClass {

    private final int oresNeeded = 2;
    private final int sticksNeeded = 1;
    private final int stoneDrops = 5;
    private final int oreDrops = 1;
    private final int minRespawnTime = 750;
    private final int maxRespawnTime = 2000;
    private final int woodRespawnTime = 600;
    private final int sticksPerLog = 2;

    private List<Material> mineableMaterials = new ArrayList<Material>(Arrays.asList(new Material[] {Material.STONE, Material.DIAMOND_ORE, Material.IRON_ORE, Material.GOLD_ORE}));

    public Miner() {
        super();
        dvzClass = DvzClass.MINER;
        classItem = new DvzItem(Material.DIAMOND_PICKAXE, 1, (byte)0, "&8&lMiner", 20, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte) 0, "&8&lWorkshop", new String[]{"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        DvzItem pickaxe = new DvzItem(Material.DIAMOND_PICKAXE, 1, -1, -1);
        pickaxe.addEnchantment(Enchantment.DIG_SPEED, 1);
        equipment.add(pickaxe);
        equipment.add(new DvzItem(Material.STONE_AXE, 1, -1, -1));
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Long t = System.currentTimeMillis();
        final Block block = event.getBlock();
        final Player player = event.getPlayer();

        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.MINER) {
                return;
            }

            CWUtil.dropItemStack(block.getLocation(), Product.MINER_STICK.getItem(sticksPerLog), dvz, player);
            dvz.getPM().getPlayer(player).addClassExp(1);
            dvz.getSM().changeLocalStatVal(player, StatType.MINER_WOOD_CHOPPED, 1);

            final Material originalType = block.getType();
            final byte originalData = block.getData();
            new BukkitRunnable() {
                @Override
                public void run()   {
                    if (block.getType() == Material.AIR) {
                        block.setType(originalType);
                        block.setData(originalData);
                        ParticleEffect.CRIT.display(0.7f, 0.7f, 0.7f, 0.0001f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
                        block.getWorld().playSound(block.getLocation(), Sound.DIG_WOOD, 1.0f, 1.3f);
                    }
                }

            }.runTaskLater(dvz, woodRespawnTime);
            dvz.logTimings("Miner.blockBreak()[wood]", t);
        }

        if (!mineableMaterials.contains(block.getType())) {
            return;
        }

        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.MINER) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Miner.blockBreak()[no workshop]", t);
            return;
        }

        final MinerWorkshop ws = (MinerWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.isBuild()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            dvz.logTimings("Miner.blockBreak()[ws not build]", t);
            return;
        }

        if (!ws.getMineableBlocks().contains(block)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cMine stone and ores in your own workshop! &4&l<<"));
            dvz.logTimings("Miner.blockBreak()[not part of workshop]", t);
            return;
        }

        event.setCancelled(false);

        //Particle effect for breaking blocks and drop ores/stone.
        final Material mat = block.getType();
        if (mat == Material.STONE) {
            CWUtil.dropItemStack(block.getLocation(), Product.STONE_BRICK.getItem(stoneDrops), dvz, player);
            ParticleEffect.SMOKE_NORMAL.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, block.getLocation().add(0.5f, 0.5f, 0.5f));
            dvz.getSM().changeLocalStatVal(player, StatType.MINER_STONE_MINED, 1);
        } else if (mat == Material.DIAMOND_ORE) {
            dvz.getPM().getPlayer(player).addClassExp(10);
            CWUtil.dropItemStack(block.getLocation(), Product.DIAMOND_ORE.getItem(oreDrops), dvz, player);
            ParticleEffect.CRIT_MAGIC.display(0.5f, 0.5f, 0.5f, 0.0001f, 20, block.getLocation().add(0.5f,0.5f,0.5f));
            dvz.getSM().changeLocalStatVal(player, StatType.MINER_DIAMONDS_MINED, 1);
        } else if (mat == Material.GOLD_ORE) {
            dvz.getPM().getPlayer(player).addClassExp(10);
            CWUtil.dropItemStack(block.getLocation(), Product.GOLD_ORE.getItem(oreDrops), dvz, player);
            ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 15, block.getLocation().add(0.5f,0.5f,0.5f));
            dvz.getSM().changeLocalStatVal(player, StatType.MINER_GOLD_MINED, 1);
        } else if (mat == Material.IRON_ORE) {
            dvz.getPM().getPlayer(player).addClassExp(10);
            CWUtil.dropItemStack(block.getLocation(), Product.IRON_ORE.getItem(oreDrops), dvz, player);
            ParticleEffect.CRIT.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, block.getLocation().add(0.5f,0.5f,0.5f));
            dvz.getSM().changeLocalStatVal(player, StatType.MINER_IRON_MINED, 1);
        }


        ws.runnables.add(new BukkitRunnable() {
            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                WorkShop wsLoc = dvz.getWM().locGetWorkShop(block.getLocation());
                if (wsLoc == null || !wsLoc.isOwner(player) || !wsLoc.isBuild()) {
                    dvz.logTimings("Miner.blockBreakRunnable()[workshop invalid]", t);
                    cancel();
                    return;
                }

                //Get a list of all blocks that are currently air/stone.
                List<Block> airBlocks = new ArrayList<Block>();
                List<Block> stoneblocks = new ArrayList<Block>();
                List<Block> mineableBlocks = ws.getMineableBlocks();
                for (Block block : mineableBlocks) {
                    if (block.getType() == Material.AIR) {
                        airBlocks.add(block);
                    } else if (block.getType() == Material.STONE) {
                        stoneblocks.add(block);
                    }
                }

                if (airBlocks.isEmpty()) {
                    dvz.logTimings("Miner.blockBreakRunnable()[no more air available]", t);
                    return;
                }

                //If there are only 10 blocks mined swap the block with a stone block randomly.
                //Else miners can just not mine stone at all and the ores will always respawn at same places.
                Block block = null;
                if (airBlocks.size() > 10 || mat == Material.STONE) {
                    //Enough blocks are mined so we we can just regenerate it in a open space.
                    block = CWUtil.random(airBlocks);

                    spawnBlockLowest(mat, block, ws, airBlocks);
                } else {
                    block = CWUtil.random(stoneblocks);
                    Material swapMat = block.getType();
                    block.setType(mat);

                    block = CWUtil.random(airBlocks);
                    spawnBlockLowest(swapMat, block, ws, airBlocks);
                }
                dvz.logTimings("Miner.blockBreakRunnable()", t);
            }
        }.runTaskLater(dvz, CWUtil.random(minRespawnTime, maxRespawnTime)));
        dvz.logTimings("Miner.blockBreak()", t);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void interact(DelayedPlayerInteractEvent event) {
        Long t = System.currentTimeMillis();
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

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            return;
        }

        MinerWorkshop ws = (MinerWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.isBuild()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            return;
        }

        if (!ws.getCuboid().contains(event.getClickedBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cClick on the workbench in your own workshop to craft! &4&l<<"));
            return;
        }

        Inventory inv = player.getInventory();
        Location dropLoc = event.getClickedBlock().getLocation().add(0.5f, 1f, 0.5f);

        int iron = 0;
        int gold = 0;
        int diamond = 0;
        int sticks = 0;
        boolean craft = false;
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            if (item.getType() == Product.DIAMOND.getItem().getType()) {
                diamond += item.getAmount();
            } else if (item.getType() == Product.GOLD_INGOT.getItem().getType()) {
                gold += item.getAmount();
            } else if (item.getType() == Product.IRON_INGOT.getItem().getType()) {
                iron += item.getAmount();
            } else if (item.getType() == Product.MINER_STICK.getItem().getType()) {
                sticks += item.getAmount();
            }

            if (sticks >= sticksNeeded) {
                if (iron >= oresNeeded) {
                    CWUtil.removeItems(inv, Product.MINER_STICK.getItem(), sticksNeeded, false);
                    CWUtil.removeItems(inv, Product.IRON_INGOT.getItem(), oresNeeded, false);
                    CWUtil.dropItemStack(dropLoc, Product.GREATSWORD.getItem(), dvz, player);
                    craft = true;
                } else if (gold >= oresNeeded) {
                    CWUtil.removeItems(inv, Product.MINER_STICK.getItem(), sticksNeeded, false);
                    CWUtil.removeItems(inv, Product.GOLD_INGOT.getItem(), oresNeeded, false);
                    CWUtil.dropItemStack(dropLoc, Product.FIERY_FLAIL.getItem(), dvz, player);
                    craft = true;
                } else if (diamond >= oresNeeded) {
                    CWUtil.removeItems(inv, Product.MINER_STICK.getItem(), sticksNeeded, false);
                    CWUtil.removeItems(inv, Product.DIAMOND.getItem(), oresNeeded, false);
                    CWUtil.dropItemStack(dropLoc, Product.BATTLEAXE.getItem(), dvz, player);
                    craft = true;
                }
            }
            if (craft) {
                break;
            }
        }
        if (craft) {
            ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
            player.updateInventory();

            dvz.getSM().changeLocalStatVal(player, StatType.MINER_WEAPONS_CRAFTED, 1);

            dvz.getPM().getPlayer(player).addClassExp(50);
            // + 5 per ore mined
            // + 5 per ore smelted
            // = 80 per craft
            dvz.logTimings("Miner.interact()[craft]", t);
            return;
        } else {
            if (sticks < sticksNeeded) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (sticksNeeded - sticks) + " more STICKS to craft weapons! &4&l<<"));
            } else {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need at least " + oresNeeded + " iron, gold or diamond to craft! &4&l<<"));
            }
        }
        dvz.logTimings("Miner.interact()", t);
    }

    //This will spawn a block with the given material at the block location.
    //It will begin at the bottom of the workshop and then check all the blocks above for air.
    //If no blocks are found it will just spawn it at a random place.
    private void spawnBlockLowest(Material mat, Block block, MinerWorkshop ws, List<Block> airBlocks) {
        Long t = System.currentTimeMillis();
        int maxY = block.getY();
        block = block.getWorld().getBlockAt(block.getX(), ws.getCuboid().getMinY(), block.getZ());
        if (block.getType() != Material.AIR) {
            while (block.getRelative(BlockFace.UP).getType() != Material.AIR && block.getY() <= maxY) {
                block = block.getRelative(BlockFace.UP);
            }
            if (block.getY() <= maxY) {
                block = block.getRelative(BlockFace.UP);
            }
        }
        if (block.getType() != Material.AIR) {
            block = CWUtil.random(airBlocks);
        }
        block.setType(mat);
        dvz.logTimings("Miner.spawnBlockLowest()", t);
    }


    public List<Material> getMineableMaterials() {
        return mineableMaterials;
    }

}
