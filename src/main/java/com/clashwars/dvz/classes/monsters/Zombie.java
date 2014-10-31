package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Zombie extends MobClass {

    public Zombie() {
        super();
        dvzClass = DvzClass.ZOMBIE;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)54, displayName, 10, -1);

        abilities.add(Ability.INFECT);
        abilities.add(Ability.RUSH);
    }

}
