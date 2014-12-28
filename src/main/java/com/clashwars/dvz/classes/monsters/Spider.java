package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Spider extends MobClass {

    public Spider() {
        super();
        dvzClass = DvzClass.SPIDER;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)52, displayName, 30, -1);

        abilities.add(Ability.POISON);
        abilities.add(Ability.WEB);
        abilities.add(Ability.POISONATTACK);
    }

    @Override
    public void onEquipClass(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    }

}
