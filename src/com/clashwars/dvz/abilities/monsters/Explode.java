package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Explode extends MobAbility {

    public Explode() {
        super();
        ability = Ability.EXPLODE;
        castItem = new DvzItem(Material.SULPHUR, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        //--
    }

    @EventHandler
    private void sneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        final CWPlayer cwp = dvz.getPM().getPlayer(player);

        if (player.isSneaking()) {
            return;
        }

        if (!isCastItem(player.getItemInHand())) {
            return;
        }

        if (!canCast(player)) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        Util.disguisePlayer(player.getName(), (cwp.getPlayerClass().getClassClass().getDisguise() + " setPowered true"));
        player.getWorld().playSound(player.getLocation(), Sound.FUSE, 1, 2);
        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 10, player.getLocation().add(0, 1, 0));

        double pps = 0.5f;
        if (dvz.getGM().getMonsterPower(1) >= 0.8f) {
            pps = 1.5f;
        } else if (dvz.getGM().getMonsterPower(1) >= 0.3f) {
            pps = 1f;
        }
        final double powerPerSec = dvz.getGM().getMonsterPower(2) + 0.5f;

        new BukkitRunnable() {
            int minPower = 1;
            int maxPower = (int)dvz.getGM().getMonsterPower(3, 12);
            int ticks = 0;
            Double power = (double)minPower;
            Location playerLoc = player.getLocation();

            @Override
            public void run() {
                ticks++;
                //Every second... +/-
                if (ticks % 20 == 0) {
                    //Check if player has moved
                    if (playerLoc.getBlockX() != player.getLocation().getBlockX() || playerLoc.getBlockY() != player.getLocation().getBlockY() || playerLoc.getBlockZ() != player.getLocation().getBlockZ()) {
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cExplosion cancelled because you moved! &4&l<<"));
                        Util.disguisePlayer(player.getName(), cwp.getPlayerClass().getClassClass().getDisguise());
                        this.cancel();
                        return;
                    }
                    //Increase the power by powerpersec value and clamp it between min/max value.
                    double prevPower = power;
                    power = Math.min(Math.max((ticks / 20) * powerPerSec, minPower), maxPower);
                    CWUtil.sendActionBar(player, CWUtil.integrateColor("&9&l>> &3Charge power&8: &6&l" + power + "&7&l/&8&l" + maxPower + " &9&l<<"));
                    if (power != prevPower) {
                        player.getWorld().playSound(playerLoc, Sound.FUSE, 1, 2);
                        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 10, playerLoc.clone().add(0,1,0));
                    }
                }

                //If player died create small explosion.
                if (player.isDead()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + minPower + " &cpower because you died!"));
                    createExplosion(playerLoc, (float)minPower);
                    this.cancel();
                    return;
                }

                //If player stops sneaking then explode!
                if (!player.isSneaking()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + power + " &cpower!"));
                    player.setHealth(0);
                    createExplosion(playerLoc, power.floatValue());
                    this.cancel();
                    return;
                }

                //After 30 seconds force explosion. [because this is quite a heavy task so it's just a waste running this forever]
                if (ticks > 600) {
                    player.sendMessage(Util.formatMsg("&cOvercharge!"));
                    player.setHealth(0);
                    createExplosion(playerLoc, power.floatValue());
                    this.cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }

    private void createExplosion(Location loc, float power) {
        loc.getWorld().createExplosion(loc, power, false);

        if (dvz.getGM().getMonsterPower(3) >= 1) {
            List<Entity> entities = CWUtil.getNearbyEntities(loc, power * 0.75f, null);
            for (Entity e : entities) {
                if (!(e instanceof Player)) {
                    continue;
                }
                CWPlayer cwp = dvz.getPM().getPlayer((Player)e);
                if (cwp.isDwarf()) {
                    ((Player)e).damage(dvz.getGM().getMonsterPower(3));
                }
            }
        }

        //Damage shrine blocks.
        for (ShrineBlock shrineBlock : dvz.getGM().getShrineBlocks()) {
            if (shrineBlock != null && !shrineBlock.isDestroyed()) {
                if (shrineBlock.getLocation().distance(loc) < power) {
                    shrineBlock.damage(Math.round(power * 2));
                }
            }
        }

    }
}
