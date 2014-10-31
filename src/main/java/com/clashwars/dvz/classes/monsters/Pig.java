package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Pig extends MobClass {

    public Pig() {
        super();
        dvzClass = DvzClass.PIG;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)90, displayName, 70, -1);
    }

}
