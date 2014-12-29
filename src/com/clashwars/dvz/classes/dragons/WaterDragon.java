package com.clashwars.dvz.classes.dragons;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

public class WaterDragon extends DragonClass {

    public WaterDragon() {
        super();
        dvzClass = DvzClass.WATERDRAGON;

        abilities.add(Ability.GEYSER);
    }

}
