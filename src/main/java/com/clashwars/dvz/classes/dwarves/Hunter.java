package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Hunter extends DwarfClass {

    public Hunter() {
        super();
        dvzClass = DvzClass.HUNTER;
        classItem = new CWItem(Material.WOOD_SWORD, 1, (byte)0, "&2&lHunter");
    }
}
