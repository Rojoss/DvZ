package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.structures.data.DisposeData;
import com.clashwars.dvz.structures.data.EnchantData;
import com.clashwars.dvz.structures.data.FurnaceData;
import com.clashwars.dvz.structures.data.StorageData;

public class StructureCfg extends EasyConfig {

    public String ENCHANT_DATA = "";
    public String FURNACE_DATA = "";
    public String DISPOSE_DATA = "";
    public String STORAGE_DATA = "";

    public StructureCfg(String fileName) {
        this.setFile(fileName);
    }


    public EnchantData getEnchantData() {
        return DvZ.inst().getGson().fromJson(ENCHANT_DATA, EnchantData.class);
    }

    public void setEnchantData(EnchantData data) {
        ENCHANT_DATA = DvZ.inst().getGson().toJson(data, EnchantData.class);
        save();
    }


    public FurnaceData getFurnaceData() {
        return DvZ.inst().getGson().fromJson(FURNACE_DATA, FurnaceData.class);
    }

    public void setFurnaceData(FurnaceData data) {
        FURNACE_DATA = DvZ.inst().getGson().toJson(data, FurnaceData.class);
        save();
    }


    public DisposeData getDisposeData() {
        return DvZ.inst().getGson().fromJson(DISPOSE_DATA, DisposeData.class);
    }

    public void setDisposeData(DisposeData data) {
        DISPOSE_DATA = DvZ.inst().getGson().toJson(data, DisposeData.class);
        save();
    }


    public StorageData getStorageData() {
        return DvZ.inst().getGson().fromJson(STORAGE_DATA, StorageData.class);
    }

    public void setStorageData(StorageData data) {
        STORAGE_DATA = DvZ.inst().getGson().toJson(data, StorageData.class);
        save();
    }

}
