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

    protected List<CWEntity> chickens = new ArrayList<CWEntity>();

    public FletcherWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }


    public void spawnChicken(final EntityType type, final int yOffset) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isBuild()) {
                    cancel();
                    return;
                }

                int y = getOrigin().getBlockY() + yOffset;
                Cuboid inside = cuboid.clone();
                inside.inset(2, 0, 2);

                CWEntity entity = CWEntity.create(type, new Location(cuboid.getWorld(), CWUtil.random(inside.getMinX(), inside.getMaxX()+1),
                        y, CWUtil.random(inside.getMinZ(), inside.getMaxZ()+1)));
                chickens.add(entity);
                ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, entity.entity().getLocation(), 500);
            }
        }.runTaskLater(dvz, CWUtil.random(DvzClass.FLETCHER.getClassClass().getIntOption("animal-respawn-time-min"), DvzClass.FLETCHER.getClassClass().getIntOption("animal-respawn-time-max")));
    }

    public List<CWEntity> getChickens() {
        return chickens;
    }


    @Override
    public void onBuild() {
        //Spawn chickens
        for (int i = 0; i < DvzClass.FLETCHER.getClassClass().getIntOption("chicken-amount"); i++) {
            spawnChicken(EntityType.CHICKEN, CWUtil.random(cuboid.getMaxY() + 0, cuboid.getMaxY() + 10));
        }
    }

    @Override
    public void onDestroy() {
        for (CWEntity entity : chickens) {
            entity.entity().remove();
        }
        chickens.clear();
        chickens = null;
    }
}
