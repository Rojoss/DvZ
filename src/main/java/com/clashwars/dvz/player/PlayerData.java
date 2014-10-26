package com.clashwars.dvz.player;

import com.clashwars.dvz.classes.DvzClass;

import java.util.HashSet;
import java.util.Set;

public class PlayerData {

    private DvzClass playerClass = DvzClass.DWARF;
    private int classExp = 0;
    private Set<DvzClass> classOptions = new HashSet<DvzClass>();

    public PlayerData() {
        //--
    }

    public PlayerData(DvzClass playerClass, int classExp, Set<DvzClass> classOptions) {
        this.playerClass = playerClass;
        this.classExp = classExp;
        this.classOptions = classOptions;
    }

    public DvzClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(DvzClass playerClass) {
        this.playerClass = playerClass;
    }

    public int getClassExp() {
        return classExp;
    }

    public void setClassExp(int classExp) {
        this.classExp = classExp;
    }

    public Set<DvzClass> getClassOptions() {
        return classOptions;
    }

    public void setClassOptions(Set<DvzClass> classOptions) {
        this.classOptions = classOptions;
    }

}
