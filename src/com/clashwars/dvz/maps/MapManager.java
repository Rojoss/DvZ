package com.clashwars.dvz.maps;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.config.MapCfg;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MapManager {

    private DvZ dvz;
    private MapCfg mapCfg;
    private String activeMap;
    private Map<String, DvzMap> maps = new HashMap<String, DvzMap>();

    public MapManager(DvZ dvz) {
        this.dvz = dvz;
        mapCfg = dvz.getMapCfg();
        if (mapCfg.getActiveMap() != null && !mapCfg.getActiveMap().isEmpty()) {
            activeMap = mapCfg.getActiveMap();
        }
        populate();
    }

    private void populate() {
        Long t = System.currentTimeMillis();
        //Go through all maps in the maps directory and load their data or create new data if it doesn't exist yet.
        List<File> mapFolders = CWUtil.getDirectories(new File("plugins/DvZ/maps"));
        Map<String, MapData> mapsInConfig = mapCfg.getMaps();
        for (File mapFolder : mapFolders) {
            String name = mapFolder.getName();
            if (!maps.containsKey(name)) {
                if (mapsInConfig.containsKey(name)) {
                    maps.put(name, new DvzMap(name, mapsInConfig.get(name)));
                } else {
                    maps.put(name, new DvzMap(name, new MapData()));
                }
            } else {
                if (mapsInConfig.containsKey(name)) {
                    maps.get(name).setData(mapsInConfig.get(name));
                } else {
                    maps.remove(name);
                }
            }
        }

        //Load active map in.
        if (activeMap == null || activeMap.isEmpty() || getActiveMap() == null) {
            dvz.log("No active map was found so loading in a random map.");
            loadMap("");
        } else if (!getActiveMap().isLoaded()) {
            if (getActiveMap().isReadyToLoad()) {
                dvz.log("The active map is not loaded anymore. Trying to load it again...");
                WorldCreator wc = new WorldCreator(getActiveMapName());
                wc.createWorld();
                if (!getActiveMap().isLoaded()) {
                    dvz.log("Failed at loading the map. Trying to load a new one in.");
                    loadMap(activeMap);
                }
            } else {
                dvz.log("The active map no longer exists so loading a new one in.");
                loadMap(activeMap);
            }
        }
        dvz.getServer().dispatchCommand(dvz.getServer().getConsoleSender(), "hd reload");
        dvz.log("Active map loaded in successfully!");
        dvz.logTimings("MapManager.populate()", t);
    }

    public boolean removeActiveMap() {
        Long t = System.currentTimeMillis();
        if (getActiveMap() == null || !getActiveMap().isLoaded()) {
            dvz.logTimings("MapManager.removeActiveMap()[no map]", t);
            return false;
        }

        //Kick all death players
        Collection<Player> onlinePlayers = (Collection<Player>)dvz.getServer().getOnlinePlayers();
        for (Player p : onlinePlayers) {
            if (p.isDead() || !p.isValid()) {
                p.kickPlayer(CWUtil.integrateColor("&4&lYou have been kicked because the map is resetting!\n&cWe couldn't teleport you to the lobby for some reason.\n&cYou can try to log back in to fix it."));
            }
        }

        //Kick all players remaining in the map.
        List<Player> players = getActiveMap().getWorld().getPlayers();
        for (Player p : players) {
            p.kickPlayer(CWUtil.integrateColor("&4&lYou have been kicked because the map is resetting!\n&cWe couldn't teleport you to the lobby for some reason.\n&cYou can try to log back in to fix it."));
        }

        //Try unload the map.
        if (getActiveMap().isLoaded()) {
            Bukkit.unloadWorld(getActiveMapName(), false);
        }
        String activeName = getActiveMapName();
        activeMap = null;
        mapCfg.setActiveMap("");

        //Try remove the map.
        if (!CWUtil.deleteFolder(new File(activeName))) {
            dvz.log("Failed to remove the map '" + activeName + "'. The map has been unloaded though so it can be manually deleted.");
            dvz.logTimings("MapManager.removeActiveMap()[failed]", t);
            return false;
        }

        dvz.logTimings("MapManager.removeActiveMap()", t);
        return true;
    }


    public boolean loadMap(String name) {
        Long t = System.currentTimeMillis();
        if (name == null || name.isEmpty()) {
            name = getRandomMapName();
        }
        final String mapName = name;

        //If map is still null it means there are no maps in the map manager.
        if (mapName == null || mapName.isEmpty()) {
            Util.broadcastAdmins(Util.formatMsg("&cFailed to load the map '" + mapName + "'"));
            Util.broadcastAdmins(Util.formatMsg("&cYou probably forgot to add maps to '/plugins/dvz/maps'"));
            dvz.logTimings("MapManager.loadMap()[Invalid map name]", t);
            return false;
        }

        //Set active map and make sure it's a valid map.
        final DvzMap newMap = getMap(mapName);
        if (newMap == null) {
            Util.broadcastAdmins(Util.formatMsg("&cFailed to load the map '" + mapName + "'"));
            Util.broadcastAdmins(Util.formatMsg("&cThis is not a valid DvZ map!"));
            dvz.logTimings("MapManager.loadMap()[no dvz map]", t);
            return false;
        }

        //Map is already loaded.
        if (newMap.isLoaded()) {
            activeMap = mapName;
            mapCfg.setActiveMap(activeMap);
            Util.broadcastAdmins(Util.formatMsg("&cFailed to load the map '" + mapName + "'"));
            Util.broadcastAdmins(Util.formatMsg("&cMap was already loaded."));
            dvz.logTimings("MapManager.loadMap()[already loaded]", t);
            return true;
        }

        //Copy the map to root if it's not in there already.
        if (!newMap.isReadyToLoad()) {
            try {
                CWUtil.copyDirectory(new File("plugins/DvZ/maps/" + mapName), new File(mapName));
            } catch (IOException e) {
                Util.broadcastAdmins(Util.formatMsg("&cFailed to load the map '" + mapName + "'"));
                Util.broadcastAdmins(Util.formatMsg("&cCould not copy the map to the root."));
                dvz.logTimings("MapManager.loadMap()[failed copy new map]", t);
                return false;
            }
        }

        //Check if map is found in the root. (If it was copied successfully)
        if (newMap != null && newMap.isReadyToLoad()) {
            WorldCreator wc = new WorldCreator(mapName);
            wc.createWorld();
            dvz.getServer().createWorld(wc);
        } else {
            Util.broadcastAdmins(Util.formatMsg("&cFailed to load the map '" + mapName + "'"));
            Util.broadcastAdmins(Util.formatMsg("&cCould not find the map while trying to create the world."));
            dvz.logTimings("MapManager.loadMap()[failed loading]", t);
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                //Check if the map is loaded.
                if (!newMap.isLoaded()) {
                    Util.broadcastAdmins(Util.formatMsg("&cFailed to load the map '" + mapName + "'"));
                    Util.broadcastAdmins(Util.formatMsg("&cThe world didn't get loaded properly."));
                }

                //Map loaded!
                activeMap = mapName;
                mapCfg.setActiveMap(activeMap);
            }
        }.runTaskLater(dvz, 20);
        dvz.logTimings("MapManager.loadMap()", t);
        return true;
    }

    public Set<String> isSetProperly(DvzMap map) {
        MapData data = map.getData();
        Set<String> missingData = new HashSet<String>();

        String[] locNames = getLocationNames();
        for (String locType : locNames) {
            if (map.getLocation(locType) == null) {
                missingData.add("&4loc&7:&c" + locType);
            }
        }

        String[] cubNames = getCuboidNames();
        for (String cubType : cubNames) {
            if (map.getCuboid(cubType) == null) {
                missingData.add("&4cuboid&7:&c" + cubType);
            }
        }

        return missingData;
    }


    public String[] getLocationNames() {
        return new String[]{"lobby", "dwarf", "monster", "monsterlobby", "wall", "dragon"};
    }

    public String[] getCuboidNames() {
        return new String[]{"shrinewall", "shrine1keep", "shrine2keep", "keep", "wall", "innerwall"};
    }

    public World getUsedWorld() {
        if (dvz.getGM().getState() == GameState.CLOSED || dvz.getGM().getState() == GameState.ENDED || dvz.getGM().getState() == GameState.SETUP) {
            return dvz.getCfg().getDefaultWorld();
        } else {
            if (dvz.getMM().getActiveMap() == null || !dvz.getMM().getActiveMap().isLoaded()) {
                return dvz.getCfg().getDefaultWorld();
            } else {
                return dvz.getMM().getActiveMap().getWorld();
            }
        }
    }


    public DvzMap getMap(String mapName) {
        if (maps.containsKey(mapName)) {
            return maps.get(mapName);
        }
        return null;
    }

    public String getRandomMapName() {
        if (maps.size() <= 0) {
            return null;
        }
        return CWUtil.random(new ArrayList<String>(maps.keySet()));
    }

    public Map<String, DvzMap> getMaps() {
        return maps;
    }


    public String getActiveMapName() {
        return activeMap;
    }

    public DvzMap getActiveMap() {
        return getMap(activeMap);
    }

}
