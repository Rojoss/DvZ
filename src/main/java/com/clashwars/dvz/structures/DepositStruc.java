package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.DepositData;
import com.clashwars.dvz.structures.internal.Structure;

public class DepositStruc extends Structure {

    private DepositData data;

    public DepositStruc() {
        data = dvz.getStrucCfg().getDepositData();
    }

}
