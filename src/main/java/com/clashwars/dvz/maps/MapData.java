package com.clashwars.dvz.maps;

import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;

public class MapData {

    public String locLobby;
    public String locDwarf;
    public String locMonster;
    public String locMonsterLobby;
    public String locWall;
    public String locDragon;

    public String locShrineWall1;
    public String locShrineWall2;
    public String locShrine1;
    public String locShrine2;

    public String locFortress1;
    public String locFortress2;
    public String locInnerWall1;
    public String locInnerWall2;


    public Location getLocLobby() {
        return CWUtil.locFromString(locLobby);
    }

    public void setLocLobby(Location locLobby) {
        this.locLobby = CWUtil.locToString(locLobby);
    }

    public Location getLocDwarf() {
        return CWUtil.locFromString(locDwarf);
    }

    public void setLocDwarf(Location locDwarf) {
        this.locDwarf = CWUtil.locToString(locDwarf);
    }

    public Location getLocMonster() {
        return CWUtil.locFromString(locMonster);
    }

    public void setLocMonster(Location locMonster) {
        this.locMonster = CWUtil.locToString(locMonster);
    }

    public Location getLocMonsterLobby() {
        return CWUtil.locFromString(locMonsterLobby);
    }

    public void setLocMonsterLobby(Location locMonsterLobby) {
        this.locMonsterLobby = CWUtil.locToString(locMonsterLobby);
    }

    public Location getLocWall() {
        return CWUtil.locFromString(locWall);
    }

    public void setLocWall(Location locWall) {
        this.locWall = CWUtil.locToString(locWall);
    }

    public Location getLocDragon() {
        return CWUtil.locFromString(locDragon);
    }

    public void setLocDragon(Location locDragon) {
        this.locDragon = CWUtil.locToString(locDragon);
    }

    public Location getLocShrineWall1() {
        return CWUtil.locFromString(locShrineWall1);
    }

    public void setLocShrineWall1(Location locShrineWall1) {
        this.locShrineWall1 = CWUtil.locToString(locShrineWall1);
    }

    public Location getLocShrineWall2() {
        return CWUtil.locFromString(locShrineWall2);
    }

    public void setLocShrineWall2(Location locShrineWall2) {
        this.locShrineWall2 = CWUtil.locToString(locShrineWall2);
    }

    public Location getLocShrine1() {
        return CWUtil.locFromString(locShrine1);
    }

    public void setLocShrine1(Location locShrine1) {
        this.locShrine1 = CWUtil.locToString(locShrine1);
    }

    public Location getLocShrine2() {
        return CWUtil.locFromString(locShrine2);
    }

    public void setLocShrine2(Location locShrine2) {
        this.locShrine2 = CWUtil.locToString(locShrine2);
    }

    public Location getLocFortress1() {
        return CWUtil.locFromString(locFortress1);
    }

    public void setLocFortress1(Location locFortress1) {
        this.locFortress1 = CWUtil.locToString(locFortress1);
    }

    public Location getLocFortress2() {
        return CWUtil.locFromString(locFortress2);
    }

    public void setLocFortress2(Location locFortress2) {
        this.locFortress2 = CWUtil.locToString(locFortress2);
    }

    public Location getLocInnerWall1() {
        return CWUtil.locFromString(locInnerWall1);
    }

    public void setLocInnerWall1(Location locInnerWall1) {
        this.locInnerWall1 = CWUtil.locToString(locInnerWall1);
    }

    public Location getLocInnerWall2() {
        return CWUtil.locFromString(locInnerWall2);
    }

    public void setLocInnerWall2(Location locInnerWall2) {
        this.locInnerWall2 = CWUtil.locToString(locInnerWall2);
    }

}
