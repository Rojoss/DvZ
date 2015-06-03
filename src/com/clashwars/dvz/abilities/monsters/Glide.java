package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class Glide extends MobAbility {

    public Glide() {
        super();
        ability = Ability.GLIDE;
        castItem = new DvzItem(Material.BLAZE_POWDER, 1, (short)0, displayName, 199, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        Vector d = player.getLocation().getDirection();
        player.setVelocity(d.multiply(dvz.getGM().getMonsterPower(0.4f) + 0.4f).setY(0.32f));
        ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0, 5, player.getLocation());
        ParticleEffect.LAVA.display(0.5f, 0.5f, 0.5f, 0, 1, player.getLocation());
        player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 0.1f, 2);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
