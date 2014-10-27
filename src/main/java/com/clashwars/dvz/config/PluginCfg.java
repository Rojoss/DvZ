package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

public class PluginCfg extends EasyConfig {

    public int XP_NEEDED_TO_LVL = 500;

    public PluginCfg(String fileName) {
        this.setFile(fileName);
    }
}
