package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

public class Rapidfire extends MobAbility {

    public Rapidfire() {
        super();
        this.ability = Ability.RAPIDFIRE;
        castItem = new CWItem(Material.BOW, 1, (short) 0, displayName);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        final DvZ dvzf = dvz;
        //ArrowsPerSec and Duration
        Bukkit.getScheduler().scheduleSyncDelayedTask(dvzf,
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (int x = 1; x <= getIntOption("arrowspersec"); x++) {
                            player.launchProjectile(Arrow.class);
                        }
                    }
                }, getIntOption("duration") * 1000);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
