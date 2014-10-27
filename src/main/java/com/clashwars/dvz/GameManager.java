package com.clashwars.dvz;

import com.clashwars.dvz.config.GameCfg;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class GameManager {

    private DvZ dvz;
    private GameCfg gCfg;

    public GameManager(DvZ dvz) {
        this.dvz = dvz;
        gCfg = dvz.getGameCfg();
    }



    public void openGame() {

    }


    public void startGame() {

    }


    public void createDragon() {

    }


    public void releaseMonsters() {

    }


    public void captureWall() {

    }


    public void stopGame(boolean force, String reason) {

    }


    public boolean joinGame(Player player) {

        return false;
    }




    public World getWorld() {
        return dvz.getServer().getWorld(gCfg.WORLD);
    }


    public GameState getState() {
        return GameState.valueOf(gCfg.GAME__STATE);
    }

    public void setState(GameState state) {
        gCfg.GAME__STATE = state.toString();
        gCfg.save();
    }

    public boolean isStarted() {
        return (getState() != GameState.CLOSED && getState() != GameState.ENDED);
    }

    public boolean isDwarves() {
        return (getState() == GameState.DAY_ONE || getState() == GameState.DAY_TWO || getState() == GameState.NIGHT_ONE);
    }

    public boolean isMonsters() {
        return (getState() == GameState.MONSTERS || getState() == GameState.MONSTERS_WALL);
    }


    public int getSpeed() {
        return gCfg.GAME__SPEED;
    }

    public void setSpeed(int speed) {
        gCfg.GAME__SPEED = speed;
        gCfg.save();
    }
}
