package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;

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
        classData = loadDefault(classData, "speed", "0.2");
        c.getClassClass().setSpeed(CWUtil.getFloat(classData.get("speed")));
        classData = loadDefault(classData, "displayname", "");
        c.getClassClass().setDisplayName(classData.get("displayname"));
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
                classData = loadDefault(classData, "stone-drops", "2");
                classData = loadDefault(classData, "ore-drops", "1");
                classData = loadDefault(classData, "min-respawn-time", "200");
                classData = loadDefault(classData, "max-respawn-time", "800");
                break;
            case FLETCHER:
                classData = loadDefault(classData, "workshop-types", "1");
                classData = loadDefault(classData, "pig-drop-amount", "1");
                classData = loadDefault(classData, "chicken-bonus-height", "5");
                classData = loadDefault(classData, "flint-needed", "8");
                classData = loadDefault(classData, "feathers-needed", "8");
                classData = loadDefault(classData, "bow-product-chance", "0.33");
                classData = loadDefault(classData, "min-arrow-amount", "16");
                classData = loadDefault(classData, "max-arrow-amount", "64");
                classData = loadDefault(classData, "flint-chance", "0.25");
                classData = loadDefault(classData, "animal-respawn-time-min", "100");
                classData = loadDefault(classData, "animal-respawn-time-max", "300");
                classData = loadDefault(classData, "chicken-amount", "4");
                classData = loadDefault(classData, "pig-amount", "2");
                break;
            case TAILOR:
                classData = loadDefault(classData, "workshop-types", "1");
                classData = loadDefault(classData, "sheep-amount", "8");
                classData = loadDefault(classData, "wool-drop-amount", "1");
                classData = loadDefault(classData, "wool-regrow-min", "100");
                classData = loadDefault(classData, "wool-regrow-max", "600");
                classData = loadDefault(classData, "flower-respawn-time", "600");
                classData = loadDefault(classData, "wool-needed", "8");
                classData = loadDefault(classData, "reddye-needed", "1");
                classData = loadDefault(classData, "yellowdye-needed", "1");
                break;
            case ALCHEMIST:
                classData = loadDefault(classData, "workshop-types", "1");
                classData = loadDefault(classData, "cauldron-refill-delay", "4");
                classData = loadDefault(classData, "melons-needed", "5");
                classData = loadDefault(classData, "sugar-needed", "5");
                classData = loadDefault(classData, "melon-respawn-time", "600");
                classData = loadDefault(classData, "sugarcane-respawn-time", "200");
                break;
            case BAKER:
                classData = loadDefault(classData, "workshop-types", "1");
                classData = loadDefault(classData, "wheat-per-flour", "5");
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
