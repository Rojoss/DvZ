package com.clashwars.dvz.runnables;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.monsters.silverfish.Infest;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InfestRunnable extends BukkitRunnable {

    private DvZ dvz;
    public Player player;
    public CWPlayer cwp;
    public Player target;
    public CWPlayer cwt;

    public Long startTime;

    public InfestRunnable(Player player, Player target) {
        dvz = DvZ.inst();
        this.player = player;
        cwp = dvz.getPM().getPlayer(player);

        this.target = target;
        cwt = dvz.getPM().getPlayer(target);

        startTime = System.currentTimeMillis();
    }


    @Override
    public void run() {
        if (player == null || target == null) {
            return;
        }

        if (player.isDead() || target.isDead() || cwp.getPlayerClass() != DvzClass.SILVERFISH) {
            ((Infest)Ability.INFEST.getAbilityClass()).leaveBody(player.getUniqueId());
            return;
        }

        if (player.isSneaking()) {
            ((Infest)Ability.INFEST.getAbilityClass()).leaveBody(player.getUniqueId());
            return;
        }

        player.teleport(target);
        player.setVelocity(target.getVelocity());
    }
}
