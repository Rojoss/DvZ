package com.clashwars.dvz.maps;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;

public class DvzMap {

    private DvZ dvz;

    private String mapName;
    private MapData data;


    public DvzMap(String mapName, MapData data) {
        dvz = DvZ.inst();
        this.mapName = mapName;
        this.data = data;
    }


    public World getWorld() {
        return dvz.getServer().getWorld(mapName);
    }

    //Check if the map is is loaded as bukkit world.
    public boolean isLoaded() {
        return dvz.getServer().getWorlds().contains(mapName);
    }

    //Check if this map is in root ready to be loaded.
    public boolean isReadyToLoad() {
        return CWUtil.getDirectories(new File("")).contains(mapName);
    }

    //Check if this is the active map used for dvz..
    public boolean isActive() {
        return dvz.getMM().getActiveMapName().equals(mapName);
    }


    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }


    public MapData getData() {
        return data;
    }

    public void setData(MapData data) {
        this.data = data;
    }


    public boolean isLocWithin(Location loc, String name) {
        switch (name.toLowerCase()) {
            case "shrinewall":
                return CWUtil.isLocWithin(loc, data.getLocShrineWall1(), data.getLocShrineWall2());

            case "shrine":
                return CWUtil.isLocWithin(loc, data.getLocShrine1(), data.getLocShrine2());

            case "fortress":
            case "tower1":
                return CWUtil.isLocWithin(loc, data.getLocFortress1(), data.getLocFortress2());

            case "innerwall":
                return CWUtil.isLocWithin(loc, data.getLocInnerWall1(), data.getLocInnerWall2());
        }
        return false;
    }

    public Location getLocation(String name) {
        switch (name.toLowerCase()) {
            case "lobby":
            case "spawn":
                return data.getLocLobby();
            case "dwarf":
            case "dwarves":
                return data.getLocDwarf();
            case "monsterlobby":
            case "monsterslobby":
            case "moblobby":
            case "mobslobby":
                return data.getLocMonsterLobby();
            case "monster":
            case "monsters":
            case "mob":
            case "mobs":
                return data.getLocMonster();
            case "wall":
                return data.getLocWall();
            case "dragon":
            case "dragons":
                return data.getLocDragon();

            case "shrinewall1":
                return data.getLocShrineWall1();
            case "shrinewall2":
                return data.getLocShrineWall2();

            case "shrine1":
                return data.getLocShrine1();
            case "shrine2":
                return data.getLocShrine2();

            case "fortress1":
            case "tower1":
                return data.getLocFortress1();
            case "fortress2":
            case "tower2":
                return data.getLocFortress2();

            case "innerwall1":
                return data.getLocInnerWall1();
            case "innerwall2":
                return data.getLocInnerWall2();

        }
        return null;
    }

    public Location setLocation(String name, Location loc) {
        switch (name.toLowerCase()) {
            case "lobby":
            case "spawn":
                data.setLocLobby(loc);
                break;
            case "dwarf":
            case "dwarves":
                data.setLocDwarf(loc);
                break;
            case "monsterlobby":
            case "monsterslobby":
            case "moblobby":
            case "mobslobby":
                data.setLocMonsterLobby(loc);
                break;
            case "monster":
            case "monsters":
            case "mob":
            case "mobs":
                data.setLocMonster(loc);
                break;
            case "wall":
                data.setLocWall(loc);
                break;
            case "dragon":
            case "dragons":
                data.setLocDragon(loc);
                break;

            case "shrinewall1":
                data.setLocShrineWall1(loc);
                break;
            case "shrinewall2":
                data.setLocShrineWall2(loc);
                break;

            case "shrine1":
                data.setLocShrine1(loc);
                break;
            case "shrine2":
                data.setLocShrine2(loc);
                break;

            case "fortress1":
            case "tower1":
                data.setLocFortress1(loc);
                break;
            case "fortress2":
            case "tower2":
                data.setLocFortress2(loc);
                break;

            case "innerwall1":
                data.setLocInnerWall1(loc);
                break;
            case "innerwall2":
                data.setLocInnerWall2(loc);
                break;

        }
        return null;
    }
}
