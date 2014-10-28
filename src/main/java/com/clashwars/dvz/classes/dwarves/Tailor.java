package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Tailor extends DwarfClass {

    public Tailor() {
        super();
        dvzClass = DvzClass.TAILOR;
        color = ChatColor.DARK_AQUA;
        classItem = new CWItem(Material.SHEARS, 1, (byte)0, "&3&lTailor");
    }
}
