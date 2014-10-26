package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.config.PlayerCfg;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {

    private DvZ dvz;
    private PlayerCfg pcfg;

    Map<UUID, CWPlayer> players = new HashMap<UUID, CWPlayer>();

    public PlayerManager(DvZ dvz, PlayerCfg pcfg) {
        this.dvz = dvz;
        this.pcfg = pcfg;
        populate();
    }

    private void populate() {
        for (UUID uuid : pcfg.getPlayerData().keySet()) {
            players.put(uuid, new CWPlayer(uuid, pcfg.getPlayerData().get(uuid)));
        }
    }

    public CWPlayer getPlayer(Player p) {
        UUID uuid = p.getUniqueId();
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else if (pcfg.PLAYERS.containsKey(uuid.toString())) {
            CWPlayer cw = new CWPlayer(uuid, dvz.getGson().fromJson(pcfg.PLAYERS.get(uuid), PlayerData.class));
            players.put(cw.getUUID(), cw);
            return cw;
        } else {
            CWPlayer cwp = new CWPlayer(uuid, new PlayerData());
            players.put(cwp.getUUID(), cwp);
            return cwp;
        }
    }

    public CWPlayer getPlayer(String name) {
        return getPlayer(dvz.getServer().getPlayer(name));
    }

    public CWPlayer getPlayer(UUID uuid) {
        return getPlayer(dvz.getServer().getPlayer(uuid));
    }

    public Map<UUID, CWPlayer> getPlayers(){
        return players;
    }

    public void savePlayers() {
        for(CWPlayer p : players.values()) {
            p.savePlayer();
        }
    }

    public void removePlayer(UUID uuid, boolean fromConfig) {
        players.remove(uuid);
        if (fromConfig = true) {
            pcfg.removePlayer(uuid);
        }
    }

    public void removePlayer(CWPlayer p, boolean fromConfig) {
        removePlayer(p.getUUID(), fromConfig);
    }

    public void removePlayer(Player p, boolean fromConfig) {
        removePlayer(p.getPlayer(), fromConfig);
    }

    public void removePlayers(boolean fromConfig) {
        players.clear();
        if(fromConfig = true) {
            pcfg.PLAYERS.clear();
        }
    }

}
