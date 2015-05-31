package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BakerWorkshop extends WorkShop {

    private List<Block> wheatBlocks = new ArrayList<Block>();
    private Block hopperBlock;
    private boolean removed = false;

    public BakerWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }


    public List<Block> getWheatBlocks() {
        return wheatBlocks;
    }

    public Block getHopperBlock() {
        return hopperBlock;
    }


    @Override
    public void onBuild() {
        if (cuboid == null || cuboid.getBlocks() == null || cuboid.getBlocks().size() <= 0) {
            if (getOrigin() == null) {
                return;
            }
            build(getOrigin());
        }
        for (Block block : cuboid.getBlocks()) {
            if (block.getType() == Material.HOPPER) {
                hopperBlock = block;
            }
            if (block.getType() == Material.CROPS) {
                wheatBlocks.add(block);
            }
        }

        //Cauldron refilling and rain effect.
        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (removed) {
                    cancel();
                    return;
                }
                for (Block block : wheatBlocks) {
                    if (block.getType() != Material.CROPS) {
                        continue;
                    }
                    if (block.getData() == 7) {
                        continue;
                    }
                    if (CWUtil.randomFloat() <= 0.2f) {
                        block.setData((byte)(block.getData() + 1));
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.5f, 0.5f, 0.5f, 0.01f, 8, block.getLocation().add(0.5f, 0.5f, 0.5f));
                        block.getWorld().playSound(block.getLocation(), Sound.DIG_GRASS, 0.1f, 2.0f);
                    }
                }
            }
        }.runTaskTimer(dvz, 10, 10);
    }

    @Override
    public void onLoad() {
        if (getOrigin() == null) {
            return;
        }
        build(getOrigin());

        new BukkitRunnable() {
            @Override
            public void run() {
                onBuild();
            }
        }.runTaskLater(dvz, 20);
    }

    @Override
    public void onRemove() {
        removed = true;
    }
}
