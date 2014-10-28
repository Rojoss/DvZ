package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Alchemist extends DwarfClass {

    public Alchemist() {
        super();
        dvzClass = DvzClass.ALCHEMIST;
        color = ChatColor.DARK_PURPLE;
        classItem = new CWItem(Material.POTION, 1, (byte)0, "&5&lAlchemist");
    }
}
