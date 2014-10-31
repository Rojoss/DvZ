package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Alchemist extends DwarfClass {

    public Alchemist() {
        super();
        dvzClass = DvzClass.ALCHEMIST;
        classItem = new DvzItem(Material.POTION, 1, (byte)0, "&5&lAlchemist", 60, -1);
    }
}
