package com.clashwars.dvz.structures;

import com.clashwars.dvz.structures.data.EnchantData;
import com.clashwars.dvz.structures.internal.Structure;

public class EnchantStruc extends Structure {

    private EnchantData data;

    public EnchantStruc() {
        data = dvz.getStrucCfg().getEnchantData();
    }

}
