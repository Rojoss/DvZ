package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.FletcherWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Fletcher extends DwarfClass {

    public Fletcher() {
        super();
        dvzClass = DvzClass.FLETCHER;
        classItem = new DvzItem(Material.WOOD_SWORD, 1, (byte)0, "&2&lFletcher", 30, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&2&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
    }


    @EventHandler
    private void mobKill(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.PIG && event.getEntityType() != EntityType.CHICKEN) {
            return;
        }

        LivingEntity entity = event.getEntity();
        if (entity.getKiller() == null) {
            return;
        }

        CWPlayer cwp = dvz.getPM().getPlayer(entity.getKiller());
        if (cwp.getPlayerClass() != DvzClass.FLETCHER) {
            return;
        }

        final FletcherWorkshop ws = (FletcherWorkshop)dvz.getPM().getWorkshop(entity.getKiller());

        //Check for killing animal that belongs to the killer.
        boolean isOwnAnimal = false;
        for (CWEntity animal : ws.getAnimals()) {
            if (animal.entity().getUniqueId() == entity.getUniqueId()) {
                isOwnAnimal = true;
            }
        }
        if (!isOwnAnimal) {
            return;
        }

        //Pork for killing pigs.
        if (event.getEntityType() == EntityType.PIG) {
            CWItem pork = Product.RAW_PORK.getItem();
            pork.setAmount(getIntOption("pig-drop-amount"));
            entity.getWorld().dropItem(entity.getLocation(), pork);
            ParticleEffect.PORTAL.display(entity.getLocation(), 0.5f, 0.5f, 0.5f, 0.0001f, 8);
            ws.spawnAnimal(EntityType.PIG, false);
            return;
        }

        //Feathers for killing chickens.
        Location loc = entity.getLocation();
        CWItem feathers = Product.FEATHER.getItem();
        feathers.setAmount(1);
        if (loc.getBlockY() > ws.getCuboid().getMinY() + getIntOption("chicken-bonus-height")) {
            feathers.setAmount(2);
            ParticleEffect.HAPPY_VILLAGER.display(entity.getLocation(), 0.5f, 0.5f, 0.5f, 0.0001f, 8);
        } else {
            ParticleEffect.PORTAL.display(entity.getLocation(), 0.5f, 0.5f, 0.5f, 0.0001f, 8);
        }
        entity.getWorld().dropItem(entity.getLocation(), feathers);
        ws.spawnAnimal(EntityType.CHICKEN, true);
    }


    @EventHandler
    private void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.WORKBENCH) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.FLETCHER) {
            return;
        }

        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws == null || !(ws instanceof FletcherWorkshop)) {
            return;
        }
        if (!ws.getCuboid().contains(event.getClickedBlock())) {
            return;
        }

        event.setCancelled(true);
        Inventory inv = player.getInventory();
        Location dropLoc = event.getClickedBlock().getLocation().add(0.5f, 1f, 0.5f);
        int flint = 0;
        int feathers = 0;
        int flintNeeded = getIntOption("flint-needed");
        int feathersNeeded = getIntOption("feathers-needed");
        //Find all feathers/flint in inventory.
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            //Increase flint/feathers amt if the current item is flint/feather.
            if (item.getType() == Product.FLINT.getItem().getType()) {
                flint += item.getAmount();
            } else if (item.getType() == Product.FEATHER.getItem().getType()) {
                feathers += item.getAmount();
            }
            //if enough feathers and flint then craft.
            if (feathers >= feathersNeeded && flint >= flintNeeded) {
                CWUtil.removeItems(inv, Product.FLINT.getItem(), flintNeeded, true);
                CWUtil.removeItems(inv, Product.FEATHER.getItem(), feathersNeeded, true);
                //Random chance to get a bow.
                if (CWUtil.randomFloat() <= getDoubleOption("bow-product-chance")) {
                    dropLoc.getWorld().dropItem(dropLoc, Product.BOW.getItem());
                }
                //Random amount of arrows.
                CWItem arrows = Product.ARROW.getItem();
                arrows.setAmount(CWUtil.random(getIntOption("min-arrow-amount"), getIntOption("max-arrow-amount")));
                dropLoc.getWorld().dropItem(dropLoc, arrows);
                ParticleEffect.WITCH_MAGIC.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 20);
                return;
            }
        }
        player.sendMessage(CWUtil.formatCWMsg("&cNothing to craft."));
    }


    @EventHandler
    private void blockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.GRAVEL) {
            return;
        }

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.FLETCHER) {
            return;
        }

        //Give flint with random chance for breaking gravel.
        if (CWUtil.randomFloat() <= getDoubleOption("flint-chance")) {
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5f, 0.5f, 0.5f), Product.FLINT.getItem());
        }
    }
}
