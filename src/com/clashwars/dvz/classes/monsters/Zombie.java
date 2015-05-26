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

        equipment.add(new DvzItem(Material.IRON_SWORD, -1, -1));
        equipment.add(new DvzItem(Material.LEATHER_HELMET, -1, -1));
        equipment.add(new DvzItem(Material.LEATHER_CHESTPLATE, -1, -1));
        equipment.add(new DvzItem(Material.LEATHER_LEGGINGS, -1, -1));
        equipment.add(new DvzItem(Material.LEATHER_BOOTS, -1, -1));
    }

}
