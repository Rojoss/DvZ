package com.clashwars.dvz.classes.dragons;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

public class AirDragon extends DragonClass {

    public AirDragon() {
        super();
        dvzClass = DvzClass.AIRDRAGON;

        abilities.add(Ability.WIND);
    }

}
