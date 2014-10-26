package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.DvZClass;
import org.bukkit.ChatColor;

public class MobClass extends BaseClass {

    public MobClass() {
        super();
        dvzClass = DvZClass.MONSTER;
        color = ChatColor.RED;
    }

}
