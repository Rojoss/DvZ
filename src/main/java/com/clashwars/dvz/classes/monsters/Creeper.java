package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Creeper extends MobClass {

    public Creeper() {
        super();
        dvzClass = DvzClass.CREEPER;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)50, displayName, 40, -1);
    }

}
