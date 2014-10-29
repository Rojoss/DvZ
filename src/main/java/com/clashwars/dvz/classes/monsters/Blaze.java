package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Blaze extends MobClass {

    public Blaze() {
        super();
        dvzClass = DvzClass.BLAZE;
        classItem = new CWItem(Material.MONSTER_EGGS, 1, (short)61, displayName);
    }

}
