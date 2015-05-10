package com.clashwars.dvz.classes.dragons;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

public class FireDragon extends DragonClass {

    public FireDragon() {
        super();
        dvzClass = DvzClass.FIREDRAGON;

        abilities.add(Ability.BURN);
        abilities.add(Ability.FIRE_BREATH);
        abilities.add(Ability.FIREFLY);
        abilities.add(Ability.FIREBALL);
    }

}
