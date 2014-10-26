package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvZClass;
import com.clashwars.dvz.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCfg extends EasyConfig {

    private DvZ dvz;
    private DvZClass dc;

    public HashMap<String, String> PLAYERS = new HashMap<String, String>();

    public PlayerCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<UUID, PlayerData> getPlayerData() {
        Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
        for (String key : PLAYERS.keySet()) {
            players.put(UUID.fromString(key), dc.getGson().fromJson(PLAYERS.get(key), PlayerData.class));
        }
        return players;
    }

}
