package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Villager extends MobClass {

    public Villager() {
        super();
        dvzClass = DvzClass.VILLAGER;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)120, displayName, 80, -1);
    }

}
