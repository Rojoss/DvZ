package com.clashwars.dvz.abilities.monsters.witchvillager;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;

public class Morph extends BaseAbility {

    public Morph() {
        super();
        ability = Ability.MORPH;
        castItem = new DvzItem(Material.PRISMARINE_SHARD, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        DvzClass dvzClass = dvz.getPM().getPlayer(player).getPlayerClass();
        ParticleEffect.SPELL_WITCH.display(0.5f, 1f, 0.5f, 0, 50, player.getLocation().add(0,1,0));
        ParticleEffect.VILLAGER_HAPPY.display(0.5f, 1f, 0.5f, 0, 50, player.getLocation().add(0,1,0));
        if (dvzClass == DvzClass.VILLAGER) {
            player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1, 0);
            dvz.getPM().getPlayer(player).setClass(DvzClass.WITCH);
        }
        else if (dvzClass == DvzClass.WITCH) {
            player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1, 2);
            dvz.getPM().getPlayer(player).setClass(DvzClass.VILLAGER);
        }
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
