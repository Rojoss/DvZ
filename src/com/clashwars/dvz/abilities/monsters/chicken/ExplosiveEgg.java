package com.clashwars.dvz.abilities.monsters.chicken;

import com.clashwars.dvz.abilities.dwarves.bonus.Forcefield;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Egg;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.List;

public class ExplosiveEgg extends BaseAbility {

    public ExplosiveEgg() {
        super();
        ability = Ability.EXPLOSIVE_EGG;
        castItem = new DvzItem(Material.EGG, 1, (short) 0, displayName, 180, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.6f, 1.6f);
        player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 1, 0);
        player.throwEgg();
    }

    @EventHandler
    private void eggLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Egg)) {
            return;
        }
        Egg egg = (Egg)event.getEntity();
        if (egg.getShooter() == null || !(egg.getShooter() instanceof Player)) {
            return;
        }
        if (dvz.getPM().getPlayer((Player)egg.getShooter()).getPlayerClass() != DvzClass.CHICKEN) {
            return;
        }

        if (Forcefield.inForcefield(egg.getLocation())) {
            return;
        }

        egg.getWorld().playSound(event.getEntity().getLocation(), Sound.EXPLODE, 1, 1.5f);
        ParticleEffect.EXPLOSION_LARGE.display(dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), 0, 10, egg.getLocation(), 500);
        ParticleEffect.SMOKE_LARGE.display(dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), dvz.getGM().getMonsterPower(0.5f, 2f), 0, 150, egg.getLocation(), 500);
        List<Player> players = CWUtil.getNearbyPlayers(egg.getLocation(), (int)dvz.getGM().getMonsterPower(1, 2.5f));
        for (Player p : players) {
            if (dvz.getPM().getPlayer((Player)p).isDwarf() && !Forcefield.inForcefield(p.getLocation())) {
                new AbilityDmg(p, dvz.getGM().getMonsterPower(1,5), ability, (Player)egg.getShooter());
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) dvz.getGM().getMonsterPower(20, 80), 1));
                ParticleEffect.SMOKE_LARGE.display(0.5f, 0.5f, 0.5f, 0, 50, p.getLocation());
            }
        }


        Location blockCenter = egg.getLocation().getBlock().getLocation().add(0.5f, 0.5f, 0.5f);
        int radius = 3;
        List<Material> undestroyableBlocks = dvz.getUndestroyableBlocks();
        for (double x = blockCenter.getBlockX() - radius; x < blockCenter.getBlockX() + radius; x++) {
            for (double y = blockCenter.getBlockY() - radius; y < blockCenter.getBlockY() + radius; y++) {
                for (double z = blockCenter.getBlockZ() - radius; z < blockCenter.getBlockZ() + radius; z++) {
                    Block block = blockCenter.getWorld().getBlockAt((int)x, (int)y, (int)z);
                    if (undestroyableBlocks.contains(block.getType())) {
                        if (block.getType() == Material.ENDER_PORTAL_FRAME) {
                            Util.damageShrine(block.getLocation(), (Player)egg.getShooter(), (int)dvz.getGM().getMonsterPower(1, 2));
                        }
                        continue;
                    }
                    if (Util.isProtected(block.getLocation().toVector())) {
                        continue;
                    }
                    double distance = block.getLocation().distance(blockCenter);
                    if (distance > radius) {
                        continue;
                    }
                    if (distance > radius-1  && CWUtil.randomFloat() > 0.5f) {
                        continue;
                    }

                    FallingBlock fallingBlock = blockCenter.getWorld().spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                    Vector dir = block.getLocation().toVector().subtract(blockCenter.toVector()).normalize();
                    fallingBlock.setVelocity(dir.multiply(0.5f));

                    block.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    private void eggHatch(CreatureSpawnEvent event) {
        if (!(event.getEntity() instanceof Chicken)) {
            return;
        }
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.EGG) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
