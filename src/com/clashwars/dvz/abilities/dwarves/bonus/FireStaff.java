package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class FireStaff extends BaseAbility {

    public FireStaff() {
        super();
        //ability = Ability.FIRESTAFF;
        castItem = new DvzItem(Material.BLAZE_ROD, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }
        if (onCooldown(player)) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {

                    SmallFireball smallFireball = player.launchProjectile(SmallFireball.class);
                    smallFireball.setVelocity(player.getVelocity());

                    player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 0.8f, 0.6f);
                    ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0, 20, player.getLocation().add(0, 1, 0));
                }

            }
        }.runTaskLater(dvz, 10);
    }

    @EventHandler
    public void onProjectileHit(final ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof SmallFireball)) {
            return;
        }

        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Location l = event.getEntity().getLocation();
        final int radius = 2;
        final Player p = ((Player) event.getEntity().getShooter()).getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                for(int x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++) {
                    for (int y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++) {
                        for (int z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++) {
                            Block b = l.getWorld().getBlockAt(x, y, z).getRelative(BlockFace.UP);
                            if (b.getType() == Material.FIRE) {
                                b.setType(Material.AIR);
                                p.playSound(l, Sound.FIZZ, 1, 0.6f);

                            }
                        }
                    }
                }
            }
        }.runTaskLater(dvz, 80);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
