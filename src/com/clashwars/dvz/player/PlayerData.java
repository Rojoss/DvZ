package com.clashwars.dvz.player;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;

import java.util.HashSet;
import java.util.Set;

public class PlayerData {

    private DvzClass playerClass = DvzClass.DWARF;
    private int classLvl = 0;
    private int classExp = 0;
    private boolean parkourCompleted = false;
    private Set<DvzClass> classOptions = new HashSet<DvzClass>();
    private Set<Ability> dwarfAbilitiesReceived = new HashSet<Ability>();
    private boolean buffUsed = false;
    private boolean bombUsed = false;
    private boolean isBuffed = false;

    public PlayerData() {
        //--
    }

    public DvzClass getPlayerClass() {
        return playerClass;
    }

    public void setPlayerClass(DvzClass playerClass) {
        this.playerClass = playerClass;
    }

    public int getClassLvl() {
        return classLvl;
    }

    public void setClassLvl(int classLvl) {
        this.classLvl = classLvl;
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

    public Set<Ability> getDwarfAbilitiesReceived() {
        return dwarfAbilitiesReceived;
    }

    public void addDwarfAbility(Ability ability) {
        dwarfAbilitiesReceived.add(ability);
    }

    public void setDwarfAbilitiesReceived(Set<Ability> abilities) {
        this.dwarfAbilitiesReceived = abilities;
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
