package com.clashwars.dvz.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.PlayerData;

import java.util.*;

public class RewardsCfg extends EasyConfig {

    public List<String> STRUCTURE_CMDS = new ArrayList<String>();
    public List<String> STORAGE_CMD = new ArrayList<String>();
    public List<String> FURNACE_CMD = new ArrayList<String>();
    public List<String> ENCHANT_CMD = new ArrayList<String>();
    public HashMap<String, String> EXTRA_MONSTER = new HashMap<String, String>();
    public HashMap<String, String> EXTRA_DWARF = new HashMap<String, String>();

    public RewardsCfg(String fileName) {
        this.setFile(fileName);
    }


    public List<UUID> getStructureCmds() {
        return CWUtil.stringListToUUID(STRUCTURE_CMDS);
    }

    public void addStructureCmds(UUID player) {
        STRUCTURE_CMDS.add(player.toString());
        save();
    }

    public boolean hasStructureCmds(UUID player) {
        return STRUCTURE_CMDS.contains(player.toString());
    }

    public void resetStructureCmds() {
        STRUCTURE_CMDS.clear();
        save();
    }



    public List<UUID> getStorageCmd() {
        return CWUtil.stringListToUUID(STORAGE_CMD);
    }

    public void addStorageCmd(UUID player) {
        STORAGE_CMD.add(player.toString());
        save();
    }

    public boolean hasStorageCmd(UUID player) {
        return STORAGE_CMD.contains(player.toString());
    }



    public List<UUID> getFurnaceCmd() {
        return CWUtil.stringListToUUID(FURNACE_CMD);
    }

    public void addFurnaceCmd(UUID player) {
        FURNACE_CMD.add(player.toString());
        save();
    }

    public boolean hasFurnaceCmd(UUID player) {
        return FURNACE_CMD.contains(player.toString());
    }



    public List<UUID> getEnchantCmd() {
        return CWUtil.stringListToUUID(ENCHANT_CMD);
    }

    public void addEnchantCmd(UUID player) {
        ENCHANT_CMD.add(player.toString());
        save();
    }

    public boolean hasEnchantCmd(UUID player) {
        return ENCHANT_CMD.contains(player.toString());
    }



    public HashMap<UUID, Integer> getExtraMonster()  {
        HashMap<UUID, Integer> result = new HashMap<UUID, Integer>();
        for (Map.Entry<String, String> entry : EXTRA_MONSTER.entrySet()) {
            result.put(UUID.fromString(entry.getKey()), CWUtil.getInt(entry.getValue()));
        }
        return result;
    }

    public int getExtraMonster(UUID player) {
        if (EXTRA_MONSTER.containsKey(player.toString())) {
            return CWUtil.getInt(EXTRA_MONSTER.get(player.toString()));
        }
        return 0;
    }

    public void setExtraMonster(UUID player, int percent) {
        if (EXTRA_MONSTER.containsKey(player.toString())) {
            EXTRA_MONSTER.put(player.toString(), Integer.toString(CWUtil.getInt(EXTRA_MONSTER.get(player.toString())) + percent));
        } else {
            EXTRA_MONSTER.put(player.toString(), Integer.toString(percent));
        }
        save();
    }

    public void resetExtraMonster() {
        EXTRA_MONSTER.clear();
        save();
    }



    public HashMap<UUID, Integer> getExtraDwarf()  {
        HashMap<UUID, Integer> result = new HashMap<UUID, Integer>();
        for (Map.Entry<String, String> entry : EXTRA_DWARF.entrySet()) {
            result.put(UUID.fromString(entry.getKey()), CWUtil.getInt(entry.getValue()));
        }
        return result;
    }

    public int getExtraDwarf(UUID player) {
        if (EXTRA_DWARF.containsKey(player.toString())) {
            return CWUtil.getInt(EXTRA_DWARF.get(player.toString()));
        }
        return 0;
    }

    public void setExtraDwarf(UUID player, int amount) {
        if (EXTRA_DWARF.containsKey(player.toString())) {
            EXTRA_DWARF.put(player.toString(), Integer.toString(CWUtil.getInt(EXTRA_DWARF.get(player.toString())) + amount));
        } else {
            EXTRA_DWARF.put(player.toString(), Integer.toString(amount));
        }
        save();
    }
}
