package com.clashwars.dvz.player;

import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvZClass;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private DvZClass playerClass = DvZClass.DWARF;
    private int classExp = 0;
    private Set<DvZClass> classOptions = new HashSet<DvZClass>();

    public PlayerData() {}

    public PlayerData(DvZClass playerClass, int classExp, Set<DvZClass> classOptions) {
        this.playerClass = playerClass;
        this.classExp = classExp;
        this.classOptions = classOptions;
    }

    public DvZClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(DvZClass playerClass) {
        this.playerClass = playerClass;
    }

    public int getClassExp() {
        return classExp;
    }

    public void setClassExp(int classExp) {
        this.classExp = classExp;
    }

    public Set<DvZClass> getClassOptions() {
        return classOptions;
    }

    public void setClassOptions(Set<DvZClass> classOptions) {
        this.classOptions = classOptions;
    }

}
