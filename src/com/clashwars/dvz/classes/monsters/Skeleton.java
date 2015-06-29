package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Skeleton extends MobClass {

    public Skeleton() {
        super();
        dvzClass = DvzClass.SKELETON;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)51, displayName, 20, -1);

        abilities.add(Ability.RAPIDFIRE);

        equipment.add(new DvzItem(Material.ARROW, -1, 9));
    }

}
