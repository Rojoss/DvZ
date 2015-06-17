package com.clashwars.dvz.abilities.dwarves.builder;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class SummonStone extends BaseAbility {

    public SummonStone() {
        super();
        ability = Ability.SUMMON_STONE;
        castItem = new DvzItem(Material.DIAMOND_PICKAXE, -1, -1);
        castItem.addEnchantment(Enchantment.DIG_SPEED, 3);

        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR}));
    }


    @Override
    public void castAbility(final Player player, final Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        new BukkitRunnable() {
            int itterations = 0;
            @Override
            public void run() {
                itterations++;
                player.getInventory().addItem(new CWItem(Material.SMOOTH_BRICK, 8, (byte)2, Ability.REINFORCE.getAbilityClass().getCastItem().getName()));
                triggerLoc.getWorld().playSound(triggerLoc, Sound.ITEM_PICKUP, 0.5f, 2f);
                ParticleEffect.FIREWORKS_SPARK.display(0.2f, 0.8f, 0.2f, 0.02f, 5, player.getLocation().add(0,1,0), 20);
                player.updateInventory();
                if (itterations >= 8) {
                    cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 5);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
