package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.BakerWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Baker extends DwarfClass {

    public Baker() {
        super();
        dvzClass = DvzClass.BAKER;
        classItem = new DvzItem(Material.BREAD, 1, (byte)0, "&6&lBaker", 10, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&6&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        equipment.add(new DvzItem(Product.SEED.getItem(10), -1, -1));
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Long t = System.currentTimeMillis();
        Block block = event.getBlock();
        if (block.getType() != Material.CROPS) {
            return;
        }

        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.getPlayerClass() != DvzClass.BAKER) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your wheat! &4&l<<"));
            dvz.logTimings("Baker.blockBreak()[no workshop]", t);
            return;
        }

        BakerWorkshop ws = (BakerWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.getWheatBlocks().contains(block)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your wheat! &4&l<<"));
            dvz.logTimings("Baker.blockBreak()[other workshop]", t);
            return;
        }

        event.setCancelled(false);
        if (block.getData() == 7) {
            dvz.getPM().getPlayer(player).addClassExp(1);
            CWUtil.dropItemStack(block.getLocation(), Product.WHEAT.getItem(), dvz, player);
        }
        CWUtil.dropItemStack(block.getLocation(), Product.SEED.getItem(), dvz, player);
        dvz.logTimings("Baker.blockBreak()", t);
    }

    @EventHandler(priority = EventPriority.HIGH)
     private void blockPlace(BlockPlaceEvent event) {
        Long t = System.currentTimeMillis();
        Block block = event.getBlock();
        if (block.getType() != Material.CROPS) {
            return;
        }

        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.getPlayerClass() != DvzClass.BAKER) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your workshop! &4&l<<"));
            dvz.logTimings("Baker.blockPlace()[no workshop]", t);
            return;
        }

        BakerWorkshop ws = (BakerWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.getWheatBlocks().contains(block)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your workshop! &4&l<<"));
            dvz.logTimings("Baker.blockPlace()[other workshop]", t);
            return;
        }

        dvz.getPM().getPlayer(player).addClassExp(1);
        event.setCancelled(false);
        dvz.logTimings("Baker.blockPlace()", t);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void itemDrop(PlayerDropItemEvent event) {
        Long t = System.currentTimeMillis();
        if (event.getItemDrop().getItemStack().getType() != Material.WHEAT) {
            return;
        }
        event.getItemDrop().setMetadata("thrower", new FixedMetadataValue(dvz, event.getPlayer().getName()));
        dvz.logTimings("Baker.itemDrop()", t);
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void hopperItemTake(InventoryPickupItemEvent event) {
        Long t = System.currentTimeMillis();
        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.HOPPER) {
            return;
        }
        event.setCancelled(true);

        if (!event.getItem().hasMetadata("thrower")) {
            return;
        }
        String playerName = event.getItem().getMetadata("thrower").get(0).asString();
        Player player = dvz.getServer().getPlayer(playerName);
        if (player == null) {
            return;
        }

        WorkShop workshop = dvz.getWM().locGetWorkShop(event.getItem().getLocation());
        if (workshop == null || !(workshop instanceof BakerWorkshop)) {
            dvz.logTimings("Baker.hopperItemTake()[not in workshop]", t);
            return;
        }
        event.setCancelled(false);

        BakerWorkshop ws = (BakerWorkshop)workshop;
        if (!ws.getOwner().getName().equals(player.getName())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your grinder! &4&l<<"));
            player.getInventory().addItem(event.getItem().getItemStack());
            dvz.logTimings("Baker.hopperItemTake()[other workshop grinder]", t);
            return;
        }

        int wheatCount = event.getItem().getItemStack().getAmount();
        if (inv.getContents() != null && inv.getContents().length > 0) {
            ItemStack[] invContents = inv.getContents();
            for (ItemStack item : invContents) {
                if (item != null && item.getType() == Material.WHEAT) {
                    wheatCount += item.getAmount();
                }
            }
        }

        int flourCount = (int)Math.floor(wheatCount / getIntOption("wheat-per-flour"));
        int wheatToRemove = flourCount * getIntOption("wheat-per-flour");
        if (wheatToRemove >= event.getItem().getItemStack().getAmount()) {
            wheatToRemove -= event.getItem().getItemStack().getAmount();
            event.getItem().getItemStack().setAmount(0);
            event.getItem().remove();
            event.setCancelled(true);
        } else {
            event.getItem().getItemStack().setAmount(event.getItem().getItemStack().getAmount() - wheatToRemove);
            wheatToRemove = 0;
        }
        if (wheatToRemove > 0) {
            CWUtil.removeItems(inv, new ItemStack(Material.WHEAT), wheatToRemove);
        }

        if (flourCount > 0) {
            CWUtil.dropItemStack(ws.getHopperBlock().getRelative(BlockFace.DOWN).getLocation(), Product.FLOUR.getItem(flourCount), dvz, player);
            dvz.getPM().getPlayer(player).addClassExp(flourCount * 3);
        }
        dvz.logTimings("Baker.hopperItemTake()", t);
    }



}
