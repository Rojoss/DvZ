package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.FurnaceData;
import com.clashwars.dvz.structures.internal.Structure;

public class FurnaceStruc extends Structure {

    private FurnaceData data;

    public FurnaceStruc() {
        if (dvz.getStrucCfg().getFurnaceData() == null) {
            dvz.getStrucCfg().setFurnaceData(new FurnaceData());
        }
        data = dvz.getStrucCfg().getFurnaceData();
    }

    @Override
    public String getRegion() {
        return data.getRegion();
    }

}
