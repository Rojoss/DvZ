package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.dwarves.Miner;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class MinerWorkshop extends WorkShop {

    private List<Block> mineableBlocks = new ArrayList<Block>();
    private Block craftBlock;

    public MinerWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }


    public List<Block> getMineableBlocks() {
        return mineableBlocks;
    }


    @Override
    public void onBuild() {
        for (Block block : cuboid.getBlocks()) {
            if (((Miner)DvzClass.MINER.getClassClass()).getMineableMaterials().contains(block.getType())) {
                mineableBlocks.add(block);
                break;
            }
            if (block.getType() == Material.WORKBENCH) {
                craftBlock = block;
                break;
            }
        }
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

    @Override
    public void onRemove() {

    }
}
