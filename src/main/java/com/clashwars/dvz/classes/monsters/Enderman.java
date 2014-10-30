package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Enderman extends MobClass {

    public Enderman() {
        super();
        dvzClass = DvzClass.ENDERMAN;
        classItem = new CWItem(Material.MONSTER_EGG, 1, (short)58, displayName);
    }

}
