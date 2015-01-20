package com.clashwars.dvz.maps;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;

public class MapData {

    public String locLobby;
    public String locDwarf;
    public String locMonster;
    public String locMonsterLobby;
    public String locWall;
    public String locDragon;

    public String cubShrineWall;
    public String cubShrineKeep;
    public String cubKeep;
    public String cubWall;
    public String cubInnerWall;


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


    public Cuboid getShrineWall() {
        return Cuboid.deserialize(cubShrineWall);
    }

    public void setShrineWall(Cuboid cuboid) {
        this.cubShrineWall = cuboid.toString();
    }


    public Cuboid getShrineKeep() {
        return Cuboid.deserialize(cubShrineKeep);
    }

    public void setShrineKeep(Cuboid cuboid) {
        this.cubShrineKeep = cuboid.toString();
    }


    public Cuboid getKeep() {
        return Cuboid.deserialize(cubKeep);
    }

    public void setKeep(Cuboid cuboid) {
        this.cubKeep = cuboid.toString();
    }


    public Cuboid getWall() {
        return Cuboid.deserialize(cubWall);
    }

    public void setWall(Cuboid cuboid) {
        this.cubWall = cuboid.toString();
    }


    public Cuboid getInnerWall() {
        return Cuboid.deserialize(cubInnerWall);
    }

    public void setInnerWall(Cuboid cuboid) {
        this.cubInnerWall = cuboid.toString();
    }
}
