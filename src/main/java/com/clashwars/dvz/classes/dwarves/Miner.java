package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Miner extends DwarfClass {

    public Miner() {
        super();
        dvzClass = DvzClass.MINER;
        classItem = new DvzItem(Material.DIAMOND_PICKAXE, 1, (byte)0, "&8&lMiner", 20, -1);
    }
}
