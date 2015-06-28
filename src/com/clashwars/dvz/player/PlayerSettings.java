package com.clashwars.dvz.player;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerSettings {

    //If this is false the player won't receive tips anymore.
    public boolean tips = true;

    //If this is false it won't give the player a warning when he logs in that the enjin account isn't synced.
    public boolean enjinWarning = true;

    //If this is true it will open the stats display with the last known settings
    public boolean statsDirect = false;

    //Sets the amount of death messages to display.
    public int dwarfDeathMessages = 1; //0=none, 1=all, 2=personal, 3=personal/assists
    public int monsterDeathMessages = 1; //0=none, 1=all, 2=personal, 3=personal/assists

    //These settings are stored for players for statistics.
    public int stat_categorySelected = 1;
    public int stat_statSelected = 1;
    public UUID stat_lookupPlayer = null;
    public UUID stat_comparePlayer = null;
    public Timestamp stat_firstTime = null;
    public Timestamp stat_secondTime = null;

    public PlayerSettings() {
        //--
    }

}
