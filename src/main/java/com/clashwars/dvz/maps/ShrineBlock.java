package com.clashwars.dvz.maps;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.gmail.filoghost.holograms.api.Hologram;
import com.gmail.filoghost.holograms.api.HolographicDisplaysAPI;
import org.bukkit.Bukkit;
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
        Bukkit.broadcastMessage(getHpPercString());
        hologram = HolographicDisplaysAPI.createHologram(dvz, location.add(0.5f, 1f, 0.5f), CWUtil.integrateColor(new String[]{CWUtil.integrateColor(getHpPercString())}));
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
            } else {
                dvz.getGM().stopGame(false, null);
            }
        }
    }

    public void damage() {
        this.hp -= dvz.getCfg().SHRINE__DAMAGE_PER_HIT;
        if (this.hp <= 0) {
            destroy();
            return;
        }
        if (hologram != null) {
            hologram.setLine(0, CWUtil.integrateColor(getHpPercString()));
            hologram.update();
        }
    }


    public Double getHpPerc() {
        return CWUtil.getPercentage(hp, dvz.getCfg().SHRINE__BLOCK_HP);
    }

    public String getHpPercString() {
        Double percentage = getHpPerc();
        if (percentage > 0.8d) {
            return "&a" + percentage + "%";
        } else if (percentage > 0.6d) {
            return "&6" + percentage + "%";
        } else if (percentage > 0.4d) {
            return "&e" + percentage + "%";
        } else if (percentage > 0.2d) {
            return "&c" + percentage + "%";
        } else if (percentage <= 0.2d) {
            return "&4&l" + percentage + "%";
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
