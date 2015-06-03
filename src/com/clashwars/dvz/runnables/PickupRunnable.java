package com.clashwars.dvz.runnables;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.monsters.Pickup;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PickupRunnable extends BukkitRunnable {

    private DvZ dvz;
    private Vector offset;

    private Player player;
    private CWPlayer cwp;

    private Player target;
    private CWPlayer cwt;

    private int timer = 0;
    private int maxTime = 20;
    public boolean died = false;


    public PickupRunnable(DvZ dvz, Player player, Player target, Vector offset) {
        this.dvz = dvz;
        this.player = player;
        cwp = dvz.getPM().getPlayer(player);
        this.target = target;
        cwt = dvz.getPM().getPlayer(target);
        this.offset = offset;
        maxTime = 160;
    }


    @Override
    public void run() {
        if (player == null || target == null) {
            return;
        }
        if (died || player.isDead() || target.isDead() || cwp.getPlayerClass() != DvzClass.ENDERMAN) {
            ((Pickup)Ability.PICKUP.getAbilityClass()).dropTarget(player);
            cancel();
            return;
        }

        if (player.isSneaking()) {
            ((Pickup)Ability.PICKUP.getAbilityClass()).dropTarget(player);
            cancel();
            return;
        }

        if (timer > maxTime) {
            ((Pickup)Ability.PICKUP.getAbilityClass()).dropTarget(player);
            cancel();
            return;
        }

        target.teleport(player.getLocation().add(offset));
        target.setVelocity(player.getVelocity());
        timer++;
    }

}
