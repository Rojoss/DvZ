package com.clashwars.dvz.workshop;

import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.WorkShopCfg;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorkshopManager {

    private DvZ dvz;
    private WorkShopCfg wsCfg;

    private Map<UUID, WorkShop> workshops = new HashMap<UUID, WorkShop>();

    public WorkshopManager(DvZ dvz) {
        this.dvz = dvz;
        this.wsCfg = dvz.getWSCfg();
        loadWorkshops();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (WorkShop ws : workshops.values()) {
                    ws.build(null);
                }
            }
        }.runTaskLater(dvz, 2);
    }


    /**
     * This will try and load all workshops from the config.
     * Once the workshop is loaded it will store the workshop in "workshops"
     * And it will try to build the workshop.
     */
    public void loadWorkshops() {
        Long t = System.currentTimeMillis();
        Map<UUID, WorkShopData> cfgWorkshops = wsCfg.getWorkShops();

        //Check if there are workshops in config.
        if (cfgWorkshops == null || cfgWorkshops.size() < 1) {
            dvz.log("No workshops loaded from config.");
            dvz.logTimings("WorkshopManager.loadWorkshops()[none]", t);
            return;
        }

        //Loop through all the workshops in config.
        for (UUID uuid : cfgWorkshops.keySet()) {
            loadWorkshop(uuid);
        }
        dvz.logTimings("WorkshopManager.loadWorkshops()", t);
    }


    /**
     * Try and load a player his workshop from the config.
     * Once the workshop is loaded it will store the workshop in "workshops"
     * @param uuid The uuid of the owner of the workshop.
     */
    public boolean loadWorkshop(UUID uuid) {
        Long t = System.currentTimeMillis();
        WorkShopData wsData = wsCfg.getWorkShops().get(uuid);
        if (wsData == null) {
            dvz.log("Failed at loading the workshop for player: " + dvz.getServer().getOfflinePlayer(uuid).getName() + " Invalid data!");
            dvz.logTimings("WorkshopManager.loadWorkshop()[invalid data]", t);
            return false;
        }

        if (wsData.getType() == null) {
            dvz.log("Failed at loading the workshop for player: " + dvz.getServer().getOfflinePlayer(uuid).getName() + " Invalid type!");
            dvz.logTimings("WorkshopManager.loadWorkshop()[invalid type]", t);
            return false;
        }

        //Create the actual Workshop class based on the workshop type.
        WorkShop ws = null;
        switch(wsData.getType()) {
            case MINER:
                ws = new MinerWorkshop(uuid, wsData);
                break;
            case FLETCHER:
                ws = new FletcherWorkshop(uuid, wsData);
                break;
            case TAILOR:
                ws = new TailorWorkshop(uuid, wsData);
                break;
            case ALCHEMIST:
                ws = new AlchemistWorkshop(uuid, wsData);
                break;
            case BAKER:
                ws = new BakerWorkshop(uuid, wsData);
                break;
        }

        //Check if it created the workshop.
        if (ws == null || ws.getType() != wsData.getType()) {
            dvz.log("Failed at loading the " + wsData.getType() + " workshop for player: " + dvz.getServer().getOfflinePlayer(uuid).getName());
            dvz.logTimings("WorkshopManager.loadWorkshop()[invalid]", t);
            return false;
        }

        //If the player somehow already has a workshop remove that one first.
        if (workshops.containsKey(uuid) && workshops.get(uuid) != null && workshops.get(uuid).isBuild()) {
            workshops.get(uuid).destroy();
        }

        workshops.put(uuid, ws);
        dvz.logTimings("WorkshopManager.loadWorkshop()", t);
        return true;
    }



    /**
     * Get the workshop of the given player.
     * It will first try get the workshop from the "workshops" map and if not found it will try and load it from config.
     * If it's still not found after that it will create a new if the player has a class.
     * If the player also has no class it will return null.
     * @param uuid The workshop owner uuid.
     * @return The workshop of the given player or null if it failed to load/create one.
     */
    public WorkShop getWorkshop(UUID uuid) {
        if (workshops.containsKey(uuid)) {
            return workshops.get(uuid);
        }
        loadWorkshop(uuid);
        if (workshops.containsKey(uuid)) {
            return workshops.get(uuid);
        }
        DvzClass dvzClass = dvz.getPM().getPlayer(uuid).getPlayerClass();
        if (dvzClass != null && dvzClass.getType() == ClassType.DWARF && !dvzClass.isBaseClass()) {
            WorkShopData wsData = new WorkShopData();
            wsData.setType(dvzClass);
            wsCfg.setWorkShop(uuid, wsData);
            loadWorkshop(uuid);
            return workshops.get(uuid);
        }
        return null;
    }

    /**
     * Get a map with all the loaded workshops.
     * @return Map<UUID, Workshop> with all workshops.
     */
    public Map<UUID, WorkShop> getWorkShops() {
        return workshops;
    }

    /**
     * Check if the given player has a workshop or not.
     * It does not check if the player has a workshop in the config.
     * @param uuid The uuid of the player to check.
     * @return true if the player has one false if not.
     */
    public boolean hasWorkshop(UUID uuid) {
        return workshops.containsKey(uuid);
    }



    /**
     * This will remove the workshop reference for the specified user.
     * It's recommended to first destroy the workshop as this will clear the entities and cancel scheduled tasks etc.
     * @param uuid The workshop owner uuid.
     * @param config If this is true it will also remove the workshop from the player in the config.
     */
    public void removeWorkshop(UUID uuid, boolean config) {
        if (workshops.containsKey(uuid)) {
            workshops.remove(uuid);
        }
        if (config) {
            wsCfg.removeWorkShop(uuid);
        }
    }

    /**
     * This will remove all workshop references in this manager.
     * It's recommended to first destroy all the workshops as this will clear the entities and cancel scheduled tasks etc.
     * @param config If this is true it will also clear the workshop config.
     */
    public void removeWorkshops(boolean config) {
        workshops.clear();
        if (config) {
            wsCfg.removeWorkShops();
        }
    }


    /**
     * Try and get the workshop at the specified location.
     * @param location The location to look for a workshop.
     * @return Workshop at the given location. If there is none it will return null.
     */
    public WorkShop locGetWorkShop(Location location) {
        for (WorkShop ws : workshops.values()) {
            if (ws.getCuboid() != null && ws.getCuboid().contains(location)) {
                return ws;
            }
        }
        return null;
    }

}
