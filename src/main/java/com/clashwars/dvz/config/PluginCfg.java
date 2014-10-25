package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

public class PluginCfg extends EasyConfig {

    public int SOME_CONFIG_OPTION = 99;

    public PluginCfg(String fileName) {
        this.setFile(fileName);
    }
}
