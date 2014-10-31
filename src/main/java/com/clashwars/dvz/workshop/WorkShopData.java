package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Location;

public class WorkShopData {

    private String center = null;
    private String location = null;
    private String craftBlock = null;
    private int width = 0;
    private int length = 0;
    private int height = 0;
    private DvzClass type;

    public WorkShopData() {
        //--
    }


    public Location getLocation() {
        return CWUtil.locFromString(location);
    }

    public void setLocation(Location location) {
        this.location = CWUtil.locToString(location);
    }


    public Location getCenter() {
        return CWUtil.locFromString(center);
    }

    public void setCenter(Location center) {
        this.center = CWUtil.locToString(center);
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
