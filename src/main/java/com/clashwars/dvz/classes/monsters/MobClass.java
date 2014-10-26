package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;

public class MobClass extends BaseClass {

    public MobClass() {
        super();
        dvzClass = DvzClass.MONSTER;
        color = ChatColor.RED;

        abilities.add(Ability.SUICIDE);
        abilities.add(Ability.HAMMER);
    }

}
