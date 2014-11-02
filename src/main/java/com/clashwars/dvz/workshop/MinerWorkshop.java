package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;

import java.util.UUID;

public class MinerWorkshop extends WorkShop {

    public MinerWorkshop(UUID owner, WorkShopData wsd) {
        super(owner, wsd);
    }

    @Override
    public void onBuild() {
        Debug.bc("Miner workshop build!");
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onRemove() {

    }
}
