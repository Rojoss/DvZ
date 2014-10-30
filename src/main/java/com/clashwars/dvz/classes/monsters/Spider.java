package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Material;

public class Spider extends MobClass {

    public Spider() {
        super();
        dvzClass = DvzClass.SPIDER;
        classItem = new CWItem(Material.MONSTER_EGG, 1, (short)52, displayName);

        abilities.add(Ability.POISON);
        abilities.add(Ability.WEB);
        abilities.add(Ability.POISONATTACK);
    }

}
