package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
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
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, getIntOption("duration"), getIntOption("amplifier")));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
