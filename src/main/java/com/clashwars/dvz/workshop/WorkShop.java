package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.DvzClass;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.UUID;

public class WorkShop {

    private DvZ dvz;

    private WorkShopData data;
    private UUID owner;


    public WorkShop(UUID owner, WorkShopData wsd) {
        this.owner = owner;
        this.data = wsd;
        dvz = DvZ.inst();
    }


    public boolean build() {
        try {
            //TODO: Name of workshop (maybe system with random workshop designs?)
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(getLocation().getWorld(), CWWorldGuard.getSchematicFile("workshop-test"), getLocation(), true, 0, true);
            setWidth(cc.getWidth());
            setLength(cc.getLength());
            setHeight(cc.getHeight());
            for (int x = 0; x < cc.getWidth(); x++) {
                for (int z = 0; z < cc.getLength(); z++) {
                    for (int y = 0; y < cc.getHeight(); y++) {
                        BaseBlock block = cc.getBlock(new Vector(x, y, z));
                        Location blockLoc = new Location(getLocation().getWorld(), getLocation().getBlockX() + cc.getOffset().getBlockX() + x,
                                getLocation().getBlockY() + cc.getOffset().getBlockY() + y,
                                getLocation().getBlockZ() + cc.getOffset().getBlockZ() + z);
                        if (block.getType() != 0) {
                            ParticleEffect.displayBlockCrack(blockLoc, block.getType(), (byte)block.getData(), 0.5f, 0.5f, 0.5f, 10);
                        }
                        if (block.getType() == 58) {
                            ParticleEffect.WITCH_MAGIC.display(blockLoc, 0.3f, 0.3f, 0.3f, 0.0001f, 50);
                            setCraftBlock(blockLoc);
                        }
                    }
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


    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }


    public Location getLocation() {
        return data.getLocation();
    }

    public void setLocation(Location location) {
        data.setLocation(location);
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
