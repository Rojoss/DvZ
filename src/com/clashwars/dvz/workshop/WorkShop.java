package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.VIP.BannerData;
import com.clashwars.dvz.classes.DvzClass;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.util.io.file.FilenameException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WorkShop {

    protected DvZ dvz;

    protected WorkShopData data;
    protected UUID owner;
    protected Cuboid cuboid;
    protected int rotation = 0;

    protected Block craftBlock;


    public WorkShop(UUID owner, WorkShopData wsd) {
        this.owner = owner;
        this.data = wsd;
        dvz = DvZ.inst();
    }


    public boolean build(Location origin) {
        List<Integer> degrees = Arrays.asList(new Integer[] {180,270,360});
        rotation = CWUtil.random(degrees);
        try {
            //Get a random workshop based on type.
            int typeID = CWUtil.random(0, data.getType().getClassClass().getIntOption("workshop-types")-1);
            //Try and paste the schematic.
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(origin.getWorld(), CWWorldGuard.getSchematicFile("ws-" + data.getType().toString().toLowerCase() + "-" + typeID), origin, true, rotation, true);

            //Get the min location from the schematic.
            Location min = new Location(origin.getWorld(), origin.getBlockX() + cc.getOffset().getBlockX(), origin.getBlockY() + cc.getOffset().getBlockY(), origin.getBlockZ() + cc.getOffset().getBlockZ());
            //Create a new cuboid from the schematic clipboard.
            cuboid = new Cuboid(min, cc.getWidth()-1, cc.getHeight()-1, cc.getLength()-1);
            data.setCuboid(cuboid);
            save();

            //Loop through the blocks and do particles at each block.
            List<Block> blocks = cuboid.getBlocks();
            for (Block block : blocks) {
                if (block.getType() != Material.AIR) {
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), (byte)block.getData()), 0.5f, 0.5f, 0.5f, 0.1f, 10, block.getLocation());
                }
            }
            return true;
        } catch (CommandException e) {
        } catch (FilenameException e) {
        } catch (com.sk89q.worldedit.world.DataException e) {
        } catch (MaxChangedBlocksException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean remove() {
        if (cuboid != null && cuboid.getBlocks() != null) {
            Location pistonLoc = getOrigin().add(0,-1,0);

            for (Block block : cuboid.getBlocks()) {
                block.setType(Material.AIR);
                if (block.getLocation().equals(pistonLoc)) {
                    block.setType(Material.PISTON_BASE);
                    block.setData((byte)1);
                } else if (block.getLocation().getY() == cuboid.getMinY()) {
                    block.setType(Material.WOOD);
                }
            }

            //Remove banners for VIP's and give back items.
            BannerData data = dvz.getBannerCfg().getBanner(owner);
            if (data != null) {
                Product.VIP_BANNER.getItem(data.getBannerLocations().size()).setBaseColor(data.getBaseColor()).setPatterns(data.getPatterns()).giveToPlayer(getOwner());

                for (Vector loc : data.getBannerLocations()) {
                    Block block = getOwner().getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    if (block.getType() == Material.STANDING_BANNER || block.getType() == Material.WALL_BANNER) {
                        block.setType(Material.AIR);
                    }
                }
                data.setBannerLocations(null);
                dvz.getBannerCfg().setBanner(owner, data);
            }

            cuboid = null;
            data = null;
            owner = null;
            return true;
        }
        return false;
    }


    public void onBuild() {
        //-- To be overridden
    }

    public void onLoad() {
        //-- To be overridden
    }

    public void onRemove() {
        craftBlock = null;
        //-- To be overridden
    }


    public Cuboid getCuboid() {
        if (cuboid == null) {
            cuboid = data.getCuboid();
        }
        return cuboid;
    }

    public void setCuboid() {
        if (cuboid != null) {
            data.setCuboid(cuboid);
        }
    }


    public Location getOrigin() {
        if (cuboid == null) {
            if (data == null) {
                return null;
            }
            cuboid = data.getCuboid();
        }
        Location center = cuboid.getCenterLoc();
        center.setY(cuboid.getMinY() + 1);
        return center;
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


    public DvzClass getType() {
        return data.getType();
    }

    public void setType(DvzClass type) {
        data.setType(type);
    }


    public void save() {
        dvz.getWSCfg().setWorkShop(owner, data);
    }

    public int getRotation() {
        return rotation;
    }


    public void setCraftBlock() {
        if (cuboid != null) {
            for (Block block : cuboid.getBlocks()) {
                if (block.getType() == Material.WORKBENCH) {
                    craftBlock = block;
                    break;
                }
            }
        }
    }
}
