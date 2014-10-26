package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvZClass;
import com.clashwars.dvz.config.PlayerCfg;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private DvZ dvz;
    private DvZClass dc;
    private PlayerCfg pcfg;

    Map<UUID, CWPlayer> players = new HashMap<UUID, CWPlayer>();

    public void populate() {
        //Go through all the players in the config
        //Create CWPlayer's of each
        for (UUID uuid : pcfg.getPlayerData().keySet()) {
            players.put(uuid, new CWPlayer(uuid, pcfg.getPlayerData().get(uuid)));
        }
    }

    public CWPlayer getPlayer(Player p) {
        UUID uuid = p.getUniqueId();
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else if (pcfg.PLAYERS.containsKey(uuid.toString())) {
            return new CWPlayer(uuid, dc.getGson().fromJson(pcfg.PLAYERS.get(uuid), PlayerData.class));
        } else {
            return new CWPlayer(uuid, new PlayerData());
        }
    }

    public CWPlayer getPlayer(String name) {
        Player p = dvz.getServer().getPlayer(name);
        UUID uuid = p.getUniqueId();
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else if (pcfg.PLAYERS.containsKey(uuid.toString())) {
            return new CWPlayer(uuid, dc.getGson().fromJson(pcfg.PLAYERS.get(uuid), PlayerData.class));
        } else {
            return new CWPlayer(uuid, new PlayerData());
        }
    }

    public Map<UUID, CWPlayer> getPlayers(){
        return players;
    }

}
