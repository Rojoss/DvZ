package com.clashwars.dvz.workshop;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class WorkShop {

    protected DvZ dvz;

    protected WorkShopData wsData;
    protected UUID owner;

    public List<BukkitTask> runnables = new ArrayList<BukkitTask>();

    protected Cuboid cuboid;


    public WorkShop(UUID owner, WorkShopData wsData) {
        dvz = DvZ.inst();
        this.owner = owner;
        this.wsData = wsData;
    }


    /**
     * Try and build the workshop from the WorkshopData.
     * If it builded it will call onBuild() in the workshop class.
     * If it failed it will be printed to the console why it failed.
     * @param origin The location to build the workshop at. If this is null it will use the origin from config.
     * @return true if it builded successfully and false if something failed while building it.
     */
    public boolean build(Location origin) {
        //Load origin from config if not specified. If it is update data.
        if (origin == null) {
            origin = wsData.getOrigin();
        } else {
            wsData.setOrigin(origin);
        }

        //Validate the origin/world
        if (origin == null) {
            dvz.log("Failed at building " + dvz.getServer().getOfflinePlayer(owner).getName() + " his workshop. The origin/world it was in is null now!");
            return false;
        }

        //Validate the origin/world
        if (origin.getChunk() == null || !origin.getChunk().isLoaded()) {
            dvz.log("Failed at building " + dvz.getServer().getOfflinePlayer(owner).getName() + " his workshop. The chunk isn't loaded!");
            return false;
        }

        //Load the rotation and if it's not set generate a random one.
        int rotation = wsData.getRotation();
        if (rotation < 0 || rotation % 90 != 0) {
            rotation = CWUtil.random(new Integer[]{180, 270, 360});
            wsData.setRotation(rotation);
        }

        //Load the variant and if it's not set generate a random one.
        //The variant is the schematic id some workshops have multiple variants.
        int variantCount = wsData.getType().getClassClass().getIntOption("workshop-types") - 1;
        int variant = wsData.getVariant();
        if (variant < 0 || variant > variantCount) {
            variant = CWUtil.random(0, variantCount);
            wsData.setVariant(variant);
        }

        save();

        try {
            //Try and paste the schematic.
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(origin.getWorld(), CWWorldGuard.getSchematicFile("ws-" + wsData.getType().toString().toLowerCase() + "-" + variant), origin, true, rotation, true);

            //Create a new cuboid from the schematic clipboard.
            Location min = new Location(origin.getWorld(), origin.getBlockX() + cc.getOffset().getBlockX(), origin.getBlockY() + cc.getOffset().getBlockY(), origin.getBlockZ() + cc.getOffset().getBlockZ());
            cuboid = new Cuboid(min, cc.getWidth()-1, cc.getHeight()-1, cc.getLength()-1);

            if (cuboid == null) {
                dvz.log("Failed at building " + dvz.getServer().getOfflinePlayer(owner).getName() + " his workshop. Failed at creating the cuboid!");
                return false;
            }

            //Remove all entities in the cuboid if there are any.
            //For example if the server crashes the onDestroy() isn't called and the tailor workshop would still have the sheep.
            //This will just make sure that there wont be any entities in the workshop. (except players)
            List<Entity> entities = CWUtil.getNearbyEntities(cuboid.getCenterLoc(), cuboid.getWidth() + 5, null);
            for (Entity e : entities) {
                if (e instanceof Player) {
                    continue;
                }
                if (cuboid.contains(e)) {
                    e.remove();
                }
            }

            //Classes can override this to do stuff when the workshop is build.
            dvz.getWM().getWorkshop(owner).onBuild();

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
        dvz.log("Failed at building " + dvz.getServer().getOfflinePlayer(owner).getName() + " his workshop. Failed at pasting the schematic!");
        return false;
    }

    /**
     * If the workshop is build this will destroy it.
     * It will first call onDestroy() in the workshop class.
     * It will then remove all blocks and place back the wood/piston.
     * And then it will call onDestroyed() in the workshop class.
     * All data will stay in this class only the cuboid will be set to null so you can just do build() again.
     * @return true if it destroyed it and false if not. For example if the workshop wasn't build yet it will be false as there is nothing to destroy.
     */
    public boolean destroy() {
        if (cuboid != null && cuboid.getBlocks() != null) {
            //Classes can override this to do stuff before the workshop is destroyed.
            dvz.getWM().getWorkshop(owner).onDestroy();

            //Cancel all runnables.
            if (runnables != null && !runnables.isEmpty()) {
                for (BukkitTask runnable : runnables) {
                    runnable.cancel();
                }
            }
            runnables.clear();

            //Delete all blocks and set floor back to wood with a piston.
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

            //Classes can override this to do stuff after the workshop is destroyed.
            dvz.getWM().getWorkshop(owner).onDestroyed();

            //TODO: Try and put it in a better place (maybe create a custom event)
            //Remove banners for VIP's and give back items.
            BannerData data = dvz.getBannerCfg().getBanner(owner);
            if (data != null && data.getBannerLocations() != null && data.getBannerLocations().size() > 0 && getOwner() != null) {
                Product.VIP_BANNER.getItem(data.getBannerLocations().size()).setBaseColor(data.getBaseColor()).setPatterns(data.getPatterns()).giveToPlayer(getOwner());

                for (Vector loc : data.getBannerLocations()) {
                    Block block = getOwner().getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                    if (block.getType() == Material.STANDING_BANNER || block.getType() == Material.WALL_BANNER) {
                        block.setType(Material.AIR);
                    }
                }
                data.setBannerLocations(null);
                dvz.getBannerCfg().setBanner(owner, data);
                dvz.getBannerMenu().tempBanners.put(owner, data);
            }

            cuboid = null;
            return true;
        }
        return false;
    }


    /**
     * This method is to be overriden in the class specific workshop classes.
     * This is called after the workshop is actually build.
     */
    public void onBuild() {
    }

    /**
     * This methid is to be overridden in the class specific workshop classes.
     * This is called before the workshop is destroyed.
     */
    public void onDestroy() {
    }

    /**
     * This methid is to be overridden in the class specific workshop classes.
     * This is called after the workshop is destroyed.
     */
    public void onDestroyed() {
    }


    /**
     * Check if the workshop is build or not.
     * This is the same as checking if getCuboid() is null.
     * @return true if the workshop is build and has a cuboid and false if not.
     */
    public boolean isBuild() {
        return getCuboid() != null;
    }

    /**
     * Get the cuboid of the workshop.
     * If the workshop isn't build yet with build() this cuboid will be null.
     * If the building failed it will also be null.
     * @return Cuboid of the workshop containing all blocks etc.
     */
    public Cuboid getCuboid() {
        return cuboid;
    }


    /**
     * Get the origin of the cuboid.
     * It will first try and return the origin from config.
     * If that's null it will try and return the origin calculated from the cuboid.
     * If those both failed it will return null.
     * @return Location that is the origin of the workshop. (the block above the piston) Can also return null (see desc).
     */
    public Location getOrigin() {
        if (wsData.getOrigin() != null) {
            return wsData.getOrigin();
        }
        if (cuboid != null) {
            Location center = cuboid.getCenterLoc();
            center.setY(cuboid.getMinY() + 1);
            return center;
        }
        return null;
    }

    /**
     * Get the Player that owns this workshop.
     * Workshops can be created for offline players so this can be null if the player isn't online.
     * @return Player that owns the workshop or null if the Player isn't online or valid.
     */
    public Player getOwner() {
        return dvz.getServer().getPlayer(owner);
    }

    /**
     * Get the UUID of the player that owns this workshop.
     * @return UUID of player that owns the workshop.
     */
    public UUID getOwnerUUID() {
        return owner;
    }

    /**
     * Check if the given player owns this workshop.
     * @param player Player to check.
     * @return true if it's the owner false if not.
     */
    public boolean isOwner(OfflinePlayer player) {
        return owner.equals(player.getUniqueId());
    }

    /**
     * Check if the given uuid is the same as the uuid of the owner of this workshop.
     * @param uuid UUID to check.
     * @return true if it matches false if not.
     */
    public boolean isOwner(UUID uuid) {
        return owner.equals(uuid);
    }

    /**
     * Get the workshop type.
     * @return DvZClass for the type.
     */
    public DvzClass getType() {
        return wsData.getType();
    }

    /**
     * Get the workshop rotation in degrees. (90, 180, 360)
     * For some reason ladders aren't placed at 270 degrees.
     * @return int rotation in degrees
     */
    public int getRotation() {
        return wsData.getRotation();
    }

    /**
     * Get the workshop variant.
     * Some workshops have multiple schematic variants and this is the ID for that.
     * @return int variant id.
     */
    public int getVariant() {
        return wsData.getVariant();
    }

    /**
     * Save the workshop to config.
     * It will save the WorkshopData (wsdata) from this class to config.
     */
    public void save() {
        dvz.getWSCfg().setWorkShop(owner, wsData);
    }

}
