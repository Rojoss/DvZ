package com.clashwars.dvz.maps;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import org.bukkit.Location;
import org.bukkit.Material;

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

        //Check for remaining blocks.
        Set<ShrineBlock> blocks = dvz.getGM().getShrineBlocks(type);
        int blockCount = 0;
        for (ShrineBlock block : blocks) {
            if (!block.isDestroyed()) {
                blockCount++;
            }
        }

        if (blockCount <= 0) {
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
        if (this.hp == 10 || this.hp == 25 || this.hp == 50 || this.hp == 75) {
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
