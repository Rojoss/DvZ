package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.monsters.enderman.Pickup;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PickupRunnable extends BukkitRunnable {

    private DvZ dvz;
    private Vector offset;
    private double depth;

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
        depth = offset.getX();
        offset.setX(0);
        maxTime = (int)dvz.getGM().getMonsterPower(40, 100);
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

        String timeLeft = CWUtil.formatTime((int)((float)(maxTime - timer) / 20 * 1000), "&7%S&8.&7%%%&ds");
        CWUtil.sendActionBar(target, CWUtil.integrateColor("&5&l>> &dPicked up by an enderman! &8(&7" + timeLeft + "&8) &5&l<<"));
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&5&l>> &dPicked up " + target.getName() + "! &8(&7" + timeLeft + "&8) &5&l<<"));

        target.teleport(player.getLocation().add(offset).add(player.getLocation().getDirection().multiply(depth)));
        target.setVelocity(player.getVelocity());
        timer++;
    }

}
