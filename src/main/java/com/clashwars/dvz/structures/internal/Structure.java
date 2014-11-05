package com.clashwars.dvz.structures.internal;

import com.clashwars.dvz.DvZ;
import org.bukkit.event.Listener;

public class Structure implements Listener {

    protected DvZ dvz;

    public Structure() {
        this.dvz = DvZ.inst();
    }

}
