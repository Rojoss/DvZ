package com.clashwars.dvz;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.GameCfg;
import com.clashwars.dvz.maps.DvzMap;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private DvZ dvz;
    private GameCfg gCfg;
    private Set<ShrineBlock> shrineBlocks = new HashSet<ShrineBlock>();
    private int dragonPower = 1;

    public GameManager(DvZ dvz) {
        this.dvz = dvz;
        gCfg = dvz.getGameCfg();

        //Load shrines if server crashed/restarted during game.
        if (dvz.getMM().getActiveMap() != null && (isStarted() || getState() == GameState.OPENED)) {
            populateShrines();
        }
    }


    public void resetGame(boolean nextGame, String mapName) {
        setState(GameState.SETUP);
        if (!nextGame) {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &c&lDvZ has ended! &7=========="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7Come back again later for more DvZ!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7Make sure to follow us on Twitch to know when DvZ starts!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &9&lhttp://twitch.tv/clashwars"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7Use &6&l/leave &7to go to the pvp server!"));
            setState(GameState.CLOSED);
        } else {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ is resetting! &7=========="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7A new game will be starting soon."));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7If you're not watching the stream yet make sure to do!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));
        }

        //Reset data
        for (CWPlayer cwp : dvz.getPM().getPlayers().values()) {
            cwp.undisguise();
            cwp.reset();
            cwp.resetData();
        }
        shrineBlocks.clear();
        dvz.getPM().removePlayers(true);
        dvz.getPM().removeWorkshops(true);
        gCfg.STORAGE_PRODUCTS.clear();
        setDragonPlayer(null);
        setDragonType(null);
        setSpeed(0);
        //TODO: Reset all other data
        Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5All data has been removed/reset"));

        if (dvz.getMM().getActiveMap() != null) {
            //Tp all players to default world.
            for (Player player : dvz.getMM().getActiveMap().getWorld().getPlayers()) {
                player.teleport(dvz.getCfg().getDefaultWorld().getSpawnLocation());
            }

            //Remove the map
            if (dvz.getMM().removeActiveMap()) {
                Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5Previous map has been removed"));
            } else {
                Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &4Failed at removing previous map"));
            }
        }

        //Load in new map.
        if (dvz.getMM().loadMap(mapName)) {
            Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5New map loaded."));
        } else {
            Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &4Failed at loading new map"));
        }
    }

    public void openGame() {
        if (dvz.getMM().getActiveMap() == null || !dvz.getMM().getActiveMap().isLoaded()) {
            dvz.getMM().loadMap(null);
        }

        if (dvz.getMM().getActiveMap() == null || !dvz.getMM().getActiveMap().isLoaded()) {
            Util.broadcastAdmins(Util.formatMsg("&6The game couldn't be opened because there is no map loaded."));
            return;
        }

        Set<String> setupOptions = dvz.getMM().isSetProperly(dvz.getMM().getActiveMap());
        if (!setupOptions.isEmpty()) {
            Util.broadcastAdmins(CWUtil.integrateColor("&cCould not open the game because the map is not set up properly."));
            Util.broadcastAdmins(CWUtil.integrateColor("&4Missing&8: &c" + CWUtil.implode(setupOptions.toArray(new String[dvz.getMM().getMaps().size()]), "&8, &c")));
            //return;
        }

        if (!populateShrines()) {
            Util.broadcastAdmins(CWUtil.integrateColor("&cCould not open the game because the map is not set up properly."));
            Util.broadcastAdmins(CWUtil.integrateColor("&cMissing end portal frames between shrine locations."));
            Util.broadcastAdmins(CWUtil.integrateColor("&cThere has to be at least 1 shrine block for the wall and one for the keep."));
        }

        setState(GameState.OPENED);
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has opened! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7The game will be starting soon so please wait."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Beat the parkour to get 3 classes instead of 2!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7If you're not watching the stream yet make sure to do!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));

        //Tp all players to active world.
        for (Player player : dvz.getServer().getOnlinePlayers()) {
            dvz.getPM().getPlayer(player).reset();
            player.teleport(dvz.getMM().getUsedWorld().getSpawnLocation());
        }
    }


    public void startGame() {
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has started! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You can now choose a dwarf class!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You get one and a half day to prepare."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Do your tasks and get fully equipped!."));

        for (Player player : dvz.getServer().getOnlinePlayers()) {
            dvz.getPM().getPlayer(player).giveClassItems(ClassType.DWARF, false);
        }

        getUsedWorld().setTime(23000);
        setState(GameState.DAY_ONE);
    }


    public void createDragon() {
        Player player = getDragonPlayer();
        if (player == null) {
            for (Player p : dvz.getServer().getOnlinePlayers()) {
                if (p.isOp() || p.hasPermission("dvz.admin")) {
                    p.sendMessage(Util.formatMsg("&cNo dragon was set up so you have been set as dragon!"));
                    player = p;
                    setDragonPlayer(p.getUniqueId());
                    if (getDragonType() == null) {
                        setDragonType(DvzClass.FIREDRAGON);
                    }
                }
            }
        }
        if (player == null) {
            Bukkit.broadcastMessage(Util.formatMsg("&4&lThere is no staff member to player the dragon. &c&lPlease wait..."));
            Bukkit.broadcastMessage(Util.formatMsg("&7If there has been no dragon when the sun rises again, the monsters will be automatically released. Random dwarves will be killed!"));
            return;
        }
        DvzClass dragonType = getDragonType();
        setDragonType(dragonType);

        Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe " + dragonType.getClassClass().getDisplayName() + " arises! &7======="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Stop working and get to the walls!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Kill the dragon and become the &bDragonSlayer&7!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &8Remember: &7If you die you become a monster."));

        CWPlayer cwp = dvz.getPM().getPlayer(player);
        cwp.setClass(dragonType);
        player.setAllowFlight(true);
        player.setFlying(true);
        setState(GameState.DRAGON);
    }


    public void releaseMonsters(boolean doExecution) {
        if (doExecution) {
            List<CWPlayer> dwarves = dvz.getPM().getPlayers(ClassType.DWARF);
            List<CWPlayer> monsters = dvz.getPM().getPlayers(ClassType.MONSTER);
            List<CWPlayer> killed = new ArrayList<CWPlayer>();

            int dwarvesToKill = (int)Math.round((monsters.size() + dwarves.size()) * dvz.getCfg().MONSTER_PERCENTAGE_MIN);
            int attempts = 20;
            for (int i = 0; i < dwarvesToKill && attempts > 0; i++) {
                CWPlayer randomDwarf = CWUtil.random(dwarves);
                if (killed.contains(randomDwarf)) {
                    i--;
                    attempts--;
                    continue;
                }
                attempts = 20;
                killed.add(randomDwarf);
                randomDwarf.getPlayer().setHealth(0);
                Bukkit.broadcastMessage(Util.formatMsg("&8" + randomDwarf.getName() + " &7has been killed!"));
            }
        }
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7===== &a&lThe monsters have been released! &7====="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Get to the front wall to hold the monsters of!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Don't let them break the shrine at the wall."));
        setState(GameState.MONSTERS);
    }


    public void captureWall() {
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7===== &a&lThe wall has been captured! &7====="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Get back to the fortress to defend the main shrine!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Monsters will now spawn at the wall."));
        setState(GameState.MONSTERS_WALL);
        //TODO: Replace blocks in wall to nether style.
    }


    public void stopGame(boolean force, String reason) {
        if (force) {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7===== &4&lThe game has been stopped! &7====="));
            if (reason != null && !reason.isEmpty()) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &8Reason: &7" + reason));

            }
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7The game will be closed soon."));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7Don't log off yet! There might be another round."));
        } else {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7=== &a&lThe monsters have destroyed the shrine! &7==="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7The game will be closed soon."));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Don't log off yet! There might be another round."));
        }
        setState(GameState.ENDED);
    }


    public boolean populateShrines() {
        boolean keepShrine = populateShrines("shrine1", "shrine2", ShrineType.KEEP);
        boolean wallShrine = populateShrines("shrinewall1", "shrinewall2", ShrineType.WALL);
        if (keepShrine && wallShrine) {
            return true;
        }
        return false;
    }

    private boolean populateShrines(String loc1, String loc2, ShrineType type) {
        boolean foundBlocks = false;
        DvzMap map = dvz.getMM().getActiveMap();
        Set<Block> shrineKeepBlocks = CWUtil.findBlocksInArea(map.getLocation(loc1), map.getLocation(loc2), new Material[]{Material.ENDER_PORTAL_FRAME});
        for (Block shrineBlock : shrineKeepBlocks) {
            shrineBlocks.add(new ShrineBlock(shrineBlock.getLocation(), type));
            foundBlocks = true;
        }
        return foundBlocks;
    }



    public World getUsedWorld() {
        return dvz.getMM().getUsedWorld();
    }

    public GameState getState() {
        return GameState.valueOf(gCfg.GAME__STATE);
    }

    public void setState(GameState state) {
        gCfg.GAME__STATE = state.toString();
        gCfg.save();
    }

    public boolean isStarted() {
        return (getState() != GameState.CLOSED && getState() != GameState.ENDED && getState() != GameState.SETUP);
    }

    public boolean isDwarves() {
        return (getState() == GameState.DAY_ONE || getState() == GameState.DAY_TWO || getState() == GameState.NIGHT_ONE);
    }

    public boolean isMonsters() {
        return (getState() == GameState.MONSTERS || getState() == GameState.MONSTERS_WALL);
    }


    public int getSpeed() {
        return gCfg.GAME__SPEED;
    }

    public void setSpeed(int speed) {
        gCfg.GAME__SPEED = speed;
        gCfg.save();
    }


    public DvzClass getDragonType() {
        DvzClass dvzClass = DvzClass.fromString(gCfg.GAME__DRAGON_TYPE);
        if (dvzClass == null) {
            Set<DvzClass> dragons = dvz.getCM().getClasses(ClassType.DRAGON).keySet();
            dvzClass = CWUtil.random(new ArrayList<DvzClass>(dragons));
        }
        return dvzClass;
    }

    public void setDragonType(DvzClass dvzClass) {
        if (dvzClass == null) {
            gCfg.GAME__DRAGON_TYPE = "";
        } else {
            gCfg.GAME__DRAGON_TYPE = dvzClass.toString();
        }
        gCfg.save();
    }


    public Player getDragonPlayer() {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(gCfg.GAME__DRAGON_PLAYER);
        } catch (Exception e) {
            return null;
        }
        if (uuid == null) {
            return null;
        }
        return dvz.getServer().getPlayer(uuid);
    }

    public void setDragonPlayer(UUID uuid) {
        if (uuid == null) {
            gCfg.GAME__DRAGON_PLAYER = "";
        } else {
            gCfg.GAME__DRAGON_PLAYER = uuid.toString();
        }
        gCfg.save();
    }

    public int getDragonPower() {
        return dragonPower;
    }

    public void setDragonPower(int dragonPower) {
        this.dragonPower = dragonPower;
    }

    public Set<ShrineBlock> getShrineBlocks() {
        return shrineBlocks;
    }

    public Set<ShrineBlock> getShrineBlocks(ShrineType type) {
        Set<ShrineBlock> blocks = new HashSet<ShrineBlock>();
        for (ShrineBlock block : shrineBlocks) {
            if (block.getType() == type) {
                blocks.add(block);
            }
        }
        return blocks;
    }

    public ShrineBlock getShrineBlock(Location loc) {
        for (ShrineBlock block : shrineBlocks) {
            if (block.getLocation().getBlockX() == loc.getBlockX() && block.getLocation().getBlockY() == loc.getBlockY() && block.getLocation().getBlockZ() == loc.getBlockZ()) {
                return block;
            }
        }
        return null;
    }
}
