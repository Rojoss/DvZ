package com.clashwars.dvz.abilities.monsters.slime;

import com.clashwars.cwcore.damage.types.CustomDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.abilities.dwarves.bonus.Forcefield;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SlimeSpray extends BaseAbility {

    public SlimeSpray() {
        super();
        ability = Ability.SLIME_SPRAY;
        castItem = new DvzItem(Material.SLIME_BALL, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (onCooldown(player)) {
            return;
        }

        new BukkitRunnable() {
            int count = 0;
            List<Item> slimeballs = new ArrayList<Item>();
            @Override
            public void run() {
                final Item slimeball = player.getWorld().dropItem(player.getLocation().add(0,0.5f,0), new ItemStack(Material.SLIME_BALL));
                slimeball.setVelocity(player.getLocation().add(0,0.5f,0).getDirection());
                slimeball.setPickupDelay(9999);
                slimeballs.add(slimeball);
                ParticleEffect.SLIME.display(0.2f, 0.2f, 0.2f, 0, 10, player.getLocation().add(0,0.5f,0));
                player.getWorld().playSound(player.getLocation(), Sound.SLIME_ATTACK, CWUtil.randomFloat(0.2f, 0.6f), CWUtil.randomFloat(1.6f, 2));

                new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        count++;
                        if (slimeball.isOnGround() || count > 10) {
                            ParticleEffect.SLIME.display(0.5f, 0.5f, 0.5f, 0, 20, slimeball.getLocation());
                            slimeball.remove();
                            List<Player> nearby = CWUtil.getNearbyPlayers(slimeball.getLocation(), 2);
                            for (Player p : nearby) {
                                if (!dvz.getPM().getPlayer(p).isDwarf()) {
                                    continue;
                                }
                                if (Forcefield.inForcefield(p.getLocation())) {
                                    continue;
                                }
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int)dvz.getGM().getMonsterPower(40, 100), 1), true);
                                new AbilityDmg(p, 1, Ability.SLIME_SPRAY);
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(dvz, 5, 5);

                count++;
                if (count >= dvz.getGM().getMonsterPower(1, 15)) {
                    cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 3);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
