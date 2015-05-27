package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
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
        List<Entity> entities = CWUtil.getNearbyEntities(monsterLoc, warnRange, Arrays.asList(new EntityType[] {EntityType.PLAYER}));
        for (Entity e : entities) {
            if (e instanceof Player) {
                Player player = (Player)e;
                CWPlayer cwp = dvz.getPM().getPlayer(player);
                if (cwp.isDwarf()) {
                    if (player.getLocation().distance(monsterLoc) <= range) {
                        player.damage(2);
                        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte) 0), 0.4f, 1.0f, 0.4f, 0.01f, 20, player.getLocation());
                    } else {
                        player.sendMessage(Util.formatMsg("&4&lDon't camp the monsters!"));
                    }
                }
            }
        }

    }

}
