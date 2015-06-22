package com.clashwars.dvz.workshop;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.dwarves.Miner;
import org.bukkit.block.Block;

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
        Long t = System.currentTimeMillis();
        List<Block> blocks = cuboid.getBlocks();
        for (Block block : blocks) {
            if (((Miner)DvzClass.MINER.getClassClass()).getMineableMaterials().contains(block.getType())) {
                mineableBlocks.add(block);
            }
        }
        dvz.logTimings("MinerWorkshop.onBuild()", t);
    }

    @Override
    public void onDestroy() {
        mineableBlocks.clear();
        mineableBlocks = new ArrayList<Block>();
    }
}
