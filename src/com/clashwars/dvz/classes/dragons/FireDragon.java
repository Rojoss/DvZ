package com.clashwars.dvz.classes.dragons;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FireDragon extends DragonClass {

    public FireDragon() {
        super();
        dvzClass = DvzClass.FIREDRAGON;

        abilities.add(Ability.BURN);
        abilities.add(Ability.FIRE_BREATH);
        abilities.add(Ability.FIREFLY);
    }
    @Override
    public void onEquipClass(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
    }


}
