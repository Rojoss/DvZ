package com.clashwars.dvz.abilities.monsters.blaze;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Fireball extends BaseAbility {

    public Fireball() {
        super();
        ability = Ability.FIREBALL;
        castItem = new DvzItem(Material.FIREBALL, 1, (short)0, 198, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }
        SmallFireball smallFireball = player.launchProjectile(SmallFireball.class);
        smallFireball.setMetadata("type", new FixedMetadataValue(dvz, "Fireball-Ability"));
        smallFireball.setVelocity(player.getVelocity());

        player.getWorld().playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1, 0.6f);
        ParticleEffect.FLAME.display(0.5f, 0.5f, 0.5f, 0, 20, player.getLocation().add(0,1,0));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof SmallFireball)) {
            return;
        }

        if(!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!event.getDamager().hasMetadata("type")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(ProjectileHitEvent event) {
        if(!(event.getEntity() instanceof SmallFireball)) {
            return;
        }

        if(!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        final Location l = event.getEntity().getLocation();

        if (!getDvzClasses().contains(dvz.getPM().getPlayer(player).getPlayerClass())) {
            return;
        }

        final int radius = (int)dvz.getGM().getMonsterPower(1, 2);

        ParticleEffect.LAVA.display(radius,radius,radius, 0, 15, l);
        ParticleEffect.FLAME.display(radius, radius, radius, 0, 20, l);
        l.getWorld().playSound(l, Sound.EXPLODE, 0.5f, 0);
        l.getWorld().playSound(l, Sound.GHAST_FIREBALL, 0.4f, 0);

        for(int x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++) {
            for(int y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++) {
                for(int z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++) {
                    Block b = l.getWorld().getBlockAt(x, y, z);
                    if(b.getType().isSolid()) {
                        if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
                            if (CWUtil.randomFloat() <= dvz.getGM().getMonsterPower(0.1f, 0.6f)) {
                                b.getRelative(BlockFace.UP).setType(Material.FIRE);
                            }
                        }
                    }
                }
            }
        }

        List<Player> players = CWUtil.getNearbyPlayers(l, radius + 2);
        for (Player p : players) {
            if (dvz.getPM().getPlayer((Player)p).isDwarf()) {
                new AbilityDmg(p, dvz.getGM().getMonsterPower(1, 3), ability, player);
                player.setFireTicks((int) dvz.getGM().getMonsterPower(20, 80));
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for(int x = l.getBlockX() - radius; x <= l.getBlockX() + radius; x++) {
                    for (int y = l.getBlockY() - radius; y <= l.getBlockY() + radius; y++) {
                        for (int z = l.getBlockZ() - radius; z <= l.getBlockZ() + radius; z++) {
                            Block b = l.getWorld().getBlockAt(x, y, z).getRelative(BlockFace.UP);
                            if (b.getType() == Material.FIRE) {
                                b.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        }.runTaskLater(dvz, 200);

    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

}
