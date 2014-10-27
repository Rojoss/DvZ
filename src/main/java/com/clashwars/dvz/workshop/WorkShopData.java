package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

public class WorkShopData {

    private String location;
    private int width = 0;
    private int length = 0;
    private int height = 0;
    private String craftBlock;
    private DvzClass type;

    public WorkShopData() {
        //--
    }


    public Location getLocation() {
        return CWUtil.locFromString(location);
    }

    public void setLocation(Location location) {
        this.location = CWUtil.locToString(location);
        Bukkit.broadcastMessage(this.location);
    }


    public Location getCraftBlock() {
        return CWUtil.locFromString(craftBlock);
    }

    public void setCraftBlock(Location craftBlock) {
        this.craftBlock = CWUtil.locToString(craftBlock);
    }


    public DvzClass getType() {
        return type;
    }

    public void setType(DvzClass type) {
        this.type = type;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
