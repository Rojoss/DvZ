package com.clashwars.dvz.workshop;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class WorkShopData {

    private DvzClass type;
    private Vector origin;
    private String world;
    private int rotation = -1;
    private int variant = -1;

    public WorkShopData() {
        //--
    }


    public DvzClass getType() {
        return type;
    }

    public void setType(DvzClass type) {
        this.type = type;
    }


    public Location getOrigin() {
        if (getWorld() == null) {
            return null;
        }
        return new Location(getWorld(), origin.getX(), origin.getY(), origin.getZ());
    }

    public void setOrigin(Location loc) {
        origin = loc.toVector();
        setWorld(loc.getWorld());
    }

    public void setOrigin(Vector loc) {
        origin = loc;
    }


    public World getWorld() {
        return DvZ.inst().getServer().getWorld(world);
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setWorld(World world) {
        this.world = world.getName();
    }


    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }


    public int getVariant() {
        return variant;
    }

    public void setVariant(int variant) {
        this.variant = variant;
    }

}
