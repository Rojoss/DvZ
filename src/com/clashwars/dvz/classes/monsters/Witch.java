package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

public class Witch extends MobClass {

    public Witch() {
        super();
        dvzClass = DvzClass.VILLAGER;

        abilities.add(Ability.POTION_BOMB);
    }

}
