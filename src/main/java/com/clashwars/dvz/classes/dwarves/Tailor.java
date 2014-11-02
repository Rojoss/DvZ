package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Tailor extends DwarfClass {

    public Tailor() {
        super();
        dvzClass = DvzClass.TAILOR;
        classItem = new DvzItem(Material.SHEARS, 1, (byte)0, "&3&lTailor", 40, -1);

        equipment.add(new DvzItem(Material.WORKBENCH, 1, (byte)0, "&3&lWorkshop", new String[] {"&7Place this down on any of the pistons.", "&7Your workshop will be build then."}, 500, -1));
    }
}
