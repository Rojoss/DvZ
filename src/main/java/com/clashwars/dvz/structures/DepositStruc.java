package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.DepositData;
import com.clashwars.dvz.structures.internal.Structure;

public class DepositStruc extends Structure {

    private DepositData data;

    public DepositStruc() {
        if (dvz.getStrucCfg().getDepositData() == null) {
            dvz.getStrucCfg().setDepositData(new DepositData());
        }
        data = dvz.getStrucCfg().getDepositData();
    }

    @Override
    public String getRegion() {
        return data.getRegion();
    }

}
