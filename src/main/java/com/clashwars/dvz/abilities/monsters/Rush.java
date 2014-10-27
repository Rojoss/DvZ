package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Rush extends MobAbility {

    public Rush() {
        super();
        this.ability = Ability.RUSH;
        castItem = new CWItem(Material.SUGAR, 1, (short) 0, displayName);
    }

    @Override
    public void setAbility(Ability ability) {
        this.ability = ability;
    }

    @Override
    public void castAbility(Player player, Location triggerloc) {
        if (CWUtil.getTargetedPlayer(player, getIntOption("range")) == null) {
            return;
        }

        if(!canCast(player)) {
            return;
        }

        Vector dir = player.getLocation().getDirection();
        player.setVelocity(new Vector(dir.getX() * getDoubleOption("multiplier"), 0.2, dir.getZ() * getDoubleOption("multiplier")));
        //TODO: Add particle and sound effects

    }

}
