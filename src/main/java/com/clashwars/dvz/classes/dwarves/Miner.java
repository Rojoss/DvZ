package com.clashwars.dvz.classes.dwarves;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Miner extends DwarfClass {

    public Miner() {
        super();
        dvzClass = DvzClass.MINER;
        classItem = new CWItem(Material.DIAMOND_PICKAXE, 1, (byte)0, "&8&lMiner");
    }
}
