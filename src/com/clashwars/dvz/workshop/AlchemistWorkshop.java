package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.effect.effects.BoilEffect;
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

    public BoilEffect boilEffect;

    private Set<Block> cauldrons = new HashSet<Block>();
    private Inventory chest = null;
    private Location chestLoc = null;
    private Cuboid pot = null;

    public int sugar = 0;
    public int melons = 0;
    public boolean potFilled = false;

    public AlchemistWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }


    public Cuboid getPot() {
        return pot;
    }

    public Inventory getChest() {
        return chest;
    }

    public Location getChestLoc() {
        return chestLoc;
    }

    public Set<Block> getCauldrons() {
        return cauldrons;
    }


    @Override
    public void onBuild() {
        Long t = System.currentTimeMillis();
        //Get all cauldrons and the chest.
        List<Block> blocks = cuboid.getBlocks();
        for (Block block : blocks) {
            if (block.getType() == Material.CAULDRON) {
                cauldrons.add(block);
            } else if (block.getType() == Material.CHEST) {
                chest = ((Chest)block.getState()).getBlockInventory();
                chestLoc = block.getLocation();
            }
        }

        //Calculate the pot inside.
        pot = cuboid.clone();
        pot.inset(2, 1, 2);
        pot.setMaxY(pot.getMinY() + 1);

        //Cauldron refilling and rain effect.
        runnables.add(new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (!isBuild()) {
                    cancel();
                    return;
                }
                iterations++;

                //Rain
                for (Block cauldron : cauldrons) {
                    ParticleEffect.DRIP_WATER.display(0.3f, 4, 0.3f, 0.001f, 2, cauldron.getLocation().add(0.5f, 10, 0.5f));
                }

                //Check if waited long enough as we don't wanna refill cauldrons every second.
                if (iterations < getType().getClassClass().getIntOption("cauldron-refill-delay")) {
                    return;
                }
                iterations = 0;

                //Refill a random cauldron that isn't full. (It will max try fill one 5 times)
                Block cauldron = CWUtil.random(new ArrayList<Block>(cauldrons));
                for (int i = 0; i < 5; i++) {
                    //Check if it's already full
                    if (cauldron.getData() == 3) {
                        cauldron = CWUtil.random(new ArrayList<Block>(cauldrons));
                        continue;
                    }
                    //Add 1 layer
                    if (cauldron.getData() >= 4) {
                        cauldron.setData((byte)1);
                    } else {
                        cauldron.setData((byte)(cauldron.getData() + 1));
                    }
                    //Effect/Sound
                    ParticleEffect.WATER_SPLASH.display(0.3f, 0.4f, 0.3f, 0.001f, 40, cauldron.getLocation().add(0.5f,0.7f,0.5f));
                    if (cauldron.getData() == 3) {
                        cauldron.getWorld().playSound(cauldron.getLocation(), Sound.SPLASH, 0.2f, 2.0f);
                    } else {
                        cauldron.getWorld().playSound(cauldron.getLocation(), Sound.SPLASH, 0.01f, 1.8f);
                    }
                    break;
                }
            }
        }.runTaskTimer(dvz, 0, 20));
        dvz.logTimings("AlchemistWorkshop.onBuild()", t);
    }

    @Override
    public void onDestroy() {
        cauldrons.clear();
        cauldrons = new HashSet<Block>();
        chest = null;
        chestLoc = null;
        pot = null;
        sugar = 0;
        melons = 0;
        potFilled = false;
    }

}
