package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AlchemistWorkshop extends WorkShop {

    private Set<Block> cauldrons = new HashSet<Block>();
    private Inventory chest = null;
    private Location potLocMin = null;
    private Location potLocMax = null;

    public AlchemistWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    @Override
    public void onBuild() {
        Debug.bc("Alchemist workshop build!");
        onLoad();
    }

    @Override
    public void onLoad() {
        Debug.bc("Alchemist workshop loaded!");
        calculateLocations();

        //Cauldron refilling and rain effect.
        new BukkitRunnable() {
            int iterations = 0;
            int recalculated = 0;

            @Override
            public void run() {
                iterations++;
                for (Block cauldron : cauldrons) {
                    ParticleEffect.DRIP_WATER.display(cauldron.getLocation().add(0.5f, 10, 0.5f), 0.3f, 4, 0.3f, 0.001f, 1);
                }
                if (iterations < getType().getClassClass().getIntOption("cauldron-refill-delay")) {
                    return;
                }
                iterations = 0;
                if (cauldrons == null || cauldrons.isEmpty()) {
                    recalculated++;
                    if (recalculated >= 50) {
                        this.cancel();
                    }
                    calculateLocations();
                    if (cauldrons == null || cauldrons.isEmpty()) {
                        return;
                    }
                }
                Block cauldron = CWUtil.random(new ArrayList<Block>(cauldrons));
                for (int i = 0; i < 6; i++) {
                    //Check if it's already full
                    if (cauldron.getData() == 3) {
                        cauldron = CWUtil.random(new ArrayList<Block>(cauldrons));
                        continue;
                    }
                    if (cauldron.getData() >= 4) {
                        cauldron.setData((byte)1);
                    } else {
                        cauldron.setData((byte)(cauldron.getData() + 1));
                    }
                    ParticleEffect.SPLASH.display(cauldron.getLocation().add(0.5f,0.7f,0.5f), 0.3f, 0.4f, 0.3f, 0.001f, 40);
                    if (cauldron.getData() == 3) {
                        cauldron.getWorld().playSound(cauldron.getLocation(), Sound.SPLASH, 0.15f, 2.0f);
                    } else {
                        cauldron.getWorld().playSound(cauldron.getLocation(), Sound.SPLASH, 0.01f, 1.8f);
                    }
                    break;
                }
            }
        }.runTaskTimer(dvz, 0, 20);
    }

    @Override
    public void onRemove() {
        Debug.bc("Alchemist workshop removed!");
        cauldrons.clear();
        chest = null;
        potLocMin = null;
        potLocMax = null;
    }

    private boolean calculateLocations() {
        if (getLocation().getWorld() == null) {
            return false;
        }
        //Get all cauldrons and the chest.
        Set<Block> blocks = getBlocks();
        for (Block block : blocks) {
            if (block.getType() == Material.CAULDRON) {
                cauldrons.add(block);
            } else if (block.getType() == Material.CHEST) {
                chest = ((Chest)block.getState()).getBlockInventory();
            }
        }

        //Calculate the pot inside.
        Location l1 = getLocation();
        Location l2 = new Location(l1.getWorld(), getLocation2().getX(), getLocation2().getY(), getLocation2().getZ());

        potLocMin = new Location(l1.getWorld(), Math.min(l1.getBlockX(), l2.getBlockX()), Math.min(l1.getBlockY(), l2.getBlockY()), Math.min(l1.getBlockZ(), l2.getBlockZ()));
        potLocMax = new Location(l1.getWorld(), Math.max(l1.getBlockX(), l2.getBlockX()), Math.max(l1.getBlockY(), l2.getBlockY()), Math.max(l1.getBlockZ(), l2.getBlockZ()));

        potLocMin.add(2, 1, 2);
        potLocMax.subtract(2, 0, 2);
        potLocMax.setY(potLocMin.getY() + 1);
        return true;
    }


    public Location getPotMin() {
        return potLocMin;
    }

    public Location getPotMax() {
        return potLocMax;
    }

    public Inventory getChestInv() {
        return chest;
    }

    public Set<Block> getCauldrons() {
        return cauldrons;
    }
}
