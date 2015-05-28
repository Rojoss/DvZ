package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DragonRunnable extends BukkitRunnable {

    private DvZ dvz;
    private Player dragon;

    public DragonRunnable(DvZ dvz) {
        this.dvz = dvz;
        dragon = dvz.getGM().getDragonPlayer();
    }

    @Override
    public void run() {
        if (dvz.getGM() == null || dvz.getGM().getDragonPlayer() == null || dvz.getGM().getDragonType() == null || dvz.getGM().isMonsters()) {
            this.cancel();
        }
        if (dragon == null) {
            dragon = dvz.getGM().getDragonPlayer();
        }

        Double monsterPerc = CWUtil.getPercentage(dvz.getPM().getPlayers(ClassType.MONSTER, false).size(), dvz.getPM().getPlayers().size());
        int power = 1;
        if (monsterPerc < dvz.getCfg().MONSTER_PERCENTAGE_MIN) {
            power = 3;
        } else if (monsterPerc < dvz.getCfg().MONSTER_PERCENTAGE_MAX) {
            power = 2;
        } else {
            power = 1;
        }
        dvz.getGM().setDragonPower(power);

        Double hpRegen = (power * 0.5d) - 0.5d;
        dragon.setHealth(Math.min(dragon.getHealth() + hpRegen, dragon.getMaxHealth()));
    }
}
