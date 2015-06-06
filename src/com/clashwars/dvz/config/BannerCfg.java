package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.VIP.BannerData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BannerCfg extends EasyConfig {

    public HashMap<String, String> BANNERS = new HashMap<String, String>();

    public BannerCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<UUID, BannerData> getBanners() {
        Map<UUID, BannerData> banners = new HashMap<UUID, BannerData>();
        for (String key : BANNERS.keySet()) {
            banners.put(UUID.fromString(key), DvZ.inst().getGson().fromJson(BANNERS.get(key), BannerData.class));
        }
        return banners;
    }

    public BannerData getBanner(UUID uuid) {
        return DvZ.inst().getGson().fromJson(BANNERS.get(uuid.toString()), BannerData.class);
    }

    public void setBanner(UUID uuid, BannerData pd) {
        BANNERS.put(uuid.toString(), DvZ.inst().getGson().toJson(pd, BannerData.class));
        save();
    }
}
