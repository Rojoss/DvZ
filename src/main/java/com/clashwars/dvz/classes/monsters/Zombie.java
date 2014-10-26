package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.classes.DvZClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Zombie extends MobClass {

    public Zombie() {
        super();
        dvzClass = DvZClass.ZOMBIE;
        classItem = new CWItem(Material.MONSTER_EGGS, 1, (short)54, "&c&lZombie");
    }

}
