package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.workshop.WorkShopData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorkShopCfg extends EasyConfig {

    public HashMap<String, String> WORKSHOPS = new HashMap<String, String>();

    public WorkShopCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<UUID, WorkShopData> getWorkShops() {
        Map<UUID, WorkShopData> workshops = new HashMap<UUID, WorkShopData>();
        for (String key : WORKSHOPS.keySet()) {
            workshops.put(UUID.fromString(key), DvZ.inst().getGson().fromJson(WORKSHOPS.get(key), WorkShopData.class));
        }
        return workshops;
    }

    public WorkShopData getWorkShop(UUID uuid) {
        return DvZ.inst().getGson().fromJson(WORKSHOPS.get(uuid.toString()), WorkShopData.class);
    }

    public void setWorkShop(UUID uuid, WorkShopData wd) {
        WORKSHOPS.put(uuid.toString(), DvZ.inst().getGson().toJson(wd, WorkShopData.class));
        save();
    }

    public void removeWorkShop(UUID uuid) {
        WORKSHOPS.remove(uuid.toString());
        save();
    }

}
