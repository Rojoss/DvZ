package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.DvzMap;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//This runnable runs half the speed! (2 tick delay)
public class GameRunnable extends BukkitRunnable {

    private DvZ dvz;
    private GameManager gm;
    private int ticks = 0;
    private long lastSave = 0;


    public GameRunnable(DvZ dvz) {
        this.dvz = dvz;
        this.gm = dvz.getGM();
    }

    @Override
    public void run() {
        ticks++;
        if (!gm.isStarted()) {
            return;
        }

        //Dragonslayer effect
        if (gm.getDragonSlayer() != null) {
            if (gm.getDragonSlayer().getGameMode() == GameMode.SURVIVAL) {
                if (dvz.getPM().getPlayer(gm.getDragonSlayer()).getPlayerClass() != DvzClass.SILVERFISH) {
                    ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.01f, 5, gm.getDragonSlayer().getLocation());
                }
            }
        }

        //Saving
        if (ticks % 600 == 0) {
            dvz.getPM().savePlayers();
            dvz.getServer().savePlayers();
            DvzMap dvzMap = dvz.getMM().getActiveMap();
            if (dvzMap != null && dvzMap.isActive() && dvzMap.isLoaded() && dvzMap.getWorld() != null) {
                dvzMap.getWorld().save();
            }

            //Game saving
            if (dvz.getGM().isStarted()) {
                if (lastSave <= 0) {
                    lastSave = System.currentTimeMillis() - 30000;
                }
                dvz.getSM().changeLocalStatVal(StatType.GENERAL_GAME_TIME, System.currentTimeMillis() - lastSave);
                lastSave = System.currentTimeMillis();
            }
        }

        if (gm.isMonsters()) {
            //Shrine damage
            if (ticks % 30 != 0) {
                return;
            }
            Set<ShrineBlock> shrineBlocks = new HashSet<ShrineBlock>();
            Location center = null;
            if (dvz.getGM().getState() == GameState.MONSTERS) {
                shrineBlocks = dvz.getGM().getShrineBlocks(ShrineType.WALL);
                center = dvz.getMM().getActiveMap().getCuboid("shrinewall").getCenterLoc();
            } else if (dvz.getGM().getState() == GameState.MONSTERS_WALL) {
                shrineBlocks = dvz.getGM().getShrineBlocks(ShrineType.KEEP_1);
                center = dvz.getMM().getActiveMap().getCuboid("shrine1keep").getCenterLoc();
            } else if (dvz.getGM().getState() == GameState.MONSTERS_KEEP) {
                shrineBlocks = dvz.getGM().getShrineBlocks(ShrineType.KEEP_1);
                center = dvz.getMM().getActiveMap().getCuboid("shrine2keep").getCenterLoc();
            }

            if (shrineBlocks != null && center != null && shrineBlocks.size() > 0) {
                List<Player> nearbyPlayers = CWUtil.getNearbyPlayers(center, 75f);
                int dwarfCount = 0;
                for (Player p : nearbyPlayers) {
                    if (dvz.getPM().getPlayer(p).isDwarf()) {
                        dwarfCount++;
                    }
                }
                if (dwarfCount <= Math.round(dvz.getPM().getPlayers(ClassType.DWARF, true, true).size() * 0.2f)) {
                    for (ShrineBlock shrineBlock : shrineBlocks) {
                        if (shrineBlock != null && !shrineBlock.isDestroyed()) {
                            shrineBlock.damage();
                        }
                    }
                }
            }
            return;
        }

        //Game loop
        Long time = gm.getUsedWorld().getTime();
        if (gm.getSpeed() != 0) {
            gm.getUsedWorld().setTime(time + gm.getSpeed());
        }
        if (time > 14000 && time < 22500) {
            if (gm.getState() == GameState.DAY_ONE) {
                gm.setState(GameState.NIGHT_ONE);
                Util.broadcast("&6&lThe first day has passed by...");
            }
            if (gm.getState() == GameState.DAY_TWO) {
                gm.setState(GameState.DRAGON);
                gm.createDragon(false);
            }
        } else if (time > 22500 || time < 14000) {
            if (gm.getState() == GameState.NIGHT_ONE) {
                gm.setState(GameState.DAY_TWO);
                Util.broadcast("&6&lThe sun is rising...");
                Util.broadcast("&6&lThis is the last day you can prepare for the fight!");
            }
            if (gm.getState() == GameState.NIGHT_TWO) {
                gm.setState(GameState.DAY_TWO);
                Util.broadcast("&4&lThere has been no dragon. &8(&cYou might die now..&8)");
                gm.releaseMonsters(true);
            }
        }
    }
}
