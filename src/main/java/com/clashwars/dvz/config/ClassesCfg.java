package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import org.bukkit.ChatColor;

import java.util.HashMap;

public class ClassesCfg extends EasyConfig {

    public HashMap<String, HashMap<String, String>> CLASSES = new HashMap<String, HashMap<String, String>>();

    public ClassesCfg(String fileName) {
        this.setFile(fileName);
        load();
    }

    @Override
    public void load() {
        super.load();
        for (DvzClass c : DvzClass.values()) {
            if (!CLASSES.containsKey(c.toString())) {
                CLASSES.put(c.toString(), new HashMap<String, String>());
            }
            CLASSES.put(c.toString(), loadDefaults(c));
        }
        save();
    }

    private HashMap<String, String> loadDefaults(DvzClass c) {
        HashMap<String, String> classData = CLASSES.get(c.toString());

        classData = loadDefault(classData, "class", c.toString());
        c.getClassClass().setClass(DvzClass.fromString(classData.get("class")));
        classData = loadDefault(classData, "weight", "0.2");
        c.getClassClass().setWeight(CWUtil.getDouble(classData.get("weight")));
        classData = loadDefault(classData, "color", "WHITE");
        c.getClassClass().setColor(ChatColor.valueOf(classData.get("color")));
        classData = loadDefault(classData, "displayname", "");
        c.getClassClass().setDisplayName(classData.get("displayname"));
        classData = loadDefault(classData, "desc", "");
        c.getClassClass().setDescription(classData.get("desc"));
        classData = loadDefault(classData, "health", "20");
        c.getClassClass().setHealth(CWUtil.getInt(classData.get("health")));

        //For all dwarves
        if (c.getType() == ClassType.DWARF) {
            classData = loadDefault(classData, "task", "");
            c.getClassClass().setTask(classData.get("task"));
            classData = loadDefault(classData, "produce", "");
            c.getClassClass().setProduce(classData.get("produce"));
        }

        //For all monsters
        if (c.getType() == ClassType.MONSTER) {
            classData = loadDefault(classData, "disguise", "");
            c.getClassClass().setDisguise(classData.get("disguise"));
        }

        switch (c) {
            case MINER:
                classData = loadDefault(classData, "workshop-types", "1");
                break;
            case FLETCHER:
                classData = loadDefault(classData, "workshop-types", "1");
                break;
            case TAILOR:
                classData = loadDefault(classData, "workshop-types", "1");
                break;
            case ALCHEMIST:
                classData = loadDefault(classData, "workshop-types", "1");
                classData = loadDefault(classData, "cauldron-refill-delay", "4");
                break;
            case PIG:
                classData = loadDefault(classData, "disguise-grown", "pig");
                classData = loadDefault(classData, "disguise-pigman", "pigman");
                break;
            case VILLAGER:
                classData = loadDefault(classData, "disguise-witch", "witch");
                break;
        }

        return classData;
    }

    private HashMap<String, String> loadDefault(HashMap<String, String> data, String type, String def) {
        if (!data.containsKey(type)) {
            data.put(type, def);
        }
        return data;
    }

    public HashMap<String, HashMap<String, String>> geOptions() {
        return CLASSES;
    }

    public HashMap<String, String> getClassOptions(DvzClass dvzClass) {
        return CLASSES.get(dvzClass.toString());
    }

    public String getOption(DvzClass dvzClass, String key) {
        key = key.toLowerCase();
        HashMap<String, String> classOptions = CLASSES.get(dvzClass.toString());
        if (classOptions.containsKey(key)) {
            return classOptions.get(key);
        }
        return null;
    }
}
