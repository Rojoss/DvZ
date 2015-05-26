package com.clashwars.dvz.player;

import com.clashwars.dvz.classes.DvzClass;

import java.util.HashSet;
import java.util.Set;

public class PlayerData {

    private DvzClass playerClass = DvzClass.DWARF;
    private int classExp = 0;
    private boolean parkourCompleted = false;
    private Set<DvzClass> classOptions = new HashSet<DvzClass>();
    private boolean buffUsed = false;
    private boolean bombUsed = false;
    private boolean isBuffed = false;

    public PlayerData() {
        //--
    }

    public PlayerData(DvzClass playerClass, int classExp, Set<DvzClass> classOptions, String workshopData) {
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

    public boolean hasCompletedParkour() {
        return parkourCompleted;
    }

    public void setParkourCompleted(boolean completed) {
        this.parkourCompleted = completed;
    }

    public Set<DvzClass> getClassOptions() {
        return classOptions;
    }

    public void removeClassOption(DvzClass dvzClass) {
        classOptions.remove(dvzClass);
    }

    public void setClassOptions(Set<DvzClass> classOptions) {
        this.classOptions = classOptions;
    }

    public boolean isBuffUsed() {
        return buffUsed;
    }

    public void setBuffUsed(boolean buffUsed) {
        this.buffUsed = buffUsed;
    }

    public boolean isBombUsed() {
        return bombUsed;
    }

    public void setbombUsed(boolean bombUsed) {
        this.bombUsed = bombUsed;
    }

    public boolean isBuffed() {
        return isBuffed;
    }

    public void setBuffed(boolean buffed) {
        this.isBuffed = buffed;
    }



}
