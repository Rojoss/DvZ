package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Fletcher extends DwarfClass {

    public Fletcher() {
        super();
        dvzClass = DvzClass.FLETCHER;
        classItem = new DvzItem(Material.WOOD_SWORD, 1, (byte)0, "&2&lFletcher", 30, -1);
    }
}
