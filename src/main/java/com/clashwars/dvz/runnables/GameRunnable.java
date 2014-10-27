package com.clashwars.dvz.runnables;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {

    private DvZ dvz;
    private GameManager gm;


    public GameRunnable(DvZ dvz) {
        this.dvz = dvz;
        this.gm = dvz.getGM();
    }

    @Override
    public void run() {
        Long time = gm.getWorld().getTime();
        if (gm.getSpeed() != 0) {
            gm.getWorld().setTime(time + gm.getSpeed());
        }
        if (time > 14000 && time < 22500) {
            if (gm.getState() == GameState.DAY_ONE) {
                gm.setState(GameState.NIGHT_ONE);
                Bukkit.broadcastMessage(Util.formatMsg("&6The first day has passed by..."));
                Bukkit.broadcastMessage(Util.formatMsg("&6One full day remaining to prepare!"));
            }
            if (gm.getState() == GameState.DAY_TWO) {
                gm.setState(GameState.DRAGON);
                //TODO: Create dragon etc...
                Bukkit.broadcastMessage("Dragon...");
            }
        } else if (time > 22500 || time < 14000) {
            if (gm.getState() == GameState.NIGHT_ONE) {
                gm.setState(GameState.DAY_TWO);
                Bukkit.broadcastMessage(Util.formatMsg("&6The sun is rising..."));
                Bukkit.broadcastMessage(Util.formatMsg("&6This is the last day you can prepare for the fight!"));
            }
        }
    }
}
