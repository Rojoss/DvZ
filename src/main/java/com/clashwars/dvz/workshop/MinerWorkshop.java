package com.clashwars.dvz.workshop;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.dwarves.Miner;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinerWorkshop extends WorkShop {

    private List<Block> mineableBlocks = new ArrayList<Block>();

    public MinerWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }


    public List<Block> getMineableBlocks() {
        return mineableBlocks;
    }


    @Override
    public void onBuild() {
        if (cuboid == null || cuboid.getBlocks() == null || cuboid.getBlocks().size() <= 0) {
            build(getOrigin());
        }
        for (Block block : cuboid.getBlocks()) {
            if (((Miner)DvzClass.MINER.getClassClass()).getMineableMaterials().contains(block.getType())) {
                mineableBlocks.add(block);
                break;
            }
        }
        setCraftBlock();
    }

    @Override
    public void onLoad() {
        build(getOrigin());

        new BukkitRunnable() {
            @Override
            public void run() {
                onBuild();
            }
        }.runTaskLater(dvz, 20);
    }
}
