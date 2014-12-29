package com.clashwars.dvz.abilities.extra;

import com.clashwars.cwcore.cuboid.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortalData {

    private Player owner;
    private Location location;
    private Location eggLoc;
    public List<UUID> downvotes = new ArrayList<UUID>();

    public PortalData(Player owner, Location location, Cuboid cuboid) {
        this.owner = owner;
        this.location = location;
        List<Block> eggs = cuboid.getBlocks(new Material[] {Material.DRAGON_EGG});
        eggLoc = eggs.get(0).getLocation();
    }


    public Player getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public Location getEgg() {
        return eggLoc;
    }
}
