package com.clashwars.dvz.config;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.stats.StatsData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsCfg extends EasyConfig {

    public HashMap<String, String> STATS = new HashMap<String, String>();
    public String SERVER_STATS = "";

    public StatsCfg(String fileName) {
        this.setFile(fileName);
    }

    public Map<UUID, StatsData> getStats() {
        Map<UUID, StatsData> stats = new HashMap<UUID, StatsData>();
        for (String key : STATS.keySet()) {
            stats.put(UUID.fromString(key), DvZ.inst().getGson().fromJson(STATS.get(key), StatsData.class));
        }
        return stats;
    }

    public StatsData getPlayerStats(UUID uuid) {
        return DvZ.inst().getGson().fromJson(STATS.get(uuid.toString()), StatsData.class);
    }

    public void setPlayerStats(UUID uuid, StatsData sd) {
        STATS.put(uuid.toString(), DvZ.inst().getGson().toJson(sd, StatsData.class));
    }

    public void removePlayerStats(UUID uuid) {
        STATS.remove(uuid.toString());
    }

    public StatsData getServerStats() {
        return DvZ.inst().getGson().fromJson(SERVER_STATS, StatsData.class);
    }

    public void setServerStats(StatsData sd) {
        SERVER_STATS = DvZ.inst().getGson().toJson(sd, StatsData.class);
    }

    public void removeServerStats() {
        SERVER_STATS = "";
    }

    public void removeStats() {
        STATS.clear();
        save();
    }
}
