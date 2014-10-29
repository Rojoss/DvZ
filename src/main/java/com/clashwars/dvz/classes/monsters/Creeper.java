package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Creeper extends MobClass {

    public Creeper() {
        super();
        dvzClass = DvzClass.CREEPER;
        classItem = new CWItem(Material.MONSTER_EGGS, 1, (short)50, displayName);
    }

}
