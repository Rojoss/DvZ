package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;

import java.util.HashMap;

public class AbilityCfg extends EasyConfig {

    public HashMap<String, HashMap<String, String>> ABILITIES = new HashMap<String, HashMap<String, String>>();

    public AbilityCfg(String fileName) {
        this.setFile(fileName);
        load();
    }

    @Override
    public void load() {
        super.load();
        for (Ability a : Ability.values()) {
            if (!ABILITIES.containsKey(a.toString())) {
                ABILITIES.put(a.toString(), new HashMap<String, String>());
            }
            ABILITIES.put(a.toString(), loadDefaults(a));
        }
        save();
    }

    private HashMap<String, String> loadDefaults(Ability a) {
        HashMap<String, String> abilityData = ABILITIES.get(a.toString());
        abilityData = loadDefault(abilityData, "displayname", "");
        a.getAbilityClass().setDisplayName(abilityData.get("displayname"));
        abilityData = loadDefault(abilityData, "desc", "");
        a.getAbilityClass().setDesc(abilityData.get("desc"));
        abilityData = loadDefault(abilityData, "usage", "");
        a.getAbilityClass().setUsage(abilityData.get("usage"));
        abilityData = loadDefault(abilityData, "cooldown", "0");
        a.getAbilityClass().setCooldown(CWUtil.getInt(abilityData.get("cooldown")));

        switch (a) {
            case PICKUP:
                abilityData = loadDefault(abilityData, "block-cooldown", "5000");
                break;
            case BUILDING_BRICK:
                abilityData = loadDefault(abilityData, "range", "50");
                break;
            case BUILDING_BLOCK:
                abilityData = loadDefault(abilityData, "blocks", "5");
                break;
            case BURN:
                abilityData = loadDefault(abilityData, "distance", "50");
            case GEYSER:
                abilityData = loadDefault(abilityData, "range", "50");
                abilityData = loadDefault(abilityData, "geyser-height", "4");
                abilityData = loadDefault(abilityData, "force", "1");
                break;
            case HORN:
                abilityData = loadDefault(abilityData, "duration", "400");
                break;
        }

        return abilityData;
    }

    private HashMap<String, String> loadDefault(HashMap<String, String> data, String type, String def) {
        if (!data.containsKey(type)) {
            data.put(type, def);
        }
        return data;
    }

    public HashMap<String, HashMap<String, String>> geOptions() {
        return ABILITIES;
    }

    public HashMap<String, String> getAbilityOptions(Ability ability) {
        return ABILITIES.get(ability.toString());
    }

    public String getOption(Ability ability, String key) {
        key = key.toLowerCase();
        HashMap<String, String> abilityOptions = ABILITIES.get(ability.toString());
        if (abilityOptions.containsKey(key)) {
            return abilityOptions.get(key);
        }
        return null;
    }
}
