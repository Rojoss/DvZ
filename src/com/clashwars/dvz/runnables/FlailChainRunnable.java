package com.clashwars.dvz.runnables;

import com.clashwars.cwcore.debug.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.listeners.WeaponHandler;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class FlailChainRunnable extends BukkitRunnable {

    private OfflinePlayer caster;
    private OfflinePlayer target;
    private Long startTime;

    public FlailChainRunnable(OfflinePlayer caster, OfflinePlayer target) {
        this.caster = caster;
        this.target = target;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        if (caster == null || target == null || !caster.isOnline() || !target.isOnline()) {
            WeaponHandler.flailedPlayers.remove(caster.getUniqueId());
            cancel();
            return;
        }

        if (System.currentTimeMillis() >= startTime + 1250) {
            WeaponHandler.flailedPlayers.remove(caster.getUniqueId());
            cancel();
            return;
        }

        Player casterPlayer = (Player)caster;
        Player targetPlayer = (Player)target;
        if (casterPlayer.isDead() || targetPlayer.isDead() || !casterPlayer.isBlocking()) {
            WeaponHandler.flailedPlayers.remove(caster.getUniqueId());
            cancel();
            return;
        }

        Vector casterDir = casterPlayer.getLocation().getDirection();
        casterDir = casterDir.setY(0);
        Vector dir = targetPlayer.getLocation().add(0, 1, 0).toVector().subtract(casterPlayer.getLocation().add(0, 1, 0).add(casterDir).toVector());

        float length = (float)dir.length();
        if (length <= 1 || length > 30) {
            WeaponHandler.flailedPlayers.remove(caster.getUniqueId());
            cancel();
            return;
        }
        dir.normalize();

        float ratio = length / CWUtil.random(5, 15);
        Vector v = dir.multiply(ratio);
        Location loc = casterPlayer.getLocation().add(0,1,0).add(casterDir).clone().subtract(v);
        for (int i = 0; i < 10; i++) {
            loc.add(v);
            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(194, 100, 0), loc, 32);
        }

        dir = dir.multiply(-1);
        double y = dir.getY();
        if (y < 0) {
            y *= 0.2f;
        } else {
            y *= 0.1f;
        }
        targetPlayer.setVelocity(targetPlayer.getVelocity().add(new Vector(dir.getX() * 0.2f, y, dir.getZ() * 0.2f)));
    }
}
