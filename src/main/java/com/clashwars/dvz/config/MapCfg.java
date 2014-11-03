package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.maps.MapData;

import java.util.HashMap;
import java.util.Map;

public class MapCfg extends EasyConfig {

    public String ACTIVE_MAP_NAME = "";
    public HashMap<String, String> MAPS = new HashMap<String, String>();

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

    public String getActiveMap() {
        return ACTIVE_MAP_NAME;
    }

    public void setActiveMap(String mapName) {
        this.ACTIVE_MAP_NAME = mapName;
        save();
    }
}
