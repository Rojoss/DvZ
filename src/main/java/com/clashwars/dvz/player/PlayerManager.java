package com.clashwars.dvz.player;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.PlayerCfg;
import com.clashwars.dvz.config.WorkShopCfg;
import com.clashwars.dvz.workshop.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerManager {

    private DvZ dvz;
    private PlayerCfg pcfg;
    private WorkShopCfg wsCfg;

    private Map<UUID, CWPlayer> players = new HashMap<UUID, CWPlayer>();
    public Map<DvzClass, Integer> fakePlayers = new HashMap<DvzClass, Integer>();
    private Map<UUID, WorkShop> workshops = new HashMap<UUID, WorkShop>();

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
            DvzClass type = cfgWorkshops.get(uuid).getType();
            WorkShop ws;
            switch(type) {
                case MINER:
                    ws = new MinerWorkshop(uuid, cfgWorkshops.get(uuid));
                    break;
                case FLETCHER:
                    ws = new FletcherWorkshop(uuid, cfgWorkshops.get(uuid));
                    break;
                case TAILOR:
                    ws = new TailorWorkshop(uuid, cfgWorkshops.get(uuid));
                    break;
                case ALCHEMIST:
                    ws = new AlchemistWorkshop(uuid, cfgWorkshops.get(uuid));
                    break;
                default:
                    ws = new WorkShop(uuid, cfgWorkshops.get(uuid));
            }
            ws.onLoad();
            workshops.put(uuid, ws);
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
        } else {
            DvzClass type = getPlayer(p).getPlayerClass();
            WorkShop ws;
            switch(type) {
                case MINER:
                    ws = new MinerWorkshop(uuid, new WorkShopData());
                    break;
                case FLETCHER:
                    ws = new FletcherWorkshop(uuid, new WorkShopData());
                    break;
                case TAILOR:
                    ws = new TailorWorkshop(uuid, new WorkShopData());
                    break;
                case ALCHEMIST:
                    ws = new AlchemistWorkshop(uuid, new WorkShopData());
                    break;
                default:
                    ws = new WorkShop(uuid, new WorkShopData());
            }
            workshops.put(uuid, ws);
            return ws;
        }
    }


    public Map<UUID, CWPlayer> getPlayers() {
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


    public Map<UUID, WorkShop> getWorkShops() {
        return workshops;
    }


    public void savePlayers() {
        for (CWPlayer cwp : players.values()) {
            cwp.savePlayer();
        }
        for (WorkShop wsd : workshops.values()) {
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
