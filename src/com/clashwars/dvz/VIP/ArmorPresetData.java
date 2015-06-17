package com.clashwars.dvz.VIP;

import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class ArmorPresetData {

    List<Color> presets = new ArrayList<Color>();

    public ArmorPresetData() {
        //--
    }

    public List<Color> getPresets() {
        return presets;
    }

    public void setPresets(List<Color> colors) {
        this.presets = colors;
    }
}
