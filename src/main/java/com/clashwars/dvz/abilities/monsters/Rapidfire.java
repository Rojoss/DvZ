package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;

public class Rapidfire extends MobAbility {

    public Rapidfire() {
        super();
        ability = Ability.RAPIDFIRE;
        castItem = new CWItem(Material.BOW, 1, (short) 0, displayName);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        //TODO: Add particle and sound effects.

        new BukkitRunnable() {
            int arrows = getIntOption("arrows");
            Double m = getDoubleOption("randomoffset");
            int amt = getIntOption("arrowspershot");

            @Override
            public void run() {
                for (int i = 0; i < amt; i++) {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    arrow.setVelocity(arrow.getVelocity().add(new Vector((CWUtil.randomFloat()-0.5f) * m, (CWUtil.randomFloat()-0.5f) * m, (CWUtil.randomFloat()-0.5f) * m)));
                    arrows--;
                    if (arrows <= 0) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(dvz, 0, getIntOption("tickdelay"));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
