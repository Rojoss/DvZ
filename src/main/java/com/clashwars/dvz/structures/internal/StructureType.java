package com.clashwars.dvz.structures.internal;

import com.clashwars.dvz.structures.*;

public enum StructureType {
    BASE(new Structure()),
    ENCHANT(new EnchantStruc()),
    FURNACE(new FurnaceStruc()),
    DEPOSIT(new DisposeStruc()),
    STORAGE(new StorageStruc());

    private Structure structureClass;

    StructureType(Structure structureClass) {
        this.structureClass = structureClass;
    }

    public Structure getStrucClass() {
        return structureClass;
    }
}
