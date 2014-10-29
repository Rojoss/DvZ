package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Builder extends DwarfClass {

    public Builder() {
        super();
        dvzClass = DvzClass.BUILDER;
        classItem = new CWItem(Material.CLAY_BRICK, 1, (byte)0, "&e&lBuilder");
    }
}
