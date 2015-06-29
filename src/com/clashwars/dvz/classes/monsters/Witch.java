package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Witch extends MobClass {

    public Witch() {
        super();
        dvzClass = DvzClass.VILLAGER;

        abilities.add(Ability.POTION_BOMB);
        abilities.add(Ability.MORPH);

        equipment.add(new DvzItem(PotionType.POISON, true, 1, -1, -1, true).addPotionEffect(PotionEffectType.POISON, 1, 60));
        equipment.add(new DvzItem(PotionType.SLOWNESS, true, 1, -1, -1, true).addPotionEffect(PotionEffectType.SLOW, 1, 100));
        equipment.add(new DvzItem(Material.WOOD_SWORD, 100, -1));

        switchable = true;
    }

}
