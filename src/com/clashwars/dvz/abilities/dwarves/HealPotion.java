package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HealPotion extends DwarfAbility {

    public HealPotion() {
        super();
        ability = Ability.HEAL_POTION;
        castItem = new DvzItem(Material.POTION, 1, (short)8197, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
        ParticleEffect.HEART.display(0.3f, 0.3f, 0.3f, 0.1f, 10, player.getLocation().add(0, 2, 0));
        player.playSound(player.getLocation(), Sound.DRINK, 1, 2f);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
