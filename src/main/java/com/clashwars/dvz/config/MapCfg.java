package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.maps.MapData;

import java.util.*;

public class MapCfg extends EasyConfig {

    public HashMap<String, String> MAPS = new HashMap<String, String>();
    private final String[] locationNames = new String[] {"lobby", "dwarf", "monster", "monsterlobby", "wall", "dragon",
            "shrine1", "shrine2", "shrinewall1", "shrinewall2", "fortress1", "fortress2", "innerwall1", "innerwall2"};


    public MapCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<String, MapData> getMaps() {
        Map<String, MapData> maps = new HashMap<String, MapData>();
        for (String key : MAPS.keySet()) {
            maps.put(key, DvZ.inst().getGson().fromJson(MAPS.get(key), MapData.class));
        }
        return maps;
    }

    public MapData getMap(String worldName) {
        return DvZ.inst().getGson().fromJson(MAPS.get(worldName), MapData.class);
    }

    public void setMap(String mapName, MapData data) {
        MAPS.put(mapName, DvZ.inst().getGson().toJson(data, MapData.class));
        save();
    }

    public String[] getLocationNames() {
        return locationNames;
    }
}
