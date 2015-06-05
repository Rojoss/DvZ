package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.VIP.ArmorPresetData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArmorPresetsCfg extends EasyConfig {

    public HashMap<String, String> PRESETS = new HashMap<String, String>();

    public ArmorPresetsCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<UUID, ArmorPresetData> getPresets() {
        Map<UUID, ArmorPresetData> presets = new HashMap<UUID, ArmorPresetData>();
        for (String key : PRESETS.keySet()) {
            presets.put(UUID.fromString(key), DvZ.inst().getGson().fromJson(PRESETS.get(key), ArmorPresetData.class));
        }
        return presets;
    }

    public ArmorPresetData getPreset(UUID uuid) {
        return DvZ.inst().getGson().fromJson(PRESETS.get(uuid.toString()), ArmorPresetData.class);
    }

    public void setPreset(UUID uuid, ArmorPresetData pd) {
        PRESETS.put(uuid.toString(), DvZ.inst().getGson().toJson(pd, ArmorPresetData.class));
        save();
    }
}
