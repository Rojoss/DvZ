package com.clashwars.dvz.VIP;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.*;

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
