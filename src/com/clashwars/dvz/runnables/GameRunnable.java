package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameManager;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//This runnable runs half the speed! (2 tick delay)
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
        ticks++;
        if (!gm.isStarted()) {
            return;
        }

        //Dragonslayer effect
        if (gm.getDragonSlayer() != null) {
            ParticleEffect.SPELL_WITCH.display(0.2f, 0.4f, 0.2f, 0.01f, 5, gm.getDragonSlayer().getLocation());
        }

        //Saving
        if (ticks % 600 == 0) {
            Bukkit.broadcastMessage(Util.formatMsg("&7Saving all data..."));
            dvz.getPM().savePlayers();
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
                List<Entity> nearbyEntities = CWUtil.getNearbyEntities(center, 75f, Arrays.asList(new EntityType[] {EntityType.PLAYER}));
                int dwarfCount = 0;
                for (Entity e : nearbyEntities) {
                    CWPlayer cwp = dvz.getPM().getPlayer((Player)e);
                    if (cwp.isDwarf()) {
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
                Bukkit.broadcastMessage(Util.formatMsg("&6The first day has passed by..."));
                Bukkit.broadcastMessage(Util.formatMsg("&6If you die now you'll become a monster!"));
            }
            if (gm.getState() == GameState.DAY_TWO) {
                gm.setState(GameState.DRAGON);
                gm.createDragon(false);
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
