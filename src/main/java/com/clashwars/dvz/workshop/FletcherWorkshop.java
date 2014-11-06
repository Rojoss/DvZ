package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.dwarves.Miner;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FletcherWorkshop extends WorkShop {

    List<CWEntity> animals = new ArrayList<CWEntity>();
    private Block craftBlock;

    public FletcherWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    @Override
    public void onBuild() {
        onLoad();
    }

    @Override
    public void onLoad() {
        //TODO: Config option for amounts
        //Spawn chickens
        for (int i = 0; i < 5; i++) {
            spawnAnimal(EntityType.CHICKEN, true);
        }
        //Spawn pigs
        for (int i = 0; i < 5; i++) {
            spawnAnimal(EntityType.PIG, false);
        }

        //Get craft block
        for (Block block : cuboid.getBlocks()) {
            if (block.getType() == Material.WORKBENCH) {
                craftBlock = block;
                break;
            }
        }
    }


    public void spawnAnimal(EntityType type, boolean inAir) {
        int y = getOrigin().getBlockY();
        if (inAir) {
            y = CWUtil.random(cuboid.getMaxY() + 10, cuboid.getMaxY() + 20);
        }

        CWEntity entity = CWEntity.create(type, new Location(cuboid.getWorld(), CWUtil.random(cuboid.getMinX()-1, cuboid.getMaxX()-1),
                y, CWUtil.random(cuboid.getMinZ()-1, cuboid.getMaxZ()-1)));
        animals.add(entity);
        dvz.entities.add(entity.entity().getUniqueId());
    }


    @Override
    public void onRemove() {
        for (CWEntity entity : animals) {
            entity.entity().remove();
        }
        animals.clear();
    }
}
