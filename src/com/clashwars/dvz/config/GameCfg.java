package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.HashMap;

public class GameCfg extends EasyConfig {

    public String GAME__STATE = "CLOSED";
    public int GAME__SPEED = 0;
    public String GAME__DRAGON_PLAYER = "";
    public String GAME__DRAGON_TYPE = "";
    public String GAME__DRAGON_SLAYER = "";
    public HashMap<String, Integer> STORAGE_PRODUCTS = new HashMap<String, Integer>();

    public GameCfg(String fileName) {
        this.setFile(fileName);
    }
}
