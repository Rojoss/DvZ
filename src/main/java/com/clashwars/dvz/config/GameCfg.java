package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.GameState;

public class GameCfg extends EasyConfig {

    public String WORLD = "world";
    public String GAME__STATE = "CLOSED";
    public int GAME__SPEED = 0;

    public GameCfg(String fileName) {
        this.setFile(fileName);
    }
}
