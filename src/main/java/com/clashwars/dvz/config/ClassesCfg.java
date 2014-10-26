package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.HashMap;

public class ClassesCfg extends EasyConfig {

    public HashMap<String, Double> WEIGHTS = new HashMap<String, Double>();

    public ClassesCfg(String fileName) {
        this.setFile(fileName);
    }
}
