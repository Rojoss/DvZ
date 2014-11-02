package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;

import java.util.UUID;

public class FletcherWorkshop extends WorkShop {

    public FletcherWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    @Override
    public void onBuild() {
        Debug.bc("Fletcher workshop build!");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onRemove() {

    }
}
