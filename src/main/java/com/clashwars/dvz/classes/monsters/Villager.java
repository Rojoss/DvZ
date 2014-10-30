package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Villager extends MobClass {

    public Villager() {
        super();
        dvzClass = DvzClass.VILLAGER;
        classItem = new CWItem(Material.MONSTER_EGG, 1, (short)120, displayName);
    }

}
