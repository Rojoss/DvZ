package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Blaze extends MobClass {

    public Blaze() {
        super();
        dvzClass = DvzClass.BLAZE;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)61, displayName, 50, -1);

        abilities.add(Ability.SHOOT);
        abilities.add(Ability.GLIDE);
        abilities.add(Ability.FIREBALL);
    }

}
