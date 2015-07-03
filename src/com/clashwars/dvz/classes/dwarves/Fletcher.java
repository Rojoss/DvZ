package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.workshop.FletcherWorkshop;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Fletcher extends DwarfClass {

    public Fletcher() {
        super();
        dvzClass = DvzClass.FLETCHER;
        classItem = new DvzItem(Material.WOOD_SWORD, 1, (byte)0, "&2&lFletcher", 30, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&2&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        DvzItem spade = new DvzItem(Material.STONE_SPADE, 1, -1, -1);
        spade.addEnchantment(Enchantment.DIG_SPEED, 1);
        equipment.add(spade);
        equipment.add(new DvzItem(Material.STONE_AXE, 1, -1, -1));
    }

    @EventHandler
    private void mobDamage(EntityDamageByEntityEvent event) {
        Long t = System.currentTimeMillis();
        if (!(event.getEntity() instanceof Chicken)) {
            return;
        }

        Player player;
        if (!(event.getDamager() instanceof Player)) {
            if (!(event.getDamager() instanceof  Projectile)) {
                return;
            }
            Projectile proj = (Projectile)event.getDamager();
            if (proj.getShooter() == null || !(proj.getShooter() instanceof Player)) {
                return;
            }
            player = (Player)proj.getShooter();
        } else {
            player = (Player)event.getDamager();
        }

        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.FLETCHER) {
            return;
        }

        boolean isOwnAnimal = false;
        if (dvz.getWM().hasWorkshop(player.getUniqueId())) {
            final FletcherWorkshop ws = (FletcherWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
            if (ws.isBuild()) {
                List<CWEntity> chickens = ws.getChickens();
                for (CWEntity animal : chickens) {
                    if (animal.entity().getUniqueId() == event.getEntity().getUniqueId()) {
                        isOwnAnimal = true;
                    }
                }
            }
        }

        if (!isOwnAnimal) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your " + event.getEntity().getType().name() + "! &4&l<<"));
            dvz.logTimings("Fletcher.mobDamage()[other animal]", t);
            return;
        }
        dvz.logTimings("Fletcher.mobDamage()", t);
        event.setCancelled(false);
    }


    @EventHandler
    private void mobKill(EntityDeathEvent event) {
        Long t = System.currentTimeMillis();
        if (event.getEntityType() != EntityType.CHICKEN) {
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

        //Check for killing animal that belongs to the killer.
        boolean isOwnAnimal = false;
        FletcherWorkshop ws = null;
        if (dvz.getWM().hasWorkshop(entity.getKiller().getUniqueId())) {
            ws = (FletcherWorkshop)dvz.getWM().getWorkshop(entity.getKiller().getUniqueId());
            if (ws.isBuild()) {
                List<CWEntity> chickens = ws.getChickens();
                for (CWEntity animal : chickens) {
                    if (animal.entity().getUniqueId() == entity.getUniqueId()) {
                        isOwnAnimal = true;
                    }
                }
            }
        }
        if (!isOwnAnimal) {
            dvz.logTimings("Fletcher.mobKill()[other animal]", t);
            return;
        }

        //Feathers for killing chickens.
        Location loc = entity.getLocation();
        CWItem feathers = Product.FEATHER.getItem();
        feathers.setAmount(1);
        if (loc.getBlockY() > ws.getCuboid().getMinY() + getIntOption("chicken-bonus-height")) {
            feathers.setAmount(2);
            ParticleEffect.VILLAGER_HAPPY.display(0.5f, 0.5f, 0.5f, 0.0001f, 8, entity.getLocation(), 500);
            cwp.addClassExp(2);
            dvz.getSM().changeLocalStatVal(cwp.getUUID(), StatType.FLETCHER_FEATHERS_COLLECTED, 2);
        } else {
            CWUtil.sendActionBar(cwp.getPlayer(), CWUtil.integrateColor("&2&l>> &aTIP: &7You get double feathers/xp if you kill chickens in the air! &2&l<<"));
            ParticleEffect.PORTAL.display(0.5f, 0.5f, 0.5f, 0.0001f, 8, entity.getLocation(), 500);
            dvz.getSM().changeLocalStatVal(cwp.getUUID(), StatType.FLETCHER_FEATHERS_COLLECTED, 1);
            cwp.addClassExp(1);
        }
        entity.getWorld().dropItem(entity.getLocation(), feathers);
        ws.spawnChicken(EntityType.CHICKEN, CWUtil.random(ws.getCuboid().getMaxY() + 5, ws.getCuboid().getMaxY() + 15));
        dvz.logTimings("Fletcher.mobKill()", t);
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void interact(PlayerInteractEvent event) {
        Long t = System.currentTimeMillis();
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

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
            return;
        }

        FletcherWorkshop ws = (FletcherWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cBuild your workshop by placing your workbench on one of the pistons! &4&l<<"));
        if (!ws.isBuild()) {
            return;
        }

        if (!ws.getCuboid().contains(event.getClickedBlock())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cClick on the workbench in your own workshop to craft! &4&l<<"));
            return;
        }

        Inventory inv = player.getInventory();
        Location dropLoc = event.getClickedBlock().getLocation().add(0.5f, 1f, 0.5f);
        int flint = 0;
        int feathers = 0;
        int sticks = 0;
        int flintNeeded = getIntOption("flint-needed");
        int feathersNeeded = getIntOption("feathers-needed");
        int sticksNeeded = getIntOption("sticks-needed");
        //Find all feathers/flint/sticks in inventory.
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }
            //Increase flint/feathers/sticks amt if the current item is flint/feather.
            if (item.getType() == Product.FLINT.getItem().getType()) {
                flint += item.getAmount();
            } else if (item.getType() == Product.FEATHER.getItem().getType()) {
                feathers += item.getAmount();
            } else if (item.getType() == Product.FLETCHER_STICK.getItem().getType()) {
                sticks += item.getAmount();
            }
            //if enough feathers, flint and sticks then craft.
            if (feathers >= feathersNeeded && flint >= flintNeeded && sticks >= sticksNeeded) {
                CWUtil.removeItems(inv, Product.FLINT.getItem(), flintNeeded, true);
                CWUtil.removeItems(inv, Product.FEATHER.getItem(), feathersNeeded, true);
                CWUtil.removeItems(inv, Product.FLETCHER_STICK.getItem(), sticksNeeded, true);
                //Random chance to get a bow.
                if (CWUtil.randomFloat() <= getDoubleOption("bow-product-chance")) {
                    dropLoc.getWorld().dropItem(dropLoc, Product.BOW.getItem());
                    dvz.getSM().changeLocalStatVal(player, StatType.FLETCHER_BOWS_CRAFTED, 1);
                }
                //Random amount of arrows.
                CWItem arrows = Product.ARROW.getItem();
                arrows.setAmount(CWUtil.random(getIntOption("min-arrow-amount"), getIntOption("max-arrow-amount")));
                dropLoc.getWorld().dropItem(dropLoc, arrows);
                ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 500);

                dvz.getSM().changeLocalStatVal(player, StatType.FLETCHER_ARROWS_CRAFTED, arrows.getAmount());

                dvz.getPM().getPlayer(player).addClassExp(30);
                // + 1 per flint
                // + 1 per chicken
                // + 2 per chicken in air
                // + 1 per stick
                // ~ 63
                return;
            }
        }

        if (feathers < feathersNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (feathersNeeded - feathers) + " more FEATHERS to craftl! &4&l<<"));
        } else if (flint < flintNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (flintNeeded - flint) + " more FLINT to craftl! &4&l<<"));
        } else if (sticks < sticksNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (sticksNeeded - sticks) + " more STICKS to craftl! &4&l<<"));
        }
        dvz.logTimings("Fletcher.interact()", t);
    }


    @EventHandler(priority = EventPriority.HIGH)
    private void blockBreak(BlockBreakEvent event) {
        Long t = System.currentTimeMillis();
        final Block block = event.getBlock();

        Player player = event.getPlayer();
        if (block.getType() == Material.LOG || block.getType() == Material.LOG_2) {
            if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.FLETCHER) {
                return;
            }

            CWUtil.dropItemStack(block.getLocation(), Product.FLETCHER_STICK.getItem(), dvz, player);
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

            }.runTaskLater(dvz, getIntOption("wood-respawn-time"));
            dvz.logTimings("Fletcher.blockBreak()[wood]", t);
        }

        if (block.getType() != Material.GRAVEL) {
            return;
        }

        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.FLETCHER) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need to be a fletcher to collect flint! &4&l<<"));
            return;
        }

        //Give flint with random chance for breaking gravel.
        dvz.getSM().changeLocalStatVal(event.getPlayer(), StatType.FLETCHER_GRAVEL_DUG, 1);
        event.setCancelled(false);
        if (CWUtil.randomFloat() <= getDoubleOption("flint-chance")) {
            dvz.getSM().changeLocalStatVal(event.getPlayer(), StatType.FLETCHER_FLINT_COLLECTED, 1);
            dvz.getPM().getPlayer(player).addClassExp(1);
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5f, 0.5f, 0.5f), Product.FLINT.getItem());
        }
        dvz.logTimings("Fletcher.blockBreak()", t);
    }
}
