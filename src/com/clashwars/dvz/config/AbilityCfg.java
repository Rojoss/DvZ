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
            case INFECT:
                abilityData = loadDefault(abilityData, "chance", "0.1");
                abilityData = loadDefault(abilityData, "duration", "12000");
                break;
            case HAMMER:
                abilityData = loadDefault(abilityData, "chance", "0.03");
                break;
            case RAPIDFIRE:
                abilityData = loadDefault(abilityData, "arrows", "20");
                abilityData = loadDefault(abilityData, "arrowspershot", "1");
                abilityData = loadDefault(abilityData, "tickdelay", "1");
                abilityData = loadDefault(abilityData, "randomoffset", "0.2");
                break;
            case POISON_ATTACK:
                abilityData = loadDefault(abilityData, "duration", "6000");
                abilityData = loadDefault(abilityData, "chance", "0.4");
                break;
            case RUSH:
                abilityData = loadDefault(abilityData, "range", "20");
                abilityData = loadDefault(abilityData, "multiplier", "0.4");
                break;
            case POISON:
                abilityData = loadDefault(abilityData, "range", "4");
                abilityData = loadDefault(abilityData, "duration", "6000");
                break;
            case WEB:
                abilityData = loadDefault(abilityData, "force", "0.8");
                break;
            case EXPLODE:
                abilityData = loadDefault(abilityData, "powerpersec", "0.5");
                abilityData = loadDefault(abilityData, "minpower", "0.5");
                abilityData = loadDefault(abilityData, "maxpower", "6.0");
                break;
            case SHOOT:
                abilityData = loadDefault(abilityData, "force", "0.4");
                break;
            case GLIDE:
                abilityData = loadDefault(abilityData, "force", "0.4");
                abilityData = loadDefault(abilityData, "height-force", "0.2");
                break;
            case FIREBALL:
                abilityData = loadDefault(abilityData, "damage", "2");
                abilityData = loadDefault(abilityData, "fire-duration", "40");
                abilityData = loadDefault(abilityData, "fire-radius", "3");
                abilityData = loadDefault(abilityData, "fire-chance", "0.5");
                break;
            case BLAST:
                abilityData = loadDefault(abilityData, "radius", "20");
                abilityData = loadDefault(abilityData, "rings", "5");
                break;
            case POTION_BOMB:
                abilityData = loadDefault(abilityData, "radius", "30");
                abilityData = loadDefault(abilityData, "fuse-time", "30");
                abilityData = loadDefault(abilityData, "blindness-duration", "200");
                abilityData = loadDefault(abilityData, "poison-duration", "400");
                break;
            case BLINK:
                abilityData = loadDefault(abilityData, "range", "20");
                break;
            case BUILDING_BRICK:
                abilityData = loadDefault(abilityData, "range", "50");
                break;
            case BUILDING_BLOCK:
                abilityData = loadDefault(abilityData, "range", "50");
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
