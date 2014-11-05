package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.effect.effects.BoilEffect;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.cuboid.Cuboid;
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

import java.util.*;

public class AlchemistWorkshop extends WorkShop {

    BoilEffect boilEffect;

    private Set<Block> cauldrons = new HashSet<Block>();
    private Inventory chest = null;
    private Location chestLoc = null;
    private Cuboid pot = null;

    private boolean potFilled = false;
    private int sugar = 0;
    private int melons = 0;

    public AlchemistWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    @Override
    public void onBuild() {
        onLoad();
    }

    @Override
    public void onLoad() {
        calculateLocations();

        //Initialize boiling effect.
        boilEffect = new BoilEffect(dvz.getEM());
        boilEffect.setLocation(getOrigin());
        boilEffect.amt = 20;
        boilEffect.particleOffset = new org.bukkit.util.Vector(1, 0.8f, 1);
        boilEffect.soundVolume = 0.1f;
        boilEffect.soundPitch = 1.5f;
        boilEffect.soundDelay = 10;
        boilEffect.setPaused(true);
        boilEffect.start();

        //Cauldron refilling and rain effect.
        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                iterations++;

                if (cauldrons == null || cauldrons.size() <= 0) {
                    calculateLocations();
                    if (cauldrons == null || cauldrons.size() <= 0) {
                        return;
                    }
                }

                //Rain
                for (Block cauldron : cauldrons) {
                    ParticleEffect.DRIP_WATER.display(cauldron.getLocation().add(0.5f, 10, 0.5f), 0.3f, 4, 0.3f, 0.001f, 1);
                }

                //Check if waited long enough as we don't wanna refill cauldrons every update.
                if (iterations < getType().getClassClass().getIntOption("cauldron-refill-delay")) {
                    return;
                }
                iterations = 0;

                //Refill a random cauldron that isn't full.
                Block cauldron = CWUtil.random(new ArrayList<Block>(cauldrons));
                for (int i = 0; i < 6; i++) {
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
        cauldrons.clear();
        chest = null;
        pot = null;
    }

    private boolean calculateLocations() {
        if (getCuboid() == null || cuboid.getWorld() == null) {
            return false;
        }
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

        return true;
    }

    public void checkPotFilled() {
        List<Block> waterBlocks = pot.getBlocks(new Material[]{Material.STATIONARY_WATER});
        if (waterBlocks.size() >= 18) {
            potFilled = true;
            getOwner().sendMessage(Util.formatMsg("&6Pot filled with water."));
            getOwner().sendMessage(Util.formatMsg("&7You can now start adding ingredients."));
            boilEffect.setPaused(false);
        }
    }

    public void wrongIngredientAdded() {
        getOwner().sendMessage(Util.formatMsg("&cWrong ingredient added!"));
        getOwner().sendMessage(Util.formatMsg("&cYou have to start over again with brewing."));

        //Reset
        boilEffect.setPaused(true);
        potFilled = false;
        melons = 0;
        sugar = 0;

        //Clear water and Effect/Sound
        getOwner().playSound(getOwner().getLocation(), Sound.FIZZ, 1.0f, 1.0f);
        Cuboid potTop = pot.clone();
        potTop.contract(Cuboid.Dir.DOWN, 1);
        List<Block> waterBlocks = potTop.getBlocks(new Material[]{Material.STATIONARY_WATER});
        for (Block block : waterBlocks) {
            block.setType(Material.AIR);
            ParticleEffect.SMOKE.display(block.getLocation().add(0.5f, 0.5f, 0.5f), 0.3f, 0.3f, 0.3f, 0.0001f, 5);
        }
    }

    private void brew() {
        getOwner().sendMessage(Util.formatMsg("&6Potion brewed!"));

        //Put item in chest and if chest is full drop it at chest.
        CWItem item;
        if (melons > 0) {
            item = Ability.HEAL_POTION.getAbilityClass().getCastItem();
        } else {
            item = Ability.SPEED_POTION.getAbilityClass().getCastItem();
        }
        if (item != null) {
            boolean added = false;
            for (int i = 0; i < chest.getSize(); i++) {
                if (chest.getItem(i) == null || chest.getItem(i).getType() == Material.AIR) {
                    chest.addItem(item);
                    added = true;
                    break;
                }
            }
            if (!added) {
                chestLoc.getWorld().dropItem(chestLoc.add(0,1,0), item);
            }
            ParticleEffect.WITCH_MAGIC.display(chestLoc.add(0.5f, 0.5f, 0.5f), 0.2f, 0.2f, 0.2f, 0.0001f, 30);
        }

        //Reset
        boilEffect.setPaused(true);
        potFilled = false;
        melons = 0;
        sugar = 0;

        //Clear water/Effect
        List<Block> waterBlocks = pot.getBlocks(new Material[]{Material.STATIONARY_WATER});
        for (Block block : waterBlocks) {
            block.setType(Material.AIR);
            ParticleEffect.SMOKE.display(block.getLocation().add(0.5f, 0.5f, 0.5f), 0.3f, 0.3f, 0.3f, 0.0001f, 5);
        }
    }


    public Cuboid getPot() {
        return pot;
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
