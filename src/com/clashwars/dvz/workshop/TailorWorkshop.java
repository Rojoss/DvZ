package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TailorWorkshop extends WorkShop {

    protected List<CWEntity> sheep = new ArrayList<CWEntity>();

    public TailorWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    public List<CWEntity> getSheep() {
        return sheep;
    }


    @Override
    public void onBuild() {
        //Spawn sheep
        for (int i = 0; i < DvzClass.TAILOR.getClassClass().getIntOption("sheep-amount"); i++) {
            int y = getOrigin().getBlockY();
            Cuboid inside = cuboid.clone();
            inside.inset(2, 0, 2);

            CWEntity entity = CWEntity.create(EntityType.SHEEP, new Location(cuboid.getWorld(), CWUtil.random(inside.getMinX(), inside.getMaxX() + 1),
                    y, CWUtil.random(inside.getMinZ(), inside.getMaxZ()+1)));
            entity.setDyeColor(DyeColor.WHITE);

            sheep.add(entity);

            ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0.0001f, 10, entity.entity().getLocation());
            cuboid.getWorld().playSound(entity.entity().getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 2.0f);
        }
    }

    @Override
    public void onDestroy() {
        for (CWEntity entity : sheep) {
            entity.entity().remove();
        }
        sheep.clear();
        sheep = new ArrayList<CWEntity>();
    }
}
