package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Enderman extends MobClass {

    public Enderman() {
        super();
        dvzClass = DvzClass.ENDERMAN;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)58, displayName, 90, -1);
    }

}
