package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealPotion extends BaseAbility {

    public HealPotion() {
        super();
        ability = Ability.HEAL_POTION;
        castItem = new DvzItem(Material.POTION, 1, (short)8197, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("HealPotion.castAbility()[cd]", t);
            return;
        }

        dvz.getSM().changeLocalStatVal(player, StatType.DWARF_HEALTH_POTS_USED, 1);

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        ParticleEffect.HEART.display(0.3f, 0.3f, 0.3f, 0.1f, 10, player.getLocation().add(0, 2, 0));
        player.playSound(player.getLocation(), Sound.DRINK, 1, 2f);
        dvz.logTimings("HealPotion.castAbility()", t);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
