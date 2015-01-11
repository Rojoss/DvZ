package com.clashwars.dvz.runnables;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AuraRunnable extends BukkitRunnable {

    private DvZ dvz;

    public AuraRunnable(DvZ dvz) {
        this.dvz = dvz;
    }

    @Override
    public void run() {
        for (CWPlayer cwp : dvz.getPM().getPlayers(DvzClass.VILLAGER, true)) {
            for (Entity e : cwp.getPlayer().getNearbyEntities(10, 10, 10)) {
                if(e instanceof Player) {
                    Player p = (Player) e;
                    if(dvz.getPM().getPlayer(p).isMonster()) {
                        p.setHealth(p.getHealth() + 1);
                    }
                }
            }
        }

        for (CWPlayer cwp : dvz.getPM().getPlayers(DvzClass.WITCH, true)) {
            for (Entity e : cwp.getPlayer().getNearbyEntities(10, 10, 10)) {
                if(e instanceof Player) {
                    Player p = (Player) e;
                    if(dvz.getPM().getPlayer(p).isDwarf()) {
                        p.setHealth(p.getHealth() - 1);
                    }
                }
            }
        }

    }

}
