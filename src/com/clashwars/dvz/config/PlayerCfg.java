package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCfg extends EasyConfig {

    public HashMap<String, String> PLAYERS = new HashMap<String, String>();

    public PlayerCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<UUID, PlayerData> getPlayers() {
        Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
        for (String key : PLAYERS.keySet()) {
            players.put(UUID.fromString(key), DvZ.inst().getGson().fromJson(PLAYERS.get(key), PlayerData.class));
        }
        return players;
    }

    public PlayerData getPlayer(UUID uuid) {
        return DvZ.inst().getGson().fromJson(PLAYERS.get(uuid.toString()), PlayerData.class);
    }

    public void setPlayer(UUID uuid, PlayerData pd) {
        PLAYERS.put(uuid.toString(), DvZ.inst().getGson().toJson(pd, PlayerData.class));
        save();
    }

    public void removePlayer(UUID uuid) {
        PLAYERS.remove(uuid.toString());
        save();
    }
}
