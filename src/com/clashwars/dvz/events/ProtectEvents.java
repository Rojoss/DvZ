package com.clashwars.dvz.events;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

public class ProtectEvents implements Listener {

    private DvZ dvz;
    private GameManager gm;

    public ProtectEvents(DvZ dvz) {
        this.dvz = dvz;
        gm = dvz.getGM();
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void damageEntity(EntityDamageByEntityEvent event) {
        //Don't allow it if game hasn't started.
        if (!gm.isStarted()) {
            event.setCancelled(true);
            return;
        }

        //Get damager player.
        Player damagerPlayer = null;
        if (event.getDamager() instanceof Player) {
            damagerPlayer = (Player)event.getDamager();
        } else {
            if (event.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile)event.getDamager();
                if (proj.getShooter() instanceof Player) {
                    damagerPlayer = (Player)proj.getShooter();
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        //Don't allow hitting mobs
        if (!(event.getEntity() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        //Don't allow damaging same class. (No friendly fire)
        CWPlayer damager = dvz.getPM().getPlayer(damagerPlayer);
        CWPlayer damaged = dvz.getPM().getPlayer((Player)event.getEntity());
        if (damager.getPlayerClass().getType() == damaged.getPlayerClass().getType()) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void blockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());

        //Don't allow it if game hasn't started.
        if (!gm.isStarted()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow monsters
        if (cwp.isMonster()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow dwarves during dwarf time within the inner walls.
        if (gm.isDwarves() && cwp.isDwarf()) {
            if (dvz.getMM().getActiveMap() == null || dvz.getMM().getActiveMap().getCuboid("innerwall").contains(event.getBlock())) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void blockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());

        //Don't allow it if game hasn't started.
        if (!gm.isStarted()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow monsters
        if (cwp.isMonster()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow dwarves during dwarf time within the inner walls
        if (gm.isDwarves() && cwp.isDwarf()) {
            if (dvz.getMM().getActiveMap() == null || dvz.getMM().getActiveMap().getCuboid("innerwall").contains(event.getBlock())) {
                event.setCancelled(true);
            }

        }
        Material blockType = event.getBlock().getType();
        if (blockType == Material.MELON_BLOCK || blockType == Material.GRAVEL || blockType == Material.RED_ROSE || blockType == Material.SUGAR_CANE_BLOCK) {
            event.setCancelled(true);
        }
        blockType = event.getBlock().getRelative(BlockFace.UP).getType();
        if (blockType == Material.SUGAR_CANE_BLOCK || blockType == Material.RED_ROSE)  {
            event.setCancelled(true);
        }

    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void itemDrop(PlayerDropItemEvent event) {
        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());

        //Don't allow it if game hasn't started.
        if (!gm.isStarted()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow monsters
        if (cwp.isMonster()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow when no class
        if (cwp.getPlayerClass() == DvzClass.DWARF) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void itemPickup(PlayerPickupItemEvent event) {
        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());

        //Don't allow it if game hasn't started.
        if (!gm.isStarted()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow monsters
        if (cwp.isMonster()) {
            event.setCancelled(true);
            return;
        }

        //Don't allow when no class
        if (cwp.getPlayerClass() == DvzClass.DWARF) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void explosion(ExplosionPrimeEvent event) {
        //Don't allow it if game hasn't started or if it's dwarves.
        if (!gm.isStarted() || gm.isDwarves()) {
            event.setCancelled(true);
            return;
        }
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void interact(PlayerInteractEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            //Don't allow opening invs/gui's and using stuff like buttons.
            Material type = event.getClickedBlock().getType();
            if (type == Material.LEVER || type == Material.STONE_BUTTON  || type == Material.WOOD_BUTTON || type == Material.NOTE_BLOCK  || type == Material.JUKEBOX  || type == Material.CAKE
                    || type == Material.WOODEN_DOOR || type == Material.TRAP_DOOR || type == Material.FENCE_GATE || type == Material.DISPENSER || type == Material.FURNACE
                    || type == Material.BURNING_FURNACE || type == Material.WORKBENCH || type == Material.BREWING_STAND || type == Material.ENCHANTMENT_TABLE || type == Material.CAULDRON
                    || type == Material.ENDER_CHEST || type == Material.CHEST || type == Material.BEACON || type == Material.ANVIL || type == Material.HOPPER || type == Material.DROPPER
                    || type == Material.DRAGON_EGG || type == Material.FIRE ||type == Material.ACACIA_FENCE_GATE || type == Material.BIRCH_FENCE_GATE || type == Material.DARK_OAK_FENCE_GATE
                    || type == Material.JUNGLE_FENCE_GATE || type == Material.SPRUCE_FENCE_GATE || type == Material.ACACIA_DOOR || type == Material.BIRCH_DOOR || type == Material.JUNGLE_DOOR
                    || type == Material.DARK_OAK_DOOR || type == Material.SPRUCE_DOOR || type == Material.ARMOR_STAND || type == Material.BURNING_FURNACE || type == Material.TRAPPED_CHEST) {
                event.setCancelled(true);
            }
            //Don't allow spawning mobs with mob eggs
            if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.MONSTER_EGG) {
                event.setCancelled(true);
            }
            //Allow using buckets (Handled by bucket empty/fill event)
            if (event.getItem() == null || event.getItem().getType() == null) {
                return;
            }
            if (event.getItem().getType() == Material.BUCKET || event.getItem().getType() == Material.WATER_BUCKET || event.getItem().getType() == Material.LAVA_BUCKET) {
                return;
            }

            //Allow placing blocks. (Handled by block place event)
            return;
        }
        //Allow breaking blocks. (Handled by block break event) and allow clicking in air.
        return;
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void interactEntity(PlayerInteractEntityEvent event) {
        //Allow shearing mobs (Handled by shear event)
        if (event.getPlayer().getItemInHand().getType() == Material.SHEARS) {
            return;
        }
        //Allow leashing mobs (Handled by leash event)
        if (event.getPlayer().getItemInHand().getType() == Material.LEASH) {
            return;
        }
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void shearEntity(PlayerShearEntityEvent event) {
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void leashEntity(PlayerLeashEntityEvent event) {
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void bedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void vehicleEnter(VehicleEnterEvent event) {
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void vehicleDamage(VehicleDamageEvent event) {
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void bucketFill(PlayerBucketFillEvent event) {
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void bucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        event.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void portalUse(PlayerPortalEvent event) {
        event.setCancelled(true);
    }
}
