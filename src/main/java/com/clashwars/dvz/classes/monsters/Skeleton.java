package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Skeleton extends MobClass {

    public Skeleton() {
        super();
        dvzClass = DvzClass.SKELETON;
        classItem = new CWItem(Material.MONSTER_EGG, 1, (short)51, displayName);

        abilities.add(Ability.RAPIDFIRE);
    }

}
