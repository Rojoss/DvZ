package com.clashwars.dvz.player;

public class PlayerSettings {

    //If this is false the player won't receive tips anymore.
    public boolean tips = true;

    //If this is false it won't give the player a warning when he logs in that the enjin account isn't synced.
    public boolean enjinWarning = true;

    //Sets the amount of death messages to display.
    public int dwarfDeathMessages = 1; //0=none, 1=all, 2=personal, 3=personal/assists
    public int monsterDeathMessages = 1; //0=none, 1=all, 2=personal, 3=personal/assists

    public PlayerSettings() {
        //--
    }

}
