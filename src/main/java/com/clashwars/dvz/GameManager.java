package com.clashwars.dvz;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.GameCfg;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private DvZ dvz;
    private GameCfg gCfg;

    public GameManager(DvZ dvz) {
        this.dvz = dvz;
        gCfg = dvz.getGameCfg();
    }



    public void resetGame(boolean nextGame, String mapName) {
        setState(GameState.SETUP);
        if (!nextGame) {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has ended! &7=========="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Come back again later for more DvZ!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Make sure to follow us on Twitch to know when DvZ starts!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Use &6&l/leave &7to go to the pvp server!"));
        } else {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ is resetting! &7=========="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7A new game will be starting soon."));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7If you're not watching the stream yet make sure to do!"));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));
        }

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

        //Reset data
        //TODO: Reset all other data

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
            return;
        }

        setState(GameState.OPENED);
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has opened! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7The game will be starting soon so please wait."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Beat the parkour to get 3 classes instead of 2!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7If you're not watching the stream yet make sure to do!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &9&lhttp://twitch.tv/clashwars"));

        //Tp all players to active world.
        for (Player player : dvz.getServer().getOnlinePlayers()) {
            player.teleport(dvz.getMM().getUsedWorld().getSpawnLocation());
        }
    }


    public void startGame() {
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has started! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You can now choose a dwarf class!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You get one and a half day to prepare."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Do your tasks and get fully equipped!."));

        for (Player player : dvz.getServer().getOnlinePlayers()) {
            giveClassItems(player, ClassType.DWARF, false);
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

        Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe " + CWUtil.capitalize(dragonType.toString().toLowerCase()) + "dragon arises! &7======="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Stop working and get to the walls!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Kill the dragon and become the &bDragonSlayer&7!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &8Remember: &7If you die you become a monster."));

        CWPlayer cwp = dvz.getPM().getPlayer(player);
        cwp.setPlayerClass(dragonType);
        BaseClass c = dvz.getCM().getClass(dragonType);
        cwp.savePlayer();


        player.setAllowFlight(true);
        player.setFlying(true);
        player.teleport(dvz.getMM().getActiveMap().getLocation("dragon"));
        player.getInventory().clear();
        c.equipItems(player);
        Util.disguisePlayer(player, "enderdragon");
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
    }


    public void giveClassItems(Player player, ClassType type, boolean forcePrevious) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (forcePrevious) {
            for (DvzClass c : cwp.getClassOptions()) {
                dvz.getCM().getClass(c).getClassItem().giveToPlayer(player);
            }
            return;
        }
        //TODO: Get classes based on weights from class manager.
        Map<DvzClass, BaseClass> classOptions = dvz.getCM().getRandomClasses(player, type);
        cwp.clearClassOptions();
        cwp.setClassOptions(classOptions.keySet());
        for (DvzClass c : classOptions.keySet()) {
            classOptions.get(c).getClassItem().giveToPlayer(player);
        }
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
        gCfg.GAME__DRAGON_TYPE = dvzClass.toString();
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
        gCfg.GAME__DRAGON_PLAYER = uuid.toString();
        gCfg.save();
    }
}
