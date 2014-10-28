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
        String s = name.toLowerCase();
        if (s.equals("shrinewall")) {
            return CWUtil.isLocWithin(loc, data.getLocShrineWall1(), data.getLocShrineWall2());
        } else if (s.equals("shrine")) {
            return CWUtil.isLocWithin(loc, data.getLocShrine1(), data.getLocShrine2());
        } else if (s.equals("fortress") || s.equals("tower1")) {
            return CWUtil.isLocWithin(loc, data.getLocFortress1(), data.getLocFortress2());
        } else if (s.equals("innerwall")) {
            return CWUtil.isLocWithin(loc, data.getLocInnerWall1(), data.getLocInnerWall2());
        }
        return false;
    }

    public Location getLocation(String name) {
        String s = name.toLowerCase();
        if (s.equals("lobby") || s.equals("spawn")) {
            return data.getLocLobby();
        } else if (s.equals("dwarf") || s.equals("dwarves")) {
            return data.getLocDwarf();
        } else if (s.equals("monsterlobby") || s.equals("monsterslobby") || s.equals("moblobby") || s.equals("mobslobby")) {
            return data.getLocMonsterLobby();
        } else if (s.equals("monster") || s.equals("monsters") || s.equals("mob") || s.equals("mobs")) {
            return data.getLocMonster();
        } else if (s.equals("wall")) {
            return data.getLocWall();
        } else if (s.equals("dragon") || s.equals("dragons")) {
            return data.getLocDragon();
        } else if (s.equals("shrinewall1")) {
            return data.getLocShrineWall1();
        } else if (s.equals("shrinewall2")) {
            return data.getLocShrineWall2();
        } else if (s.equals("shrine1")) {
            return data.getLocShrine1();
        } else if (s.equals("shrine2")) {
            return data.getLocShrine2();
        } else if (s.equals("fortress1") || s.equals("tower1")) {
            return data.getLocFortress1();
        } else if (s.equals("fortress2") || s.equals("tower2")) {
            return data.getLocFortress2();
        } else if (s.equals("innerwall1")) {
            return data.getLocInnerWall1();
        } else if (s.equals("innerwall2")) {
            return data.getLocInnerWall2();
        }
        return null;
    }

    public Location setLocation(String name, Location loc) {
        String s = name.toLowerCase();
        if (s.equals("lobby") || s.equals("spawn")) {
            data.setLocLobby(loc);
        } else if (s.equals("dwarf") || s.equals("dwarves")) {
            data.setLocDwarf(loc);
        } else if (s.equals("monsterlobby") || s.equals("monsterslobby") || s.equals("moblobby") || s.equals("mobslobby")) {
            data.setLocMonsterLobby(loc);
        } else if (s.equals("monster") || s.equals("monsters") || s.equals("mob") || s.equals("mobs")) {
            data.setLocMonster(loc);
        } else if (s.equals("wall")) {
            data.setLocWall(loc);
        } else if (s.equals("dragon") || s.equals("dragons")) {
            data.setLocDragon(loc);
        } else if (s.equals("shrinewall1")) {
            data.setLocShrineWall1(loc);
        } else if (s.equals("shrinewall2")) {
            data.setLocShrineWall2(loc);
        } else if (s.equals("shrine1")) {
            data.setLocShrine1(loc);
        } else if (s.equals("shrine2")) {
            data.setLocShrine2(loc);
        } else if (s.equals("fortress1") || s.equals("tower1")) {
            data.setLocFortress1(loc);
        } else if (s.equals("fortress2") || s.equals("tower2")) {
            data.setLocFortress2(loc);
        } else if (s.equals("innerwall1")) {
            data.setLocInnerWall1(loc);
        } else if (s.equals("innerwall2")) {
            data.setLocInnerWall2(loc);
        }
        return null;
    }
}
