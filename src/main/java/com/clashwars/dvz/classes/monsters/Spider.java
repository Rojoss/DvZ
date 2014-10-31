package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Spider extends MobClass {

    public Spider() {
        super();
        dvzClass = DvzClass.SPIDER;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)52, displayName, 30, -1);

        abilities.add(Ability.POISON);
        abilities.add(Ability.WEB);
        abilities.add(Ability.POISONATTACK);
    }

}
