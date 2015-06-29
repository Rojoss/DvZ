package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.damage.types.CustomDmg;
import com.clashwars.dvz.util.Util;
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
            dvz.getServer().broadcastMessage(Util.formatMsg("&aDragon power decreased! &8[&2" + prevPower + "&7>&a" + power + "&8]"));
        }
        prevPower = power;
        dvz.getGM().setDragonPower(power);

        Double hpRegen = (double)(power * 0.5f) - 0.5f;
        new CustomDmg(dragon, -hpRegen, "", "");
    }
}
