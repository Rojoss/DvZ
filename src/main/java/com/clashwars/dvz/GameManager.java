package com.clashwars.dvz;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.classes.BaseClass;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.config.GameCfg;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private DvZ dvz;
    private GameCfg gCfg;

    public GameManager(DvZ dvz) {
        this.dvz = dvz;
        gCfg = dvz.getGameCfg();
    }



    public void openGame() {
        //TODO: Reset stuff...
        setState(GameState.OPENED);
    }


    public void startGame() {
        Bukkit.broadcastMessage(CWUtil.integrateColor("&7========== &a&lDvZ has started! &7=========="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You can now choose a dwarf class!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You get one and a half day to prepare."));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Do your tasks and get fully equipped!."));

        //TODO: Give class item options to all players.

        getWorld().setTime(23000);
        setState(GameState.DAY_ONE);
    }


    public void createDragon() {
        Player player = getDragonPlayer();
        if (player == null) {
            for (Player p : dvz.getServer().getOnlinePlayers()) {
                if (player.isOp() || player.hasPermission("dvz.admin")) {
                    p.sendMessage(Util.formatMsg("&cNo dragon was set up so you have been set as dragon!"));
                    player = p;
                }
            }
        }
        if (player == null) {
            Bukkit.broadcastMessage(Util.formatMsg("&4&lThere is no staff member to player the dragon. &c&lPlease wait..."));
            Bukkit.broadcastMessage(Util.formatMsg("&7If there has been no dragon when the sun rises again, the monsters will be automatically released. Random dwarves will be killed!"));
            return;
        }
        DvzClass dragonType = getDragonType();

        Bukkit.broadcastMessage(CWUtil.integrateColor("&7======= &a&lThe " + CWUtil.capitalize(dragonType.toString().toLowerCase()) + "dragon arises! &7======="));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Stop working and get to the walls!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7Kill the dragon and become the &bDragonSlayer&7!"));
        Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &8Remember: &7If you die you become a monster."));

        CWPlayer cwp = dvz.getPM().getPlayer(player);
        cwp.setPlayerClass(dragonType);
        BaseClass c = dvz.getCM().getClass(dragonType);

        //TODO: Teleport player to dragon spawn.
        player.setFlying(true);
        player.setAllowFlight(true);
        player.getInventory().clear();
        c.equipItems(player);
        //TODO: Disguise player
        setState(GameState.DRAGON);
    }


    public void releaseMonsters(boolean doExecution) {
        if (doExecution) {
            //TODO: Kill players till 10% of the online players are monsters.
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
        //TODO: Set monster spawn at wall.
        //TODO: Replace blocks in wall to nether style.
    }


    public void stopGame(boolean force, String reason) {
        if (force) {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7===== &4&lThe game has been stopped! &7====="));
            if (reason != null && !reason.isEmpty()) {
                Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &8Reason: &7" + reason));

            }
            Bukkit.broadcastMessage(CWUtil.integrateColor("&c- &7You will be teleported back to the lobby."));
        } else {
            Bukkit.broadcastMessage(CWUtil.integrateColor("&7=== &a&lThe monsters have destroyed the shrine! &7==="));
            Bukkit.broadcastMessage(CWUtil.integrateColor("&a- &7You will be teleported back to the lobby."));
        }
        //TODO: Teleport all players back to lobby and reset all players.
    }




    public World getWorld() {
        return dvz.getServer().getWorld(gCfg.WORLD);
    }


    public GameState getState() {
        return GameState.valueOf(gCfg.GAME__STATE);
    }

    public void setState(GameState state) {
        gCfg.GAME__STATE = state.toString();
        gCfg.save();
    }

    public boolean isStarted() {
        return (getState() != GameState.CLOSED && getState() != GameState.ENDED);
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
        DvzClass dvzClass = DvzClass.valueOf(gCfg.GAME__DRAGON_TYPE);
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
        UUID uuid = UUID.fromString(gCfg.GAME__DRAGON_PLAYER);
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
