package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.utils.Cuboid;
import com.clashwars.dvz.classes.DvzClass;

public class WorkShopData {

    private DvzClass type;
    private String cuboid;

    public WorkShopData() {
        //--
    }


    public Cuboid getCuboid() {
        return Cuboid.deserialize(cuboid);
    }

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid.toString();
    }


    public DvzClass getType() {
        return type;
    }

    public void setType(DvzClass type) {
        this.type = type;
    }
}
