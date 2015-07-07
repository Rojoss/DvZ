package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.*;

public class ReleasePlayersCfg extends EasyConfig {

    public List<String> PLAYERS = new ArrayList<String>();

    public ReleasePlayersCfg(String fileName) {
        this.setFile(fileName);
    }

    public boolean addUser(UUID uuid) {
        if (PLAYERS.contains(uuid.toString())) {
            return false;
        } else {
            PLAYERS.add(uuid.toString());
            save();
            return true;
        }
    }
}
