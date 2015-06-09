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
import org.bukkit.Location;
import org.bukkit.Material;
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

public class Fletcher extends DwarfClass {

    public Fletcher() {
        super();
        dvzClass = DvzClass.FLETCHER;
        classItem = new DvzItem(Material.WOOD_SWORD, 1, (byte)0, "&2&lFletcher", 30, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&2&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
        equipment.add(new DvzItem(Material.STONE_SWORD, 1, -1, -1));
        DvzItem spade = new DvzItem(Material.STONE_SPADE, 1, -1, -1);
        spade.addEnchantment(Enchantment.DIG_SPEED, 1);
        equipment.add(spade);
    }

    @EventHandler
    private void mobDamage(EntityDamageByEntityEvent event) {
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
                for (CWEntity animal : ws.getChickens()) {
                    if (animal.entity().getUniqueId() == event.getEntity().getUniqueId()) {
                        isOwnAnimal = true;
                    }
                }
            }
        }

        if (!isOwnAnimal) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis is not your " + event.getEntity().getType().name() + "! &4&l<<"));
            return;
        }
        event.setCancelled(false);
    }


    @EventHandler
    private void mobKill(EntityDeathEvent event) {
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
                for (CWEntity animal : ws.getChickens()) {
                    if (animal.entity().getUniqueId() == entity.getUniqueId()) {
                        isOwnAnimal = true;
                    }
                }
            }
        }
        if (!isOwnAnimal) {
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
        } else {
            ParticleEffect.PORTAL.display(0.5f, 0.5f, 0.5f, 0.0001f, 8, entity.getLocation(), 500);
            cwp.addClassExp(1);
        }
        entity.getWorld().dropItem(entity.getLocation(), feathers);
        ws.spawnChicken(EntityType.CHICKEN, CWUtil.random(ws.getCuboid().getMaxY() + 5, ws.getCuboid().getMaxY() + 15));
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
        if (dvz.getPM().getPlayer(player).getPlayerClass() != DvzClass.FLETCHER) {
            return;
        }

        if (!dvz.getWM().hasWorkshop(player.getUniqueId())) {
            return;
        }

        FletcherWorkshop ws = (FletcherWorkshop)dvz.getWM().getWorkshop(player.getUniqueId());
        if (!ws.isBuild()) {
            return;
        }

        if (!ws.getCuboid().contains(event.getClickedBlock())) {
            return;
        }

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
                ParticleEffect.SPELL_WITCH.display(0.2f, 0.2f, 0.2f, 0.0001f, 20, event.getClickedBlock().getLocation().add(0.5f, 0.5f, 0.5f), 500);

                dvz.getPM().getPlayer(player).addClassExp(40);
                // + 1 per gravel
                // + 1 per chicken
                // + 2 per chicken in air
                // ~ 68
                return;
            }
        }

        if (feathers < feathersNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (feathersNeeded - feathers) + " more FEATHERS to craftl! &4&l<<"));
        } else if (flint < flintNeeded) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou need " + (flintNeeded - flint) + " more FLINT to craftl! &4&l<<"));
        }
    }


    @EventHandler(priority = EventPriority.HIGH)
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
        event.setCancelled(false);
        if (CWUtil.randomFloat() <= getDoubleOption("flint-chance")) {
            block.getWorld().dropItemNaturally(block.getLocation().add(0.5f, 0.5f, 0.5f), Product.FLINT.getItem());
        }
        dvz.getPM().getPlayer(player).addClassExp(1);
    }
}
