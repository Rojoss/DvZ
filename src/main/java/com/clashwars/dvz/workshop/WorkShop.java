package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.Util;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorkShop {

    private DvZ dvz;

    private WorkShopData data;
    private UUID owner;
    private Vector location2 = null;
    private Vector min = null;
    private Vector max = null;


    public WorkShop(UUID owner, WorkShopData wsd) {
        this.owner = owner;
        this.data = wsd;
        dvz = DvZ.inst();
    }


    public boolean build() {
        try {
            //TODO: Name of workshop (maybe system with random workshop designs?)
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(getCenter().getWorld(), CWWorldGuard.getSchematicFile("workshop-test"), getCenter(), true, 0, true);
            setWidth(cc.getWidth());
            setLength(cc.getLength());
            setHeight(cc.getHeight());
            //Set location at 0,0,0 point in schematic
            setLocation(new Location(getCenter().getWorld(), getCenter().getBlockX() + cc.getOffset().getBlockX(), getCenter().getBlockY() + cc.getOffset().getBlockY(),
                    getCenter().getBlockZ() + cc.getOffset().getBlockZ()));

            //Loop through the cuboid and check for a workbench and do particles etc.
            Set<Block> blocks = getBlocks();
            for (Block block : blocks) {
                //Particle for each block.
                if (block.getType() != Material.AIR) {
                    ParticleEffect.displayBlockCrack(block.getLocation(), block.getTypeId(), (byte)block.getData(), 0.5f, 0.5f, 0.5f, 10);
                }
                if (block.getType() == Material.WORKBENCH) {
                    ParticleEffect.WITCH_MAGIC.display(block.getLocation(), 0.3f, 0.3f, 0.3f, 0.0001f, 50);
                    setCraftBlock(block.getLocation());
                }
            }
            return true;
        } catch (FilenameException e) {
        } catch (CommandException e) {
        } catch (DataException e) {
        } catch (MaxChangedBlocksException e) {
        } catch (IOException e) {
        }
        return false;
    }


    public Set<org.bukkit.util.Vector> getVectors() {
        Set<org.bukkit.util.Vector> vectors = new HashSet<org.bukkit.util.Vector>();
        int xPos = getLocation().getBlockX();
        int yPos = getLocation().getBlockY();
        int zPos = getLocation().getBlockZ();
        for (int x = 0; x < getWidth(); x++) {
            for (int z = 0; z < getLength(); z++) {
                for (int y = 0; y < getHeight(); y++) {
                    vectors.add(new org.bukkit.util.Vector(xPos + x, yPos + y, zPos + z));
                }
            }
        }
        return vectors;
    }

    public Set<Block> getBlocks() {
        Set<Block> blocks = new HashSet<Block>();
        int xPos = getLocation().getBlockX();
        int yPos = getLocation().getBlockY();
        int zPos = getLocation().getBlockZ();
        World world = getLocation().getWorld();
        for (int x = 0; x < getWidth(); x++) {
            for (int z = 0; z < getLength(); z++) {
                for (int y = 0; y < getHeight(); y++) {
                    blocks.add(world.getBlockAt(xPos + x, yPos + y, zPos + z));
                }
            }
        }
        return blocks;
    }


    public boolean isLocWithinWorkShop(Location loc) {
        return isLocWithinWorkShop(loc, false);
    }

    public boolean isLocWithinWorkShop(Location loc, boolean recalculate) {
        getMin(recalculate);
        getMax(recalculate);
        if (loc.getX() >= min.getX() && loc.getX() <= max.getX() && loc.getY() >= min.getY() && loc.getY() <= max.getY() && loc.getZ() >= min.getZ() && loc.getZ() <= max.getZ()) {
            return true;
        }
        return false;
    }


    public Vector getLocation2() {
        return getLocation2(false);
    }

    public Vector getLocation2(boolean recalculate) {
        if (recalculate || location2 == null) {
            location2 = new Vector(getWidth() + getLocation().getBlockX(), getHeight() + getLocation().getBlockY(), getLength() + getLocation().getBlockZ());
        }
        return location2;
    }


    public Vector getMin() {
        return getMin(false);
    }

    public Vector getMin(boolean recalculate) {
        if (recalculate || min == null) {
            Location loc1 = getLocation();
            Vector loc2 = getLocation2(true);
            min = new Vector(Math.min(loc1.getBlockX(), loc2.getBlockX()), Math.min(loc1.getBlockY(), loc2.getBlockY()), Math.min(loc1.getBlockZ(), loc2.getBlockZ()));
        }
        return min;
    }


    public Vector getMax() {
        return getMax(false);
    }

    public Vector getMax(boolean recalculate) {
        if (recalculate || max == null) {
            Location loc1 = getLocation();
            Vector loc2 = getLocation2(true);
            max = new Vector(Math.max(loc1.getBlockX(), loc2.getBlockX()) -1, Math.max(loc1.getBlockY(), loc2.getBlockY()) -1, Math.max(loc1.getBlockZ(), loc2.getBlockZ()) -1);
    }
        return max;
    }



    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }

    public boolean isOwner(Player player) {
        return getOwner().getName().equalsIgnoreCase(player.getName());
    }

    public boolean isOwner(UUID uuid) {
        return (owner.compareTo(uuid) == 0);
    }


    public Location getLocation() {
        return data.getLocation();
    }

    public void setLocation(Location location) {
        data.setLocation(location);
    }


    public Location getCenter() {
        return data.getCenter();
    }

    public void setCenter(Location center) {
        data.setCenter(center);
    }


    public int getWidth() {
        return data.getWidth();
    }

    public void setWidth(int amt) {
        data.setWidth(amt);
    }

    public int getLength() {
        return data.getLength();
    }

    public void setLength(int amt) {
        data.setLength(amt);
    }

    public int getHeight() {
        return data.getHeight();
    }

    public void setHeight(int amt) {
        data.setHeight(amt);
    }


    public Location getCraftBlock() {
        return data.getCraftBlock();
    }

    public void setCraftBlock(Location craftBlock) {
        data.setCraftBlock(craftBlock);
    }


    public DvzClass getType() {
        return data.getType();
    }

    public void setType(DvzClass type) {
        data.setType(type);
    }


    public void save() {
        dvz.getWSCfg().setWorkShop(owner, data);
    }

}
