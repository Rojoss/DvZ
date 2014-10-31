package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Tailor extends DwarfClass {

    public Tailor() {
        super();
        dvzClass = DvzClass.TAILOR;
        classItem = new DvzItem(Material.SHEARS, 1, (byte)0, "&3&lTailor", 40, -1);
    }
}
