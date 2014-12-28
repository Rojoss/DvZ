package com.clashwars.dvz.classes.dwarves;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;

public class Builder extends DwarfClass {

    public Builder() {
        super();
        dvzClass = DvzClass.BUILDER;
        classItem = new DvzItem(Material.CLAY_BRICK, 1, (byte)0, "&e&lBuilder", 10, -1);

        abilities.add(Ability.BUILDING_BLOCK);
        abilities.add(Ability.BUILDING_BRICK);
    }

}
