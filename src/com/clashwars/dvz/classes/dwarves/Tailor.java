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
        equipment.add(new DvzItem(Material.SHEARS, 1, -1, -1));
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
                ParticleEffect.SNOWBALL.display(0.6f, 0.6f, 0.6f, 0.0001f, 30, entity.getLocation().add(0, 0.5f, 0));
                entity.getWorld().playSound(entity.getLocation(), Sound.DIG_WOOL, 1.0f, 0.0f);
            }
        }.runTaskLater(dvz, CWUtil.random(getIntOption("wool-regrow-min"), getIntOption("wool-regrow-max")));
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();
        if (block.getType() != Material.RED_ROSE) {
            return;
        }

        if (block.getData() != 3 && block.getData() != 1) {
            return;
        }
        final Material originalType = block.getType();
        final byte originalData = block.getData();

        Player player = event.getPlayer();
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.TAILOR) {
            return;
        }

        event.setCancelled(false);
        if (block.getData() == 3) {
            block.getWorld().dropItem(block.getLocation(), Product.DYE_1.getItem());
        } else if (block.getData() == 1) {
            block.getWorld().dropItem(block.getLocation(), Product.DYE_2.getItem());
        }
        block.setType(Material.AIR);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.AIR) {
                    block.setType(originalType);
                    block.setData(originalData);
                    ParticleEffect.VILLAGER_HAPPY.display(0.2f, 0.3f, 0.2f, 0.0001f, 5, block.getLocation().add(0.5f, 0.5f, 0.5f));
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
        int dye1Needed = getIntOption("reddye-needed");
        int dye2Needed = getIntOption("yellowdye-needed");
        //Find all wool/dyes in inventory.
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            //Increase wool/dyes amt if the current item is wool/dye.
            if (item.getType() == Product.WOOL.getItem().getType()) {
                wool += item.getAmount();
            } else if (item.getType() == Product.DYE_1.getItem().getType() && item.getData().getData() == Product.DYE_1.getItem().getData().getData()) {
                redDye += item.getAmount();
            } else if (item.getType() == Product.DYE_2.getItem().getType() && item.getData().getData() == Product.DYE_2.getItem().getData().getData()) {
                yellowDye += item.getAmount();
            }

            //if enough wool/dyes then craft.
            if (wool >= woolNeeded && redDye >= dye1Needed && yellowDye >= dye2Needed) {
                CWUtil.removeItems(inv, Product.WOOL.getItem(), woolNeeded, true);
                CWUtil.removeItems(inv, Product.DYE_1.getItem(), dye1Needed, true, true);
                CWUtil.removeItems(inv, Product.DYE_2.getItem(), dye2Needed, true, true);

                Product[] leatherArmor  = new Product[] {Product.HELMET, Product.CHESTPLATE, Product.LEGGINGS, Product.BOOTS};
                dropLoc.getWorld().dropItem(dropLoc, CWUtil.random(leatherArmor).getItem().setLeatherColor(CWUtil.getRandomColor()));

                ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f));
                player.updateInventory();
                return;
            }
        }
        if (wool < woolNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (woolNeeded - wool) + " more WOOL to craftl! &4&l<<"));
        } else if (redDye < dye1Needed) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (dye1Needed - redDye) + " more WHITE DYE to craftl! &4&l<<"));
        } else if(yellowDye < dye2Needed) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (dye2Needed - yellowDye) + " more BLUE DYE to craftl! &4&l<<"));
        }
    }

}
