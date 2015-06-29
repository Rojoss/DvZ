package com.clashwars.dvz;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.packet.Title;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.VIP.BannerData;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.dwarves.bonus.Camouflage;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.GameCfg;
import com.clashwars.dvz.maps.DvzMap;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.runnables.DragonRunnable;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.structures.StorageStruc;
import com.clashwars.dvz.structures.internal.StructureType;
import com.clashwars.dvz.util.Util;
import com.clashwars.dvz.workshop.WorkShop;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GameManager {

    private DvZ dvz;
    private GameCfg gCfg;
    private Set<ShrineBlock> shrineBlocks = new HashSet<ShrineBlock>();
    private int dragonPower = 1;
    private float monsterPerc = 0;
    Long startTime;

    public GameManager(DvZ dvz) {
        this.dvz = dvz;
        gCfg = dvz.getGameCfg();

        //Load shrines if server crashed/restarted during game.
        if (dvz.getMM().getActiveMap() != null && (isStarted() || getState() == GameState.OPENED)) {
            populateShrines();
        }

        if (gCfg.GAME__START_TIME != null && gCfg.GAME__START_TIME > 0) {
            startTime = gCfg.GAME__START_TIME;
        }
    }


    public void resetGame(boolean nextGame, String mapName) {
        Long t = System.currentTimeMillis();
        setState(GameState.SETUP);
        if (!nextGame) {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &c&lDvZ has ended! &7=========="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7Come back again later for more DvZ!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7Make sure to follow us on Twitch to know when DvZ starts!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &9&lhttp://twitch.tv/clashwars"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &9&lhttp://clashwars.com"));
            setState(GameState.CLOSED);
            Title title = new Title("&c&lDvZ has ended!", "&7Come back again later for more DvZ!", 10, 500, 30);
            title.setTimingsToTicks();
            title.broadcast();
        } else {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ is resetting! &7=========="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7A new game will be starting soon."));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7If you're not watching the stream yet make sure to do!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));
            Title title = new Title("&a&lDvZ is resetting!", "&7A new game will be starting soon.", 10, 100, 30);
            title.setTimingsToTicks();
            title.broadcast();
        }

        //Reset data
        Collection<Player> players = (Collection<Player>)dvz.getServer().getOnlinePlayers();
        for (Player player : players) {
            CWPlayer cwp = dvz.getPM().getPlayer(player);
            cwp.undisguise();
            cwp.reset();
            cwp.resetData();
        }
        dvz.getPM().removePlayers(true);

        for (ShrineBlock shrineBlock : shrineBlocks) {
            shrineBlock.remove();
        }
        shrineBlocks.clear();

        Collection<WorkShop> workshops = new HashMap<UUID, WorkShop>(dvz.getWM().getWorkShops()).values();
        for (WorkShop ws : workshops) {
            ws.destroy();
        }
        dvz.getWM().removeWorkshops(true);

        ((StorageStruc)StructureType.STORAGE.getStrucClass()).reset();
        gCfg.STORAGE_PRODUCTS.clear();

        setDragonPlayer(null);
        setDragonType(null);
        resetDragonSlayer();
        setSpeed(0);

        HashMap<UUID, BannerData> banners = new HashMap<UUID, BannerData>(dvz.getBannerCfg().getBanners());
        for (Map.Entry<UUID, BannerData> banner : banners.entrySet()) {
            banner.getValue().setBannerLocations(null);
            banner.getValue().setGiven(false);
            dvz.getBannerCfg().setBanner(banner.getKey(), banner.getValue());
            dvz.getBannerMenu().tempBanners.put(banner.getKey(), banner.getValue());
        }

        Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5All data has been removed/reset"));

        if (dvz.getMM().getActiveMap() != null) {
            //Tp all players to default world.
            Collection<Player> onlinePlayers = (Collection<Player>)dvz.getServer().getOnlinePlayers();
            for (Player player : onlinePlayers) {
                player.teleport(dvz.getCfg().getDefaultWorld().getSpawnLocation());
            }

            //Remove the map
            if (dvz.getMM().removeActiveMap()) {
                Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5Previous map has been removed"));
            } else {
                Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &4Failed at removing previous map. Trying again..."));
                if (dvz.getMM().removeActiveMap()) {
                    Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5Previous map has been removed"));
                } else {
                    Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &4&lFailed at removing previous map!"));
                }
            }
        }

        //Load in new map.
        if (dvz.getMM().loadMap(mapName)) {
            Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5New map loaded."));
        } else {
            Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &4Failed at loading new map. Trying again..."));
            if (dvz.getMM().loadMap(mapName)) {
                Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &5New map loaded."));
            } else {
                Util.broadcastAdmins(Util.formatMsg("&6Reset progress&8: &4&lFailed at loading new map!"));
            }
        }
        dvz.logTimings("GameManager.resetGame()", t);
    }

    public void openGame() {
        Long t = System.currentTimeMillis();
        if (dvz.getMM().getActiveMap() == null || !dvz.getMM().getActiveMap().isLoaded()) {
            dvz.getMM().loadMap(null);
        }

        if (dvz.getMM().getActiveMap() == null || !dvz.getMM().getActiveMap().isLoaded()) {
            Util.broadcastAdmins(Util.formatMsg("&6The game couldn't be opened because there is no map loaded."));
            dvz.logTimings("GameManager.openGame()[no map]", t);
            return;
        }

        Set<String> setupOptions = dvz.getMM().isSetProperly(dvz.getMM().getActiveMap());
        if (!setupOptions.isEmpty()) {
            Util.broadcastAdmins(CWUtil.integrateColor("&cCould not open the game because the map is not set up properly."));
            Util.broadcastAdmins(CWUtil.integrateColor("&4Missing&8: &c" + CWUtil.implode(setupOptions.toArray(new String[dvz.getMM().getMaps().size()]), "&8, &c")));
            dvz.logTimings("GameManager.openGame()[map invalid]", t);
            return;
        }

        if (!populateShrines()) {
            Util.broadcastAdmins(CWUtil.integrateColor("&cCould not open the game because the map is not set up properly."));
            Util.broadcastAdmins(CWUtil.integrateColor("&cMissing end portal frames between shrine locations."));
            Util.broadcastAdmins(CWUtil.integrateColor("&cThere has to be at least 1 shrine block for the wall and one for the keep."));
            dvz.logTimings("GameManager.openGame()[map invalid]", t);
            return;
        }

        setState(GameState.OPENED);
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has opened! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7The game will be starting soon so please wait."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Beat the parkour to get 3 classes instead of 2!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7If you're not watching the stream yet make sure to do!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));
        Title title = new Title("&a&lDvZ has opened!", "&7The game will be starting soon so please wait.", 10, 100, 30);
        title.setTimingsToTicks();
        title.broadcast();

        dvz.getServer().dispatchCommand(dvz.getServer().getConsoleSender(), "hd reload");

        if (dvz.getSql() != null) {
            dvz.getSM().clearLocalStats();
        }

        //Tp all players to active world.
        Collection<Player> players = (Collection<Player>)dvz.getServer().getOnlinePlayers();
        for (Player player : players) {
            dvz.getPM().getPlayer(player).reset();
            player.teleport(dvz.getMM().getUsedWorld().getSpawnLocation());
        }
        dvz.logTimings("GameManager.openGame()", t);
    }


    public void startGame() {
        Long t = System.currentTimeMillis();
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has started! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You can now choose a dwarf class!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You get one and a half day to prepare."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Do your tasks and get fully equipped!."));
        Title title = new Title("&a&lDvZ has started!", "&7You can now choose a dwarf class!", 10, 50, 30);
        title.setTimingsToTicks();
        title.broadcast();

        gCfg.GAME__START_TIME = System.currentTimeMillis();
        startTime = System.currentTimeMillis();
        
        Collection<Player> players = (Collection<Player>)dvz.getServer().getOnlinePlayers();
        for (Player player : players) {
            dvz.getPM().getPlayer(player).giveClassItems(ClassType.DWARF, false, -1);
        }

        getUsedWorld().setTime(23000);
        setState(GameState.DAY_ONE);
        dvz.logTimings("GameManager.startGame()", t);
    }


    public void createDragon(boolean force) {
        Long t = System.currentTimeMillis();
        Player player = getDragonPlayer();
        if (player == null) {
            if (force) {
                dvz.logTimings("GameManager.createDragon()[invalid player1]", t);
                return;
            }
            Collection<Player> players = (Collection<Player>)dvz.getServer().getOnlinePlayers();
            for (Player p : players) {
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
        if (force && player == null) {
            dvz.logTimings("GameManager.createDragon()[invalid player2]", t);
            return;
        }
        if (player == null) {
            Bukkit.broadcastMessage(Util.formatMsg("&4&lThere is no staff member to player the dragon. &c&lPlease wait..."));
            Bukkit.broadcastMessage(Util.formatMsg("&7If there has been no dragon when the sun rises again, the monsters will be automatically released. Random dwarves will be killed!"));
            dvz.logTimings("GameManager.createDragon()[invalid player3]", t);
            return;
        }
        DvzClass dragonType = getDragonType();
        setDragonType(dragonType);

        Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe " + dragonType.getClassClass().getDisplayName() + " &a&larises! &7======="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Stop working and get to the walls!"));
        if (getDragonPlayer() == null) {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Kill the dragon and become the &bDragonSlayer&7!"));
        }
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &8Remember: &7If you die you become a monster."));

        Title title;
        if (getDragonPlayer() == null) {
            title = new Title("&a&lThe " + dragonType.getClassClass().getDisplayName() + " &a&larises!", "&7Kill the dragon and become the &bDragonSlayer&7!", 10, 50, 30);
        } else {
            title = new Title("&a&lThe " + dragonType.getClassClass().getDisplayName() + " &a&larises!", "", 10, 50, 30);
        }
        title.setTimingsToTicks();
        title.broadcast();

        CWPlayer cwp = dvz.getPM().getPlayer(player);
        cwp.setClass(dragonType, true);
        player.setAllowFlight(true);
        player.setFlying(true);
        if (!force) {
            setState(GameState.DRAGON);
        }

        Camouflage.removeAllBlocks();

        final Player dragonPlayer = player;
        new BukkitRunnable() {
            @Override
            public void run() {
                double health = 20 + dvz.getPM().getPlayers(ClassType.DWARF, true, true).size() * 2;
                dragonPlayer.setMaxHealth(health);
                dragonPlayer.setHealth(health);
            }
        }.runTaskLater(dvz, 60);
        new DragonRunnable(dvz).runTaskTimer(dvz, 20, 20);
        dvz.logTimings("GameManager.createDragon()", t);
    }


    public void releaseMonsters(boolean doExecution) {
        Long t = System.currentTimeMillis();
        if (doExecution) {
            List<CWPlayer> dwarves = dvz.getPM().getPlayers(ClassType.DWARF, false, true);
            List<CWPlayer> monsters = dvz.getPM().getPlayers(ClassType.MONSTER, false, true);
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
        Title title = new Title("&c&lMonsters Released!", "&7Get to the front wall to hold the monsters of!", 10, 50, 30);
        title.setTimingsToTicks();
        title.broadcast();
        setState(GameState.MONSTERS);
        dvz.logTimings("GameManager.releaseMonsters()", t);
    }


    public void captureWall() {
        Long t = System.currentTimeMillis();
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7===== &a&lThe wall has been captured! &7====="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Get back to the keep to defend the main shrine!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Monsters will now spawn at the wall."));
        Title title = new Title("&c&lWall captured!", "&7Get back to the keep to defend the keep shrine!", 10, 50, 30);
        title.setTimingsToTicks();
        title.broadcast();
        setState(GameState.MONSTERS_WALL);

        Cuboid wall = dvz.getMM().getActiveMap().getCuboid("wall");
        List<Block> blocks = wall.getBlocks();
        for (Block block : blocks) {
            if (block.getType() == Material.SMOOTH_BRICK) {
                block.setType(Material.NETHER_BRICK);
            }
            if (block.getType() == Material.SMOOTH_STAIRS) {
                block.setType(Material.NETHER_BRICK_STAIRS);
            }
            if (block.getType() == Material.STONE_SLAB2) {
                if (block.getData() <= 7) {
                    block.setData((byte)6);
                }
                if (block.getData() > 7) {
                    block.setData((byte)14);
                }
            }
            if (block.getType() == Material.FENCE) {
                block.setType(Material.NETHER_FENCE);
            }

            if (block.getType() == Material.COBBLE_WALL) {
                block.setType(Material.NETHER_FENCE);
            }
        }
        dvz.logTimings("GameManager.captureWall()", t);
    }

    public void captureFirstKeepShrine() {
        Long t = System.currentTimeMillis();
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7== &a&lThe bottom of the keep has been captured! &7=="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Defend the shrine at the top!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Monsters will now spawn in the keep!"));
        Title title = new Title("&c&lKeep captured!", "&7Get to the top of the keep to defend the final shrine!", 10, 50, 30);
        title.setTimingsToTicks();
        title.broadcast();
        setState(GameState.MONSTERS_KEEP);
        dvz.logTimings("GameManager.captureFirstKeepShrine()", t);
    }


    public void stopGame(boolean force, String reason) {
        Long t = System.currentTimeMillis();
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
        Title title = new Title("&c&lGame Ended!", "&4Don't log off yet! &7There might be another round.", 10, 100, 30);
        title.setTimingsToTicks();
        title.broadcast();
        setState(GameState.ENDED);

        dvz.getSM().changeLocalStatVal(StatType.GENERAL_GAME_TIME, ((int)(System.currentTimeMillis() - startTime)));

        //Save local statistics to database and create a new game record
        dvz.getSM().uploadLocalStats();

        dvz.logTimings("GameManager.stopGame()", t);
    }


    public boolean populateShrines() {
        Long t = System.currentTimeMillis();
        boolean keep1Shrine = populateShrines("shrinekeep1", ShrineType.KEEP_1);
        boolean keep2Shrine = populateShrines("shrinekeep2", ShrineType.KEEP_2);
        boolean wallShrine = populateShrines("shrinewall", ShrineType.WALL);
        if (keep1Shrine && keep2Shrine && wallShrine) {
            return true;
        }
        dvz.logTimings("GameManager.populateShrines()", t);
        return false;
    }

    private boolean populateShrines(String cuboidStr, ShrineType type) {
        boolean foundBlocks = false;
        DvzMap map = dvz.getMM().getActiveMap();
        Cuboid cuboid = map.getCuboid(cuboidStr);
        if (cuboid != null) {
            List<Block> portalBlocks = cuboid.getBlocks(new Material[] {Material.ENDER_PORTAL_FRAME});
            for (Block shrineBlock : portalBlocks) {
                shrineBlocks.add(new ShrineBlock(shrineBlock.getLocation(), type));
                foundBlocks = true;
            }
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
        return (getState() == GameState.MONSTERS || getState() == GameState.MONSTERS_WALL|| getState() == GameState.MONSTERS_KEEP);
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

    public void setDragonSlayer(Player player) {
        Long t = System.currentTimeMillis();
        gCfg.GAME__DRAGON_SLAYER = player.getUniqueId().toString();
        gCfg.save();

        if (dvz.getPerms() != null) {
            dvz.getPerms().playerAdd(player, "prefix.dragonslayer");
        }

        dvz.getBoard().getTeam("dragonslayer" + dvz.getPM().getPlayer(player).getTeamSuffix()).addPlayer(player);

        Ability.HORN.getAbilityClass().getCastItem().giveToPlayer(player);

        player.setMaxHealth(30);
        player.setHealth(player.getHealth() + 10);
    }

    public void resetDragonSlayer() {
        Long t = System.currentTimeMillis();
        if (gCfg.GAME__DRAGON_SLAYER == null || gCfg.GAME__DRAGON_SLAYER.isEmpty() || UUID.fromString(gCfg.GAME__DRAGON_SLAYER) == null) {
            return;
        }
        Player player = dvz.getServer().getPlayer(UUID.fromString(gCfg.GAME__DRAGON_SLAYER));

        if (player == null) {
            return;
        }

        if (dvz.getPerms() != null) {
            dvz.getPerms().playerRemove(player, "prefix.dragonslayer");
        }

        player.setMaxHealth(20);

        dvz.getBoard().getTeam("dragonslayer").removePlayer(player);

        gCfg.GAME__DRAGON_SLAYER = "";
        gCfg.save();
    }

    public Player getDragonSlayer() {
        if (gCfg.GAME__DRAGON_SLAYER == null || gCfg.GAME__DRAGON_SLAYER.isEmpty() || UUID.fromString(gCfg.GAME__DRAGON_SLAYER) == null) {
            return null;
        }
        return dvz.getServer().getPlayer(UUID.fromString(gCfg.GAME__DRAGON_SLAYER));
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

    public void calculateMonsterPerc() {
        int dwarves = dvz.getPM().getPlayers(ClassType.DWARF, true, true).size();
        int monsters = dvz.getPM().getPlayers(ClassType.MONSTER, true, true).size();
        monsterPerc = (float)monsters / (monsters + dwarves);
    }

    public float getMonsterPerc() {
        return monsterPerc;
    }

    public float getMonsterPower(float scale) {
        return scale - (float)scale * (monsterPerc);
    }

    public float getMonsterPower(float base, float scale) {
        return base + (scale - (float)scale * (monsterPerc));
    }
}
