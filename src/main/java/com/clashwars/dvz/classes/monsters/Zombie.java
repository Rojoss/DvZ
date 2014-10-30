package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Zombie extends MobClass {

    public Zombie() {
        super();
        dvzClass = DvzClass.ZOMBIE;
        classItem = new CWItem(Material.MONSTER_EGG, 1, (short)54, displayName);

        abilities.add(Ability.INFECT);
        abilities.add(Ability.RUSH);
    }

}
