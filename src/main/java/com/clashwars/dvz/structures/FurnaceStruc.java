package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.FurnaceData;
import com.clashwars.dvz.structures.internal.Structure;

public class FurnaceStruc extends Structure {

    private FurnaceData data;

    public FurnaceStruc() {
        data = dvz.getStrucCfg().getFurnaceData();
    }

}
