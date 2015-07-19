package com.clashwars.dvz.classes.monsters;

import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Silverfish extends MobClass {

    public Silverfish() {
        super();
        dvzClass = DvzClass.SILVERFISH;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)60, displayName, 90, -1);

        abilities.add(Ability.INFECT_STONE);
        abilities.add(Ability.ROAR);
        abilities.add(Ability.INFEST);
    }

    @Override
    public void onEquipClass(Player player) {
        Hat hat = HatManager.getHat(player);
        if (hat != null && hat.isEquipped()) {
            hat.unequip();
        }
    }
}
