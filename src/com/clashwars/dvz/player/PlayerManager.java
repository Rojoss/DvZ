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

    public List<UUID> suicidePlayers = new ArrayList<UUID>();

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
            if (type == null) {
                continue;
            }
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
                case BAKER:
                    ws = new BakerWorkshop(uuid, cfgWorkshops.get(uuid));
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
            WorkShop ws = null;
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
                case BAKER:
                    ws = new BakerWorkshop(uuid, new WorkShopData());
                    break;
                default:
                    break;
            }
            if (ws != null) {
                workshops.put(uuid, ws);
            }
            return ws;
        }
    }

    public boolean hasWorkshop(Player p) {
        if (workshops.containsKey(p.getUniqueId())) {
            return true;
        }
        return false;
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

    public List<CWPlayer> getPlayers(ClassType classType, boolean onlineOnly) {
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
            if ((cwp.getPlayerClass() == DvzClass.MONSTER && classType == ClassType.MONSTER) || (cwp.getPlayerClass() == DvzClass.DWARF && classType == ClassType.DWARF)) {
                playersByClass.add(cwp);
                continue;
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
            pcfg.save();

            WorkShop ws = getWorkshop(dvz.getServer().getPlayer(uuid));
            if (ws != null) {
                ws.onRemove();
            }
            wsCfg.removeWorkShop(uuid);
            wsCfg.save();
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


    public boolean removeWorkshop(Player player) {
        if (workshops.containsKey(player.getUniqueId())) {
            WorkShop ws = workshops.get(player.getUniqueId());
            ws.onRemove();
            ws.remove();
            wsCfg.removeWorkShop(player.getUniqueId());
            workshops.remove(player.getUniqueId());
            return true;
        }
        return false;
    }

    public void removeWorkshops(boolean fromConfig) {
        for (WorkShop ws : workshops.values()) {
            ws.onRemove();
        }
        workshops.clear();
        if (fromConfig == true) {
            wsCfg.WORKSHOPS.clear();
            wsCfg.save();
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
            if (ws.getCuboid().contains(location)) {
                return ws;
            }
        }
        return null;
    }

}
