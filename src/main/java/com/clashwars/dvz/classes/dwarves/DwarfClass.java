package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.DvzClass;

public class DwarfClass extends BaseClass {

    public DwarfClass() {
        super();
        dvzClass = DvzClass.DWARF;

        abilities.add(Ability.HEAL_POTION);
        abilities.add(Ability.SPEED_POTION);
    }
}
