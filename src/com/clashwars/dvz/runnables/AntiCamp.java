package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.damage.types.CustomDmg;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class AntiCamp extends BukkitRunnable {

    private DvZ dvz;
    private Location monsterLoc = null;
    private int warnRange = 100;
    private int range = 75;

    public AntiCamp(DvZ dvz) {
        this.dvz = dvz;

        warnRange = dvz.getCfg().CAMP_WARN_RANGE;
        range = dvz.getCfg().CAMP_RANGE;
    }

    private void recalculate() {
        if (dvz.getMM().getActiveMap() == null || dvz.getMM().getActiveMap().getWorld() == null) {
            return;
        }
        World world = dvz.getMM().getActiveMap().getWorld();
        monsterLoc = dvz.getMM().getActiveMap().getLocation("monster");
    }


    @Override
    public void run() {
        if (monsterLoc == null) {
            if (dvz.getGM().isMonsters()) {
                recalculate();
            }
            return;
        }
        List<Player> players = CWUtil.getNearbyPlayers(monsterLoc, warnRange);
        for (Player p : players) {
            if (dvz.getPM().getPlayer(p).isDwarf()) {
                if (p.getLocation().distance(monsterLoc) <= range) {
                    new CustomDmg(p, 2, "{0} died from camping the monsters!", "monster camping");
                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.4f, 1.0f, 0.4f, 0.01f, 20, p.getLocation());
                } else {
                    p.sendMessage(Util.formatMsg("&4&lDon't camp the monsters!"));
                }
            }
        }

    }

}
