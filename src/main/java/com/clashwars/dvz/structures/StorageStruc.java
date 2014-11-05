package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.StorageData;
import com.clashwars.dvz.structures.internal.Structure;

public class StorageStruc extends Structure {

    private StorageData data;

    public StorageStruc() {
        if (dvz.getStrucCfg().getStorageData() == null) {
            dvz.getStrucCfg().setStorageData(new StorageData());
        }
        data = dvz.getStrucCfg().getStorageData();
    }

    @Override
    public String getRegion() {
        return data.getRegion();
    }

}
