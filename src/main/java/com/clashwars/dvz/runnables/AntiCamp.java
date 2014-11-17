package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class AntiCamp extends BukkitRunnable {

    private DvZ dvz;
    private Entity center = null;
    private Location centerLoc = null;
    private int warnRange = 100;
    private int range = 75;

    public AntiCamp(DvZ dvz) {
        this.dvz = dvz;
        recalculate();
    }

    private void recalculate() {
        if (dvz.getMM().getActiveMap() == null || dvz.getMM().getActiveMap().getWorld() == null) {
            return;
        }
        World world = dvz.getMM().getActiveMap().getWorld();

        Location spawnLoc = dvz.getMM().getActiveMap().getLocation("monster");
        spawnLoc.setY(255);
        center = world.spawnEntity(spawnLoc, EntityType.WITHER_SKULL);
        center.setVelocity(new Vector(0,0,0));
        ((WitherSkull)center).setDirection(new Vector(0,0,0));
        ((WitherSkull)center).setCharged(false);

        centerLoc = center.getLocation();

        warnRange = dvz.getCfg().CAMP_WARN_RANGE;
        range = dvz.getCfg().CAMP_RANGE;
    }


    @Override
    public void run() {
        if (center == null || centerLoc == null || center.isDead()) {
            if (dvz.getGM().isMonsters()) {
                recalculate();
            }
            return;
        }
        List<Entity> entities = center.getNearbyEntities(warnRange, 256, warnRange);
        for (Entity e : entities) {
            if (e instanceof Player) {
                Player player = (Player)e;
                CWPlayer cwp = dvz.getPM().getPlayer(player);
                if (cwp.isDwarf()) {
                    centerLoc = center.getLocation().clone();
                    centerLoc.setY(player.getLocation().getY());
                    if (player.getLocation().distance(centerLoc) <= range) {
                        player.damage(2);
                        ParticleEffect.displayBlockCrack(player.getLocation(), 52, (byte) 0, 0.4f, 1.0f, 0.4f, 20);
                    } else {
                        player.sendMessage(Util.formatMsg("&4&lDon't camp the monsters!"));
                    }
                }
            }
        }

    }

}
