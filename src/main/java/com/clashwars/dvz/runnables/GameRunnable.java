package com.clashwars.dvz.runnables;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
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
        gm.getWorld().setTime(time + gm.getSpeed());
        if (time > 14000 && time < 22500) {
            if (gm.getState() == GameState.DAY_ONE) {
                gm.setState(GameState.NIGHT_ONE);
                Bukkit.broadcastMessage("First night..");
            }
            if (gm.getState() == GameState.DAY_TWO) {
                gm.setState(GameState.DRAGON);
                Bukkit.broadcastMessage("Dragon...");
            }
        } else if (time > 22500 || time < 14000) {
            if (gm.getState() == GameState.NIGHT_ONE) {
                gm.setState(GameState.DAY_TWO);
                Bukkit.broadcastMessage("Day two..");
            }
            //TODO: Remove this
            if (gm.getState() == GameState.DRAGON) {
                gm.setState(GameState.DAY_ONE);
                Bukkit.broadcastMessage("Day one again.. (For debug)");
            }
        }
    }
}
