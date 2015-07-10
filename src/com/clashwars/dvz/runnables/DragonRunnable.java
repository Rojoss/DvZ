package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.damage.types.CustomDmg;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonRunnable extends BukkitRunnable {

    private DvZ dvz;
    private Player dragon;
    private int prevPower = 3;

    public DragonRunnable(DvZ dvz) {
        this.dvz = dvz;
        dragon = dvz.getGM().getDragonPlayer();
        prevPower = 3;
    }

    @Override
    public void run() {
        if (dvz.getGM() == null || dvz.getGM().getDragonPlayer() == null || dvz.getGM().getDragonType() == null || dvz.getGM().isMonsters() || dvz.getGM().getDragonPlayer().isDead()) {
            this.cancel();
        }
        if (dragon == null) {
            dragon = dvz.getGM().getDragonPlayer();
        }

        Double monsterPerc = CWUtil.getPercentage(dvz.getPM().getPlayers(ClassType.MONSTER, true, true).size(), dvz.getPM().getPlayers(true).size()) / 100;
        int power = 1;
        if (monsterPerc < dvz.getCfg().MONSTER_PERCENTAGE_MIN) {
            power = 3;
        } else if (monsterPerc < dvz.getCfg().MONSTER_PERCENTAGE_MAX) {
            power = 2;
        } else {
            power = 1;
        }
        if (power != prevPower) {
            Util.broadcast("&aDragon power decreased! &8[&2" + prevPower + "&7>&a" + power + "&8]");
        }
        prevPower = power;
        dvz.getGM().setDragonPower(power);

        Double hpRegen = (double)(power * 0.5f) - 0.5f;
        new CustomDmg(dragon, -hpRegen, "", "");

        //Make players take damage from water if it's the fire dragon
        if (dvz.getPM().getPlayer(dragon).getPlayerClass() == DvzClass.FIREDRAGON) {
            for (Player player : dvz.getServer().getOnlinePlayers()) {
                Block block = player.getLocation().getBlock();
                Block blockAbove = block.getRelative(BlockFace.UP);
                if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.CAULDRON
                        || blockAbove.getType() == Material.WATER || blockAbove.getType() == Material.STATIONARY_WATER) {
                    CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe water is boiling from the dragon! &4GET OUT! &4&l<<"));
                    new CustomDmg(player, 4, "{0} died from standing in boiling water", "boiling water", dragon);
                }
            }
        }
    }
}
