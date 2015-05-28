package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class GameRunnable extends BukkitRunnable {

    private DvZ dvz;
    private GameManager gm;
    private int ticks = 0;


    public GameRunnable(DvZ dvz) {
        this.dvz = dvz;
        this.gm = dvz.getGM();
    }

    @Override
    public void run() {
        if (!gm.isStarted()) {
            return;
        }
        if (gm.getDragonSlayer() != null) {
            ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.01f, 5, gm.getDragonSlayer().getLocation());
        }

        ticks++;
        if (ticks >= 1200) {
            dvz.getPM().savePlayers();
            ticks = 0;
        }

        if (gm.isMonsters()) {
            return;
        }
        Long time = gm.getUsedWorld().getTime();
        if (gm.getSpeed() != 0) {
            gm.getUsedWorld().setTime(time + gm.getSpeed());
        }
        if (time > 14000 && time < 22500) {
            if (gm.getState() == GameState.DAY_ONE) {
                gm.setState(GameState.NIGHT_ONE);
                Bukkit.broadcastMessage(Util.formatMsg("&6The first day has passed by..."));
                Bukkit.broadcastMessage(Util.formatMsg("&6If you die now you'll become a monster!"));
            }
            if (gm.getState() == GameState.DAY_TWO) {
                gm.setState(GameState.DRAGON);
                gm.createDragon();
            }
        } else if (time > 22500 || time < 14000) {
            if (gm.getState() == GameState.NIGHT_ONE) {
                gm.setState(GameState.DAY_TWO);
                Bukkit.broadcastMessage(Util.formatMsg("&6The sun is rising..."));
                Bukkit.broadcastMessage(Util.formatMsg("&6This is the last day you can prepare for the fight!"));
            }
            if (gm.getState() == GameState.NIGHT_TWO) {
                gm.setState(GameState.DAY_TWO);
                Bukkit.broadcastMessage(Util.formatMsg("&6The sun is rising..."));
                Bukkit.broadcastMessage(Util.formatMsg("&6There has been no dragon. &8(&7You might die now..&8)"));
                gm.releaseMonsters(true);
            }
        }
    }
}
