package com.clashwars.dvz.maps;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.List;

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
        for (World world : dvz.getServer().getWorlds()) {
            if (world.getName().equalsIgnoreCase(mapName)) {
                return true;
            }
        }
        return false;
    }

    //Check if this map is in root ready to be loaded.
    public boolean isReadyToLoad() {
        List<File> dirs = CWUtil.getDirectories(new File("."));
        for (File dir : dirs) {
            if (dir.getName().equalsIgnoreCase(mapName)) {
                return true;
            }
        }
        return false;
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
        dvz.getMapCfg().setMap(mapName, data);
    }


    public Cuboid getCuboid(String name) {
        String s = name.toLowerCase();
        if (s.equals("shrinewall") || s.equals("wallshrine")) {
            return data.getShrineWall();
        } else if (s.equals("shrine1keep") || s.equals("shrinekeep1") || s.equals("keep1shrine") || s.equals("keepshrine1")) {
            return data.getShrine1Keep();
        } else if (s.equals("shrine2keep") || s.equals("shrinekeep2") || s.equals("keep2shrine") || s.equals("keepshrine2")) {
            return data.getShrine2Keep();
        } else if (s.equals("keep")) {
            return data.getKeep();
        } else if (s.equals("wall") || s.equals("bigwall")) {
            return data.getWall();
        } else if (s.equals("innerwall") || s.equals("keepwall")) {
            return data.getInnerWall();
        }
        return null;
    }

    public void setCuboid(String name, Cuboid cuboid) {
        String s = name.toLowerCase();
        if (s.equals("shrinewall") || s.equals("wallshrine")) {
            data.setShrineWall(cuboid);
        } else if (s.equals("shrine1keep") || s.equals("shrinekeep1") || s.equals("keep1shrine") || s.equals("keepshrine1")) {
            data.setShrine1Keep(cuboid);
        } else if (s.equals("shrine2keep") || s.equals("shrinekeep2") || s.equals("keep2shrine") || s.equals("keepshrine2")) {
            data.setShrine2Keep(cuboid);
        } else if (s.equals("keep")) {
            data.setKeep(cuboid);
        } else if (s.equals("wall") || s.equals("bigwall")) {
            data.setWall(cuboid);
        } else if (s.equals("innerwall") || s.equals("keepwall")) {
            data.setInnerWall(cuboid);
        }
        dvz.getMapCfg().setMap(mapName, data);
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
        }
        return null;
    }

    public void setLocation(String name, Location loc) {
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
        }
        dvz.getMapCfg().setMap(mapName, data);
    }
}
