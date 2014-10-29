package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import com.clashwars.dvz.config.WorkShopCfg;
import com.clashwars.dvz.workshop.WorkShop;
import com.clashwars.dvz.workshop.WorkShopData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private DvZ dvz;
    private PlayerCfg pcfg;
    private WorkShopCfg wsCfg;

    Map<UUID, CWPlayer> players = new HashMap<UUID, CWPlayer>();
    Map<UUID, WorkShop> workshops = new HashMap<UUID, WorkShop>();

    public PlayerManager(DvZ dvz) {
        this.dvz = dvz;
        this.pcfg = dvz.getPlayerCfg();
        this.wsCfg = dvz.getWSCfg();
        populate();
    }


    private void populate() {
        Map<UUID, PlayerData> cfgPlayers = pcfg.getPlayers();
        for (UUID uuid : cfgPlayers.keySet()) {
            players.put(uuid, new CWPlayer(uuid, cfgPlayers.get(uuid)));
        }
        Map<UUID, WorkShopData> cfgWorkshops = wsCfg.getWorkShops();
        for (UUID uuid : cfgWorkshops.keySet()) {
            workshops.put(uuid, new WorkShop(uuid, cfgWorkshops.get(uuid)));
        }
    }


    public CWPlayer getPlayer(Player p) {
        UUID uuid = p.getUniqueId();
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

    public CWPlayer getPlayer(String name) {
        return getPlayer(dvz.getServer().getPlayer(name));
    }

    public CWPlayer getPlayer(UUID uuid) {
        return getPlayer(dvz.getServer().getPlayer(uuid));
    }


    public WorkShop getWorkshop(Player p) {
        UUID uuid = p.getUniqueId();
        if (workshops.containsKey(uuid)) {
            return workshops.get(uuid);
        } else if (wsCfg.WORKSHOPS.containsKey(uuid.toString())) {
            WorkShop wsd = new WorkShop(uuid, wsCfg.getWorkShop(uuid));
            workshops.put(uuid, wsd);
            return wsd;
        } else {
            WorkShop wsd = new WorkShop(uuid, new WorkShopData());
            workshops.put(uuid, wsd);
            return wsd;
        }
    }


    public Map<UUID, CWPlayer> getPlayers(){
        return players;
    }

    public List<CWPlayer> getPlayers(ClassType classType) {
        List<CWPlayer> playersByClass = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.getPlayerClass() == null) {
                continue;
            }
            if (cwp.getPlayerClass().getType() == classType) {
                playersByClass.add(cwp);
            }
        }
        return playersByClass;
    }

    public List<CWPlayer> getPlayers(DvzClass dvzClass) {
        List<CWPlayer> playersByClass = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.getPlayerClass() == null) {
                continue;
            }
            if (cwp.getPlayerClass() == dvzClass) {
                playersByClass.add(cwp);
            }
        }
        return playersByClass;
    }

    public List<CWPlayer> getPlayingPlayers() {
        List<CWPlayer> playingPlayers = new ArrayList<CWPlayer>();
        for (CWPlayer cwp : players.values()) {
            if (cwp.getPlayerClass() == null) {
                continue;
            }
            if (cwp.getPlayerClass().getType() == ClassType.DWARF || cwp.getPlayerClass().getType() == ClassType.MONSTER) {
                playingPlayers.add(cwp);
            }
        }
        return playingPlayers;
    }


    public Map<UUID, WorkShop> getWorkShops(){
        return workshops;
    }


    public void savePlayers() {
        for(CWPlayer cwp : players.values()) {
            cwp.savePlayer();
        }
        for(WorkShop wsd : workshops.values()) {
            wsd.save();
        }
    }


    public void removePlayer(UUID uuid, boolean fromConfig) {
        players.remove(uuid);
        workshops.remove(uuid);
        if (fromConfig == true) {
            pcfg.removePlayer(uuid);
            wsCfg.removeWorkShop(uuid);
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
        workshops.clear();
        if (fromConfig == true) {
            pcfg.PLAYERS.clear();
            wsCfg.WORKSHOPS.clear();
        }
    }


    //Returns 1 if in own workshop 0 if it's in another workshop and -1 if it's not in a workshop.
    public int locIsOwnWorkshop(UUID uuid, Location location) {
        WorkShop ws = locGetWorkShop(location);
        if (ws == null) {
            return -1;
        }
        if (ws.isOwner(uuid)) {
            return 1;
        } else {
            return 0;
        }
    }

    public WorkShop locGetWorkShop(Location location) {
        for (WorkShop ws : getWorkShops().values()) {
            if (ws.isLocWithinWorkShop(location)) {
                return ws;
            }
        }
        return null;
    }

}
