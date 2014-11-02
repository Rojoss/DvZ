package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;

import java.util.UUID;

public class TailorWorkshop extends WorkShop {

    public TailorWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    @Override
    public void onBuild() {
        Debug.bc("Tailor workshop build!");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onRemove() {

    }
}
