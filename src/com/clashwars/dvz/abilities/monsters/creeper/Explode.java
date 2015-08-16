package com.clashwars.dvz.abilities.monsters.creeper;

import com.clashwars.dvz.abilities.dwarves.bonus.Forcefield;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.damage.types.CustomDmg;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Explode extends BaseAbility {

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
        final double powerPerSec = pps;

        new BukkitRunnable() {
            int minPower = 4;
            int maxPower = (int)dvz.getGM().getMonsterPower(4, 15);
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
                    CWUtil.sendActionBar(player, CWUtil.integrateColor("&9&l>> &3Charge power&8: &6&l" + CWUtil.round(power.floatValue(), 1) + "&7&l/&8&l" + maxPower + " &9&l<<"));
                    if (power != prevPower) {
                        player.getWorld().playSound(playerLoc, Sound.FUSE, 1, 2);
                        ParticleEffect.FIREWORKS_SPARK.display(0.5f, 1f, 0.5f, 0, 10, playerLoc.clone().add(0,1,0));
                    }
                }

                //If player died create small explosion.
                if (player.isDead()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + minPower + " &cpower because you died!"));
                    createExplosion(player, playerLoc, (float)minPower);
                    this.cancel();
                    return;
                }

                //If player stops sneaking then explode!
                if (!player.isSneaking()) {
                    player.sendMessage(Util.formatMsg("&cExploded with &4" + power + " &cpower!"));
                    createExplosion(player, playerLoc, power.floatValue());
                    this.cancel();
                    return;
                }

                //After 30 seconds force explosion. [because this is quite a heavy task so it's just a waste running this forever]
                if (ticks > 600) {
                    player.sendMessage(Util.formatMsg("&cOvercharge!"));
                    createExplosion(player, playerLoc, power.floatValue());
                    this.cancel();
                }
            }
        }.runTaskTimer(dvz, 0, 1);
    }

    private void createExplosion(Player caster, Location loc, float power) {
        ParticleEffect.EXPLOSION_HUGE.display(0.5f, 0.5f, 0.5f, 0, Math.round(power), loc, 500);
        loc.getWorld().playSound(loc, Sound.EXPLODE, 2, 1 - (float)CWUtil.lerp(0, 1, power));

        if (dvz.getGM().getMonsterPower(3) >= 1) {
            List<Player> players = CWUtil.getNearbyPlayers(loc, power * 0.75f);
            for (Player p : players) {
                if (p.getUniqueId().equals(caster.getUniqueId())) {
                    continue;
                }
                if (dvz.getPM().getPlayer(p).isDwarf() && !Forcefield.inForcefield(p.getLocation())) {
                    new AbilityDmg(p, 0.5f * power, ability, caster);
                    Vector dir = p.getLocation().toVector().subtract(loc.toVector()).normalize();
                    p.setVelocity(p.getVelocity().add(dir.multiply(2.5f)));
                }
            }
        }

        Location blockCenter = loc.getBlock().getLocation().add(0.5f, 0.5f, 0.5f);
        int powerR = (int)Math.ceil(power / 2);
        List<Material> undestroyableBlocks = dvz.getUndestroyableBlocks();
        for (double x = blockCenter.getBlockX() - powerR; x < blockCenter.getBlockX() + powerR; x++) {
            for (double y = blockCenter.getBlockY() - powerR; y < blockCenter.getBlockY() + powerR; y++) {
                for (double z = blockCenter.getBlockZ() - powerR; z < blockCenter.getBlockZ() + powerR; z++) {
                    Block block = loc.getWorld().getBlockAt((int)x, (int)y, (int)z);
                    if (undestroyableBlocks.contains(block.getType())) {
                        if (block.getType() == Material.ENDER_PORTAL_FRAME) {
                            Util.damageShrine(loc, caster, Math.round(power));
                        }
                        continue;
                    }
                    if (Util.isProtected(block.getLocation().toVector())) {
                        continue;
                    }
                    double distance = block.getLocation().distance(blockCenter);
                    if (distance > power+1 / 2) {
                        continue;
                    }
                    if (distance > power / 2  && CWUtil.randomFloat() > 0.5f) {
                        continue;
                    }
                    if (Forcefield.inForcefield(block.getLocation().add(0.5f, 0.5f, 0.5f))) {
                        continue;
                    }

                    FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                    Vector dir = block.getLocation().toVector().subtract(blockCenter.toVector()).normalize();
                    fallingBlock.setVelocity(dir.multiply(0.5f));

                    block.setType(Material.AIR);
                }
            }
        }

        new CustomDmg(caster, 20, "{0} exploded", "explode");
    }
}
