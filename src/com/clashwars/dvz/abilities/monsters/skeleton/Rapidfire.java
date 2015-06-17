package com.clashwars.dvz.abilities.monsters.skeleton;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;

public class Rapidfire extends BaseAbility {

    public Rapidfire() {
        super();
        ability = Ability.RAPIDFIRE;
        DvzItem bow = new DvzItem(Material.BOW, 1, (short)0, displayName, 1, -1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        castItem = bow;
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        new BukkitRunnable() {
            int arrows = (int)dvz.getGM().getMonsterPower(8, 50);
            float randomoffset = 0.2f;
            int amt = (int)dvz.getGM().getMonsterPower(1, 4);

            @Override
            public void run() {
                for (int i = 0; i < amt; i++) {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(arrow.getVelocity().add(new Vector((CWUtil.randomFloat() - 0.5f) * randomoffset, (CWUtil.randomFloat() - 0.5f) * randomoffset, (CWUtil.randomFloat() - 0.5f) * randomoffset)));

                    ParticleEffect.CRIT.display(0.5f, 0.5f, 0.5f, 0.001f, 1, arrow.getLocation());
                    if (amt == 1 || CWUtil.randomFloat() < 0.5f) {
                        arrow.getWorld().playSound(arrow.getLocation(), Sound.SHOOT_ARROW, 0.3f, 1.2f);
                    }

                    arrows--;
                    if (arrows <= 0) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
