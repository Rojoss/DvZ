package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Spider extends MobClass {

    public Spider() {
        super();
        dvzClass = DvzClass.SPIDER;
        classItem = new CWItem(Material.MONSTER_EGGS, 1, (short)52, displayName);
    }

}
