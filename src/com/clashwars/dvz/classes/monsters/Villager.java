package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Villager extends MobClass {

    public Villager() {
        super();
        dvzClass = DvzClass.VILLAGER;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)120, displayName, 80, -1);
        abilities.add(Ability.MORPH);
        abilities.add(Ability.BUFF);

        equipment.add(new DvzItem(PotionType.REGEN, true, 1, -1, -1, true).addPotionEffect(PotionEffectType.REGENERATION, 1, 100));
        equipment.add(new DvzItem(PotionType.INSTANT_HEAL, true, 1, -1, -1, true).addPotionEffect(PotionEffectType.HEAL, 1, 1));

        switchable = true;
    }

}
