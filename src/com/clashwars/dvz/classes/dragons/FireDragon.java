package com.clashwars.dvz.classes.dragons;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

public class FireDragon extends DragonClass {

    public FireDragon() {
        super();
        dvzClass = DvzClass.FIREDRAGON;
        abilities.add(Ability.BURN);
    }

}
