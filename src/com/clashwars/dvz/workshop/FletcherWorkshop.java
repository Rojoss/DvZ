package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FletcherWorkshop extends WorkShop {

    protected List<CWEntity> animals = new ArrayList<CWEntity>();

    public FletcherWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }


    public void spawnAnimal(final EntityType type, final int yOffset) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getOrigin() == null) {
                    cancel();
                    return;
                }
                int y = getOrigin().getBlockY() + yOffset;

                Cuboid inside = cuboid.clone();
                inside.inset(2, 0, 2);
                CWEntity entity = CWEntity.create(type, new Location(cuboid.getWorld(), CWUtil.random(inside.getMinX(), inside.getMaxX()+1),
                        y, CWUtil.random(inside.getMinZ(), inside.getMaxZ()+1)));
                animals.add(entity);
                dvz.entities.add(entity.entity().getUniqueId());
                ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, entity.entity().getLocation());
                cuboid.getWorld().playSound(entity.entity().getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 2.0f);
            }
        }.runTaskLater(dvz, CWUtil.random(DvzClass.FLETCHER.getClassClass().getIntOption("animal-respawn-time-min"), DvzClass.FLETCHER.getClassClass().getIntOption("animal-respawn-time-max")));
    }

    public List<CWEntity> getAnimals() {
        return animals;
    }


    @Override
    public void onBuild() {
        onLoad();
    }

    @Override
    public void onLoad() {
        if (cuboid == null || cuboid.getBlocks() == null || cuboid.getBlocks().size() <= 0) {
            if (getOrigin() == null) {
                return;
            }
            build(getOrigin());
        }
        //Spawn chickens
        for (int i = 0; i < DvzClass.FLETCHER.getClassClass().getIntOption("chicken-amount"); i++) {
            spawnAnimal(EntityType.CHICKEN, CWUtil.random(cuboid.getMaxY() + 10, cuboid.getMaxY() + 20));
        }

        setCraftBlock();
    }

    @Override
    public void onRemove() {
        super.onRemove();
        for (CWEntity entity : animals) {
            entity.entity().remove();
        }
        animals.clear();
    }
}
