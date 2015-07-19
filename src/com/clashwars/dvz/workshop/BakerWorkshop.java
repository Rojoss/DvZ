package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.helpers.EntityTag;
import com.clashwars.cwcore.helpers.PoseType;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class BakerWorkshop extends WorkShop {

    private List<Block> wheatBlocks = new ArrayList<Block>();
    private Block hopperBlock;
    private Location millLoc;
    private ArmorStand mill;
    private float millRotation = 0;
    private float millSpeed = 0.05f;

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
        Long t = System.currentTimeMillis();
        List<Block> blocks = cuboid.getBlocks();
        for (Block block : blocks) {
            if (block.getType() == Material.HOPPER) {
                hopperBlock = block;
            }
            if (block.getType() == Material.CROPS) {
                wheatBlocks.add(block);

            }
            if (block.getType() == Material.HAY_BLOCK) {
                block.setType(Material.AIR);
                millLoc = block.getLocation();
                millLoc.setYaw(getRotation());
                if (getRotation() == 0 || getRotation() == 360) {
                    millLoc.add(0.5f, -1f, 0.9f);
                } else if (getRotation() == 90) {
                    millLoc.add(-0.9f, -1f, 0.5f);
                } else if (getRotation() == 180) {
                    millLoc.add(0.5f, -1f, 0.1f);
                } else if (getRotation() == 270) {
                    millLoc.add(0.9f, -1f, 0.5f);
                }
                mill = (ArmorStand)CWEntity.create(EntityType.ARMOR_STAND, millLoc)
                        .setName("Mill")
                        .setNameVisible(false)
                        .setRemoveWhenFarAway(false)
                        .setArmorstandVisibility(false)
                        .setArmorstandGravity(false)
                        .setPose(PoseType.HEAD, new EulerAngle(67.5f, 0, 0))
                        .setHelmet(new CWItem(Material.HAY_BLOCK))
                        .setTag(EntityTag.MARKER, 1)
                        .entity();
            }
        }

        //Fast wheat regrowth.
        runnables.add(new BukkitRunnable() {
            @Override
            public void run() {
                if (!isBuild()) {
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
                    if (CWUtil.randomFloat() <= 0.16f) {
                        block.setData((byte) (block.getData() + 1));
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), block.getData()), 0.5f, 0.5f, 0.5f, 0.01f, 8, block.getLocation().add(0.5f, 0.5f, 0.5f));
                        block.getWorld().playSound(block.getLocation(), Sound.DIG_GRASS, 0.1f, 2.0f);
                    }
                }
            }
        }.runTaskTimer(dvz, 10, 10));

        millSpeed = (float)CWUtil.random(2,6) / 100;

        //Mill rotation
        runnables.add(new BukkitRunnable() {
            @Override
            public void run() {
                if (!isBuild()) {
                    cancel();
                    return;
                }
                if (mill != null) {
                    mill.setHeadPose(new EulerAngle(67.5f, 0, millRotation));
                    millRotation -= millSpeed;
                }
            }
        }.runTaskTimer(dvz, 1, 1));
        dvz.logTimings("BakerWorkshop.onBuild()", t);
    }

    @Override
    public void onDestroy() {
        if (mill != null) {
            mill.remove();
        }

        if (millLoc != null) {
            List<Entity> entities = CWUtil.getNearbyEntities(millLoc, 3, Arrays.asList(new EntityType[] {EntityType.ARMOR_STAND}));
            for (Entity e : entities) {
                e.remove();
            }
        }

        mill = null;
        millLoc = null;
        wheatBlocks.clear();
        wheatBlocks = new ArrayList<Block>();
        hopperBlock = null;
    }
}
