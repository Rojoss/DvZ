package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.effect.effects.BoilEffect;
import com.clashwars.cwcore.effect.effects.IconEffect;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.*;
import org.bukkit.util.Vector;

import java.util.*;

public class AlchemistWorkshop extends WorkShop {

    BoilEffect boilEffect;

    private Set<Block> cauldrons = new HashSet<Block>();
    private Inventory chest = null;
    private Location chestLoc = null;
    private Location potLocMin = null;
    private Location potLocMax = null;

    private boolean potFilled = false;
    private int sugar = 0;
    private int melons = 0;

    public AlchemistWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);

        boilEffect = new BoilEffect(dvz.getEM());
        boilEffect.setLocation(getLocation());
        if (getCenter() != null) {
            boilEffect.setLocation(getCenter().add(0, 1, 0));
        }
        boilEffect.amt = 20;
        boilEffect.particleOffset = new Vector(2, 1, 2);
        boilEffect.popVolume = 0.1f;
        boilEffect.popPitch = 1.8f;
        boilEffect.soundVolume = 0.1f;
        boilEffect.soundPitch = 1.5f;
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
                chestLoc = block.getLocation();
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

    public void checkPotFilled() {
        Set<Block> waterBlocks = CWUtil.findBlocksInArea(potLocMin, potLocMax, new Material[]{Material.STATIONARY_WATER});
        if (waterBlocks.size() >= 18) {
            potFilled = true;
            getOwner().sendMessage(Util.formatMsg("&6Pot filled with water."));
            getOwner().sendMessage(Util.formatMsg("&7You can now start adding ingredients."));
            boilEffect.start();
        }
    }

    public void wrongIngredientAdded() {
        getOwner().sendMessage(Util.formatMsg("&cWrong ingredient added!"));
        getOwner().sendMessage(Util.formatMsg("&cYou have to start over again with brewing."));

        boilEffect.cancel();
        potFilled = false;
        melons = 0;
        sugar = 0;

        Location potLocMinClone = potLocMin.clone();
        potLocMinClone.setY(potLocMin.getY() + 1);
        Set<Block> waterBlocks = CWUtil.findBlocksInArea(potLocMinClone, potLocMax, new Material[]{Material.STATIONARY_WATER});
        for (Block block : waterBlocks) {
            block.setType(Material.AIR);
        }
    }

    private void brew() {
        getOwner().sendMessage(Util.formatMsg("&6Potion brewed!"));

        CWItem item;
        if (melons > 0) {
            item = Ability.HEAL_POTION.getAbilityClass().getCastItem();
        } else {
            item = Ability.SPEED_POTION.getAbilityClass().getCastItem();
        }
        if (item != null) {
            if (chest.getContents().length >= chest.getSize()) {
                chestLoc.getWorld().dropItem(chestLoc, item);
            } else {
                chest.addItem(item);
            }
        }

        boilEffect.cancel();
        melons = 0;
        sugar = 0;

        Set<Block> waterBlocks = CWUtil.findBlocksInArea(potLocMin, potLocMax, new Material[]{Material.STATIONARY_WATER});
        for (Block block : waterBlocks) {
            block.setType(Material.AIR);
        }
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

    public boolean isPotFilled() {
        return potFilled;
    }

    public int getMelons() {
        return melons;
    }

    public void addMelon(int amt) {
        melons += amt;
        if (melons >= DvzClass.ALCHEMIST.getClassClass().getIntOption("melons-needed")) {
            brew();
        }
    }

    public int getSugar() {
        return sugar;
    }

    public void addSugar(int amt) {
        sugar += amt;
        if (sugar >= DvzClass.ALCHEMIST.getClassClass().getIntOption("sugar-needed")) {
            brew();
        }
    }
}
