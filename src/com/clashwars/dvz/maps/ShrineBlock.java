package com.clashwars.dvz.maps;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.DvZ;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Set;

public class ShrineBlock {

    private DvZ dvz;
    private Hologram hologram;

    private Location location;
    private ShrineType type;
    private int hp;
    private boolean destroyed = false;

    public ShrineBlock(Location location, ShrineType type) {
        dvz = DvZ.inst();
        this.location = location;
        this.type = type;
        this.hp = dvz.getCfg().SHRINE__BLOCK_HP;
        Location hologramLoc = new Location(location.getWorld(), location.getBlockX() + 0.5f, location.getBlockY() + 1.2f, location.getBlockZ() + 0.5f);
        hologram = HolographicDisplaysAPI.createHologram(dvz, hologramLoc, CWUtil.integrateColor(new String[]{CWUtil.integrateColor(getHpPercString())}));
    }

    public void destroy() {
        hp = 0;
        destroyed = true;
        location.getBlock().setType(Material.AIR);

        //Delete hologram
        if (hologram != null) {
            hologram.delete();
            hologram = null;
        }

        //Launch away block
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, Material.ENDER_PORTAL_FRAME, (byte) 0);
        Vector velocity = RandomUtils.getRandomCircleVector();
        velocity.multiply(0.5f);
        velocity.setY(0.8f);
        fallingBlock.setVelocity(velocity);

        location.getWorld().playSound(location, Sound.EXPLODE, 1f, 1.2f);
        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, location);


        //Check for remaining blocks.
        Set<ShrineBlock> blocks = dvz.getGM().getShrineBlocks(type);
        int blockCount = 0;
        for (ShrineBlock block : blocks) {
            if (!block.isDestroyed()) {
                blockCount++;
            }
        }

        if (blockCount <= 0) {
            World world = location.getWorld();
            ParticleEffect.EXPLOSION_LARGE.display(8, 4, 8, 0, 20, location);
            new BukkitRunnable() {
                private int i = 0;
                @Override
                public void run() {
                    i++;
                    if (i >= 10) {
                        cancel();
                        return;
                    }
                    location.getWorld().playSound(location, Sound.EXPLODE, 1.5f, 0.8f);
                }
            }.runTaskTimer(dvz, 0, 2);
            final Vector loc = location.toVector();
            for (int x = loc.getBlockX() - 6; x < loc.getBlockX() + 6; x++) {
                for (int y = loc.getBlockY() - 3; y < loc.getBlockY() + 3; y++) {
                    for (int z = loc.getBlockZ() - 6; z < loc.getBlockZ() + 6; z++) {
                        Block block = world.getBlockAt(x, y, z);
                        if (block.getType() == Material.BEDROCK) {
                            continue;
                        }
                        FallingBlock fb = world.spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                        Vector v = RandomUtils.getRandomCircleVector();
                        v.setY(1);
                        fb.setVelocity(v);
                        block.setType(Material.AIR);
                    }
                }
            }

            if (type == ShrineType.WALL) {
                dvz.getGM().captureWall();
            } else if (type == ShrineType.KEEP_1) {
                dvz.getGM().captureFirstKeepShrine();
            } else {
                dvz.getGM().stopGame(false, null);
            }
        }
    }

    public void remove() {
        this.hp = dvz.getCfg().SHRINE__BLOCK_HP;
        if (hologram != null) {
            hologram.delete();
            hologram = null;
        }
    }

    public void damage() {
        damage(dvz.getCfg().SHRINE__DAMAGE_PER_HIT);
    }

    public void damage(int amount) {
        this.hp -= amount;
        if (this.hp <= 0) {
            destroy();
            return;
        }
        if (this.hp % 10 == 0) {
            if (hologram != null) {
                hologram.removeLine(0);
                hologram.addLine(CWUtil.integrateColor(getHpPercString()));
            }
        }
    }


    public Double getHpPerc() {
        return CWUtil.getPercentage(hp, dvz.getCfg().SHRINE__BLOCK_HP);
    }

    public String getHpPercString() {
        Double percentage = getHpPerc();
        if (percentage > 80) {
            return "&a" + Math.round(percentage) + "%";
        } else if (percentage > 60) {
            return "&6" + Math.round(percentage) + "%";
        } else if (percentage > 40) {
            return "&e" + Math.round(percentage) + "%";
        } else if (percentage > 20) {
            return "&c" + Math.round(percentage) + "%";
        } else if (percentage <= 20) {
            return "&4&l" + Math.round(percentage) + "%";
        }
        return "&7" + percentage + "%";
    }


    public Location getLocation() {
        return location;
    }

    public ShrineType getType() {
        return type;
    }

    public int getHp() {
        return hp;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}
