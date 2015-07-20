package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.TailorWorkshop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Tailor extends DwarfClass {

    private final int woolNeeded = 2;
    private final int dyeNeeded = 1;
    private final int woolDropAmount = 1;
    public final int sheepAmount = 8;
    private final int flowerRespawnTime = 500;
    private final int minWoolRegrowTime = 50;
    private final int maxWoolRegrowTime = 400;

    private Product[] armorPieces = new Product[] {Product.HELMET, Product.CHESTPLATE, Product.LEGGINGS, Product.BOOTS};
    private int armorID = 0;

    public Tailor() {
        super();
        dvzClass = DvzClass.TAILOR;
        classItem = new DvzItem(Material.SHEARS, 1, (byte)0, "&3&lTailor", 40, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&3&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        equipment.add(new DvzItem(Material.SHEARS, 1, -1, -1));

        equipment.add(new DvzItem(Material.IRON_PICKAXE, 1, -1, -1));
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void shear(PlayerShearEntityEvent event) {
        Long t = System.currentTimeMillis();
        final Entity entity = event.getEntity();
        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (cwp.getPlayerClass() != DvzClass.TAILOR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need to be a tailor to shear sheep! &4&l<<"));
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            dvz.logTimings("Tailor.shear()[no workshop]", t);
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            return;
        }
        final TailorWorkshop ws = (TailorWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());

        CWEntity cwe = null;
        List<CWEntity> entities = ws.getSheep();
        for (CWEntity sheep : entities) {
            if (sheep.entity().getUniqueId() == entity.getUniqueId()) {
                cwe = sheep;
            }
        }
        if (cwe == null) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your sheep! &4&l<<"));
            dvz.logTimings("Tailor.shear()[invalid sheep]", t);
            return;
        }

        cwe.setSheared(true);
        entity.getWorld().dropItemNaturally(entity.getLocation(), Product.WOOL.getItem(woolDropAmount));
        player.playSound(entity.getLocation(), Sound.SHEEP_SHEAR, 1.0f, 1.0f);
        dvz.getPM().getPlayer(player).addClassExp(1);
        dvz.getSM().changeLocalStatVal(player, StatType.TAILOR_SHEEP_SHEARED, 1);

        final CWEntity sheep = cwe;
        ws.runnables.add(new BukkitRunnable() {
            @Override
            public void run() {
                sheep.setSheared(false);
                ParticleEffect.SNOWBALL.display(0.6f, 0.6f, 0.6f, 0.0001f, 30, entity.getLocation().add(0, 0.5f, 0));
                entity.getWorld().playSound(entity.getLocation(), Sound.DIG_WOOL, 1.0f, 0.0f);
            }
        }.runTaskLater(dvz, CWUtil.random(minWoolRegrowTime, maxWoolRegrowTime)));
        dvz.logTimings("Tailor.shear()", t);
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Long t = System.currentTimeMillis();
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
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need to be a tailor to collect flowers/dyes! &4&l<<"));
            dvz.logTimings("Tailor.blockBreak()[not tailor]", t);
            return;
        }

        event.setCancelled(false);
        if (block.getData() == 3) {
            block.getWorld().dropItem(block.getLocation(), Product.DYE_1.getItem());
            dvz.getSM().changeLocalStatVal(player, StatType.TAILOR_WHITE_FLOWERS_COLLECTED, 1);
        } else if (block.getData() == 1) {
            block.getWorld().dropItem(block.getLocation(), Product.DYE_2.getItem());
            dvz.getSM().changeLocalStatVal(player, StatType.TAILOR_BLUE_FLOWERS_COLLECTED, 1);
        }
        dvz.getPM().getPlayer(player).addClassExp(3);
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
        }.runTaskLater(dvz, flowerRespawnTime);
        dvz.logTimings("Tailor.blockBreak()", t);
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
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.TAILOR) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            return;
        }

        TailorWorkshop ws = (TailorWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
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
        int wool = 0;
        int whiteDye = 0;
        int blueDye = 0;
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
                whiteDye += item.getAmount();
            } else if (item.getType() == Product.DYE_2.getItem().getType() && item.getData().getData() == Product.DYE_2.getItem().getData().getData()) {
                blueDye += item.getAmount();
            }

            //if enough wool/dyes then craft.
            if (wool >= woolNeeded && whiteDye >= dyeNeeded && blueDye >= dyeNeeded) {
                CWUtil.removeItems(inv, Product.WOOL.getItem(), woolNeeded, false);
                CWUtil.removeItems(inv, Product.DYE_1.getItem(), dyeNeeded, false, true);
                CWUtil.removeItems(inv, Product.DYE_2.getItem(), dyeNeeded, false, true);

                dropLoc.getWorld().dropItem(dropLoc, armorPieces[armorID].getItem().setLeatherColor(CWUtil.getRandomColor()));
                armorID++;
                if (armorID >= armorPieces.length) {
                    armorID = 0;
                }

                ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 500);
                player.updateInventory();

                dvz.getSM().changeLocalStatVal(player, StatType.TAILOR_ARMOR_CRAFTED, 1);

                dvz.getPM().getPlayer(player).addClassExp(35);
                // + 5 per flower broken
                // + 1 per sheep sheared
                // = 60
                dvz.logTimings("Tailor.interact()[craft]", t);
                return;
            }
        }
        if (wool < woolNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (woolNeeded - wool) + " more WOOL to craftl! &4&l<<"));
        } else if (whiteDye < dyeNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (dyeNeeded - whiteDye) + " more WHITE DYE to craftl! &4&l<<"));
        } else if(blueDye < dyeNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (dyeNeeded - blueDye) + " more BLUE DYE to craftl! &4&l<<"));
        }
        dvz.logTimings("Tailor.interact()", t);
    }

}
