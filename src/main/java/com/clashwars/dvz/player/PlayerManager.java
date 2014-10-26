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

    public void populate() {
        for (UUID uuid : pcfg.getPlayerData().keySet()) {
            players.put(uuid, new CWPlayer(uuid, pcfg.getPlayerData().get(uuid)));
        }
    }

    public CWPlayer getPlayer(Player p) {
        UUID uuid = p.getUniqueId();
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else if (pcfg.PLAYERS.containsKey(uuid.toString())) {
            return new CWPlayer(uuid, DvZ.inst().getGson().getGson().fromJson(pcfg.PLAYERS.get(uuid), PlayerData.class));
        } else {
            return new CWPlayer(uuid, new PlayerData());
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

    public void savePlayer(UUID uuid, PlayerData pd) {
        pcfg.setPlayer(uuid, pd);
    }

    public void savePlayer(CWPlayer p) {
        pcfg.setPlayer(p.getUUID(), p.getPlayerData());
    }

    public void savePlayer(Player p) {
        pcfg.setPlayer(p.getUniqueId(), getPlayer(p).getPlayerData());
    }

    public void removePlayer(UUID uuid) {
        pcfg.removePlayer(uuid);
    }

    public void removePlayer(CWPlayer p) {
        pcfg.removePlayer(p.getUUID())
    }

    public void removePlayer(Player p) {
        pcfg.removePlayer(p.getUniqueId());
    }

    public void savePlayers() {
        for(UUID uuid : players.keySet()) {
            pcfg.setPlayer(uuid, getPlayer(uuid).getPlayerData());
        }
    }

    public void clearPlayers(boolean mode) {
        players.clear();
        if(mode = true) {
            pcfg.PLAYERS.clear();
        }
    }

}
