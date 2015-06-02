package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.helpers.PoseType;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

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
                        .entity();
            }
        }

        //Fast wheat regrowth.
        new BukkitRunnable() {
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

        //Mill rotation
        new BukkitRunnable() {
            @Override
            public void run() {
                if (removed) {
                    cancel();
                    return;
                }
                if (mill != null && mill.isValid()) {
                    mill.setHeadPose(new EulerAngle(67.5f, 0, millRotation));
                    millRotation -= 0.05f;
                }
            }
        }.runTaskTimer(dvz, 1, 1);
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
        if (mill != null) {
            mill.remove();
        }
        mill = null;
        List<Entity> entities = CWUtil.getNearbyEntities(millLoc, 3, Arrays.asList(new EntityType[] {EntityType.ARMOR_STAND}));
        for (Entity e : entities) {
            e.remove();
        }
    }
}
