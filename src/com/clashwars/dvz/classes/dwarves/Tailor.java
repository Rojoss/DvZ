package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.TailorWorkshop;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Tailor extends DwarfClass {

    public Tailor() {
        super();
        dvzClass = DvzClass.TAILOR;
        classItem = new DvzItem(Material.SHEARS, 1, (byte)0, "&3&lTailor", 40, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&3&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.SHEEP) {
            return;
        }
        Entity entity = event.getEntity();

        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player)event.getDamager();
        CWPlayer cwp = dvz.getPM().getPlayer(damager);

        if (cwp.getPlayerClass() != DvzClass.TAILOR) {
            return;
        }
        final TailorWorkshop ws = (TailorWorkshop)dvz.getPM().getWorkshop(damager);

        //Check for killing animal that belongs to the killer.
        boolean isOwnAnimal = false;
        for (CWEntity sheep : ws.getSheeps()) {
            if (sheep.entity().getUniqueId() == entity.getUniqueId()) {
                isOwnAnimal = true;
            }
        }
        if (!isOwnAnimal) {
            return;
        }

        damager.sendMessage(Util.formatMsg("&cShear your sheep instead of killing them!"));
        return;
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void shear(PlayerShearEntityEvent event) {
        final Entity entity = event.getEntity();
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (cwp.getPlayerClass() != DvzClass.TAILOR) {
            return;
        }
        final TailorWorkshop ws = (TailorWorkshop)dvz.getPM().getWorkshop(player);

        CWEntity cwe = null;
        for (CWEntity sheep : ws.getSheeps()) {
            if (sheep.entity().getUniqueId() == entity.getUniqueId()) {
                cwe = sheep;
            }
        }
        if (cwe == null) {
            return;
        }

        cwe.setSheared(true);
        entity.getWorld().dropItemNaturally(entity.getLocation(), Product.WOOL.getItem(getIntOption("wool-drop-amount")));
        player.playSound(entity.getLocation(), Sound.SHEEP_SHEAR, 1.0f, 1.0f);

        final CWEntity sheep = cwe;
        new BukkitRunnable() {
            @Override
            public void run() {
                sheep.setSheared(false);
                ParticleEffect.SNOWBALL_POOF.display(entity.getLocation().add(0, 0.5f, 0), 0.6f, 0.6f, 0.6f, 0.0001f, 30);
                entity.getWorld().playSound(entity.getLocation(), Sound.DIG_WOOL, 1.0f, 0.0f);
            }
        }.runTaskLater(dvz, CWUtil.random(getIntOption("wool-regrow-min"), getIntOption("wool-regrow-max")));
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.RED_ROSE && block.getType() != Material.YELLOW_FLOWER) {
            return;
        }

        if (block.getData() != 0) {
            return;
        }
        final Material originalType = block.getType();

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.TAILOR) {
            return;
        }

        event.setCancelled(false);
        if (block.getType() == Material.RED_ROSE) {
            block.getWorld().dropItem(block.getLocation(), Product.ROSE_DYE.getItem());
        } else {
            block.getWorld().dropItem(block.getLocation(), Product.FLOWER_DYE.getItem());
        }
        block.setType(Material.AIR);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.AIR) {
                    block.setType(originalType);
                    ParticleEffect.HAPPY_VILLAGER.display(block.getLocation().add(0.5f, 0.5f, 0.5f), 0.2f, 0.3f, 0.2f, 0.0001f, 5);
                    block.getWorld().playSound(block.getLocation(), Sound.DIG_GRASS, 1.0f, 1.3f);
                }
            }
        }.runTaskLater(dvz, getIntOption("flower-respawn-time"));
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
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.TAILOR) {
            return;
        }

        WorkShop ws = dvz.getPM().getWorkshop(player);
        if (ws == null || !(ws instanceof TailorWorkshop)) {
            return;
        }
        if (!ws.getCuboid().contains(event.getClickedBlock())) {
            return;
        }

        Inventory inv = player.getInventory();
        Location dropLoc = event.getClickedBlock().getLocation().add(0.5f, 1f, 0.5f);
        int wool = 0;
        int redDye = 0;
        int yellowDye = 0;
        int woolNeeded = getIntOption("wool-needed");
        int redDyeNeeded = getIntOption("reddye-needed");
        int yellowDyeNeeded = getIntOption("yellowdye-needed");
        //Find all wool/dyes in inventory.
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            //Increase wool/dyes amt if the current item is wool/dye.
            if (item.getType() == Product.WOOL.getItem().getType()) {
                wool += item.getAmount();
            } else if (item.getType() == Product.ROSE_DYE.getItem().getType() && item.getData().getData() == Product.ROSE_DYE.getItem().getData().getData()) {
                redDye += item.getAmount();
            } else if (item.getType() == Product.FLOWER_DYE.getItem().getType() && item.getData().getData() == Product.FLOWER_DYE.getItem().getData().getData()) {
                yellowDye += item.getAmount();
            }

            //if enough wool/dyes then craft.
            if (wool >= woolNeeded && redDye >= redDyeNeeded && yellowDye >= yellowDyeNeeded) {
                CWUtil.removeItems(inv, Product.WOOL.getItem(), woolNeeded, true);
                CWUtil.removeItems(inv, Product.ROSE_DYE.getItem(), redDyeNeeded, true, true);
                CWUtil.removeItems(inv, Product.FLOWER_DYE.getItem(), yellowDyeNeeded, true, true);

                Product[] leatherArmor  = new Product[] {Product.HELMET, Product.CHESTPLATE, Product.LEGGINGS, Product.BOOTS};
                dropLoc.getWorld().dropItem(dropLoc, CWUtil.random(leatherArmor).getItem().setLeatherColor(CWUtil.getRandomColor()));

                ParticleEffect.WITCH_MAGIC.display(event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 20);
                return;
            }
        }
        player.sendMessage(CWUtil.formatCWMsg("&cNothing to craft."));
    }

}
