package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Pig extends MobClass {

    public Pig() {
        super();
        dvzClass = DvzClass.PIG;
        classItem = new CWItem(Material.MONSTER_EGG, 1, (short)90, displayName);
    }

}
