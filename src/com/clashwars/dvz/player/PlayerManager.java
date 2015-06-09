package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private DvZ dvz;
    private PlayerCfg pcfg;

    private Map<UUID, CWPlayer> players = new HashMap<UUID, CWPlayer>();
    public Map<DvzClass, Integer> fakePlayers = new HashMap<DvzClass, Integer>();

    public List<UUID> suicidePlayers = new ArrayList<UUID>();

    public PlayerManager(DvZ dvz) {
        this.dvz = dvz;
        this.pcfg = dvz.getPlayerCfg();
        populate();
    }


    private void populate() {
        Map<UUID, PlayerData> cfgPlayers = pcfg.getPlayers();
        for (UUID uuid : cfgPlayers.keySet()) {
            players.put(uuid, new CWPlayer(uuid, cfgPlayers.get(uuid)));
            players.get(uuid).onClassLoad();
        }
    }


    public CWPlayer getPlayer(OfflinePlayer p) {
        return getPlayer(p.getUniqueId());
    }

    public CWPlayer getPlayer(String name) {
        return getPlayer(dvz.getServer().getOfflinePlayer(name));
    }

    public CWPlayer getPlayer(UUID uuid) {
        if (players.containsKey(uuid)) {
            return players.get(uuid);
        } else if (pcfg.PLAYERS.containsKey(uuid.toString())) {
            CWPlayer cwp = new CWPlayer(uuid, pcfg.getPlayer(uuid));
            players.put(uuid, cwp);
            return cwp;
        } else {
            CWPlayer cwp = new CWPlayer(uuid, new PlayerData());
            players.put(uuid, cwp);
            return cwp;
        }
    }

    public Map<UUID, CWPlayer> getPlayers() {
        return players;
    }

    public List<CWPlayer> getPlayers(boolean onlineOnly) {
        List<CWPlayer> playerList = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.isOnline()) {
                playerList.add(cwp);
            }
        }
        return playerList;
    }

    public List<CWPlayer> getPlayers(ClassType classType, boolean onlineOnly, boolean includeBase) {
        List<CWPlayer> playersByClass = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.getPlayerClass() == null) {
                continue;
            }
            if (onlineOnly && !cwp.isOnline()) {
                continue;
            }
            if (cwp.getPlayerClass().getType() == classType) {
                playersByClass.add(cwp);
                continue;
            }
            if (includeBase) {
                if ((cwp.getPlayerClass() == DvzClass.MONSTER && classType == ClassType.MONSTER) || (cwp.getPlayerClass() == DvzClass.DWARF && classType == ClassType.DWARF)) {
                    playersByClass.add(cwp);
                    continue;
                }
            }
        }
        return playersByClass;
    }

    public List<CWPlayer> getPlayers(DvzClass dvzClass, boolean onlineOnly) {
        List<CWPlayer> playersByClass = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.getPlayerClass() == null) {
                continue;
            }
            if (onlineOnly && !cwp.isOnline()) {
                continue;
            }
            if (cwp.getPlayerClass() == dvzClass) {
                playersByClass.add(cwp);
            }
        }
        return playersByClass;
    }

    public List<CWPlayer> getPlayingPlayers(boolean onlineOnly) {
        List<CWPlayer> playingPlayers = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.getPlayerClass() == null) {
                continue;
            }
            if (onlineOnly && !cwp.isOnline()) {
                continue;
            }
            if (cwp.getPlayerClass().getType() == ClassType.DWARF || cwp.getPlayerClass().getType() == ClassType.MONSTER) {
                playingPlayers.add(cwp);
            }
        }
        return playingPlayers;
    }


    public void savePlayers() {
        for (CWPlayer cwp : players.values()) {
            cwp.savePlayer();
        }
    }


    public void removePlayer(UUID uuid, boolean fromConfig) {
        players.remove(uuid);
        if (fromConfig == true) {
            pcfg.removePlayer(uuid);
            pcfg.save();
        }
    }

    public void removePlayer(CWPlayer cwp, boolean fromConfig) {
        removePlayer(cwp.getUUID(), fromConfig);
    }

    public void removePlayer(Player p, boolean fromConfig) {
        removePlayer(p.getPlayer(), fromConfig);
    }


    public void removePlayers(boolean fromConfig) {
        players.clear();
        if (fromConfig == true) {
            pcfg.PLAYERS.clear();
            pcfg.save();
        }
    }
}
