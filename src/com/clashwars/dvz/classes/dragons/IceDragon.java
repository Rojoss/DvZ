package com.clashwars.dvz.classes.dragons;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

public class IceDragon extends DragonClass {

    public IceDragon() {
        super();
        dvzClass = DvzClass.ICEDRAGON;

        abilities.add(Ability.ICESPIKE);
        abilities.add(Ability.HAIL);
        abilities.add(Ability.SNOW_SPRAY);
    }

}
