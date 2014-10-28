package com.clashwars.dvz.maps;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.config.MapCfg;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

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
        activeMap = dvz.getServer().getWorlds().get(0).getName();
        populate();
    }

    private void populate() {
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
    }

    public boolean removeActiveMap() {
        if (getActiveMap() == null || !getActiveMap().isLoaded()) {
            return false;
        }

        //Kick all players remaining in the map.
        for (Player p : getActiveMap().getWorld().getPlayers()) {
            p.kickPlayer("&4&lYou have been kicked because the map is resetting!\n&cWe couldn't teleport you to the lobby for some reason.\n&cYou can try to log back in to fix it.");
        }

        //Try unload the map.
        if (getActiveMap().isLoaded()) {
            Bukkit.unloadWorld(getActiveMapName(), false);
        }
        String activeName = getActiveMapName();
        activeMap = null;

        //Try remove the map.
        if (!CWUtil.deleteDirectory(new File(activeName))) {
            dvz.log("Failed to remove the map '" + activeName + "'. The map has been unloaded though so it can be manually deleted.");
            return false;
        }

        return true;
        }


    public boolean loadMap(String mapName) {
        if (mapName == null || mapName.isEmpty()) {
            mapName = getRandomMapName();
        }
        //If map is still null it means there are no maps in the map manager.
        if (mapName == null || mapName.isEmpty()) {
            dvz.log("Failed to load the map '" + mapName + "'");
            dvz.log("You probably forgot to add maps to '/plugins/dvz/maps'");
            return false;
        }

        //Set active map and make sure it's a valid map.
        DvzMap newMap = getMap(mapName);
        if (newMap == null) {
            dvz.log("Failed to load the map '" + mapName + "'");
            dvz.log("This is not a valid DvZ map!");
            return false;
        }

        //Map is already loaded.
        if (newMap.isLoaded()) {
            activeMap = mapName;
            dvz.log("Failed to load the map '" + mapName + "'");
            dvz.log("Map was already loaded.");
            return true;
        }

        //Copy the map to root if it's not in there already.
        if (!newMap.isReadyToLoad()) {
            try {
                CWUtil.copyDirectory(new File("plugins/DvZ/maps/" + mapName), new File(""));
            } catch (IOException e) {
                dvz.log("Failed to load the map '" + mapName + "'");
                dvz.log("Could not copy the map to the root.");
                return false;
            }
        }

        //TODO: Probably need schedulers in here.
        //Check if map is found in the root. (If it was copied successfully)
        if (newMap != null && newMap.isReadyToLoad()) {
            WorldCreator wc = new WorldCreator(mapName);
            wc.createWorld();
        } else {
            dvz.log("Failed to load the map '" + mapName + "'");
            dvz.log("Could not find the map while trying to create the world.");
            return false;
        }

        //Check if the map is loaded.
        if (!getActiveMap().isLoaded()) {
            dvz.log("Failed to load the map '" + mapName + "'");
            dvz.log("The world didn't get loaded properly.");
            return false;
        }

        //Map loaded!
        activeMap = mapName;
        return true;
    }

    public Set<String> isSetProperly(DvzMap map) {
        MapData data = map.getData();
        Set<String> missingData = new HashSet<String>();

        for (String locType : mapCfg.getLocationNames()) {
            if (map.getLocation(locType) == null) {
                missingData.add("location:" + locType);
            }
        }

        return missingData;
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
        return CWUtil.random(new ArrayList<String>(maps.keySet()));
    }

    public Map<String, DvzMap> getMaps(){
        return maps;
    }


    public String getActiveMapName() {
        return activeMap;
    }

    public DvzMap getActiveMap() {
        return getMap(activeMap);
    }

}
