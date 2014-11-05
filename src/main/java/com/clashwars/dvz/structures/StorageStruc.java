package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.StorageData;
import com.clashwars.dvz.structures.internal.Structure;

public class StorageStruc extends Structure {

    private StorageData data;

    public StorageStruc() {
        data = dvz.getStrucCfg().getStorageData();
    }

}
