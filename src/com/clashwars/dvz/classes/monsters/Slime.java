package com.clashwars.dvz.classes.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Slime extends MobClass {

    public static HashMap<UUID, List<org.bukkit.entity.Slime>> slimes = new HashMap<UUID, List<org.bukkit.entity.Slime>>();

    public Slime() {
        super();
        dvzClass = DvzClass.SLIME;
        classItem = new DvzItem(Material.MONSTER_EGG, 1, (short)55, displayName, 110, -1);

        abilities.add(Ability.SPLIT);
        abilities.add(Ability.SWAP);
        abilities.add(Ability.SPLASH);
        abilities.add(Ability.SLIME_SPRAY);
    }

    public static double getHealth(int size) {
        int value = 8;
        for (int i = 1; i < size; i++) {
            value *= 2;
        }
        return value;
    }

    @Override
    public void onEquipClass(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2, true, false), true);
    }
}
