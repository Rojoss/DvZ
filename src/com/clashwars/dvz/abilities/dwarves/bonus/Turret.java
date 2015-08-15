package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Turret extends BaseAbility {

    public static List<ArmorStand> turrets = new ArrayList<ArmorStand>();

    public Turret() {
        super();
        ability = Ability.TURRET;
        castItem = new DvzItem(Material.DISPENSER, 5, (short)0, displayName, -1, -1, false);

        //Shoot a projectile every 30 ticks for each turret.
        new BukkitRunnable() {
            @Override
            public void run() {
                for (final ArmorStand turret : turrets) {
                    final List<Player> targets = new ArrayList<Player>();
                    List<Entity> nearby = turret.getNearbyEntities(10, 5, 10);
                    for (Entity e : nearby) {
                        if (!(e instanceof Player)) {
                            continue;
                        }
                        if (!dvz.getPM().getPlayer((Player)e).isMonster()) {
                            continue;
                        }
                        targets.add((Player)e);
                    }
                    if (targets.size() <= 0) {
                        continue;
                    }

                    //Switch between up to 5 targets
                    new BukkitRunnable() {
                        int count = 0;
                        @Override
                        public void run() {
                            if (targets.size() <= count) {
                                cancel();
                                return;
                            }
                            final Player target = targets.get(count);

                            //Get the start and target rotation.
                            final double startRotation = turret.getHeadPose().getY();
                            final Vector dir = turret.getEyeLocation().add(0, 0.5f, 0).toVector().subtract(target.getEyeLocation().toVector());

                            double dx = dir.getX();
                            double dz = dir.getZ();
                            double yaw = 0;
                            if (dx != 0) {
                                if (dx < 0) {
                                    yaw = 1.5 * Math.PI;
                                } else {
                                    yaw = 0.5 * Math.PI;
                                }
                                yaw -= Math.atan(dz / dx);
                            } else if (dz < 0) {
                                yaw = Math.PI;
                            }
                            final double targetRotation = (float)Math.toRadians((float) (-yaw * 180 / Math.PI - 90) - 90);

                            //Animate the rotation
                            new BukkitRunnable() {
                                int animationCount = 0;
                                double startValue = turret.getHeadPose().getY();
                                @Override
                                public void run() {
                                    float perc = (float)animationCount / 5;
                                    turret.setHeadPose(new EulerAngle(0, CWUtil.lerp(startRotation, targetRotation, perc), 0));

                                    animationCount++;
                                    if (animationCount > 5) {
                                        //Shoot projectile
                                        Vector dir2 = dir.normalize().multiply(-1);
                                        CWEntity arrow = CWEntity.create(EntityType.ARROW, turret.getEyeLocation().add(dir2.getX(), 0, dir2.getZ()));
                                        arrow.setVelocity(dir2.multiply(2));
                                        arrow.setBounce(false);
                                        arrow.setCritical(true);
                                        arrow.entity().setMetadata("turret", new FixedMetadataValue(dvz, turret.getName()));

                                        cancel();
                                    }
                                }
                            }.runTaskTimer(dvz, 0, 1);

                            count++;
                            if (count >= 4) {
                                cancel();
                            }
                        }
                    }.runTaskTimer(dvz, 0, 6);
                }
            }
        }.runTaskTimer(dvz, 5, 30);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }

        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("keep").contains(player.getLocation())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTurrets can't be placed inside the keep right now &4&l<<"));
            return;
        }
        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("innerwall").contains(player.getLocation())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTurrets can't be placed inside the keep right now &4&l<<"));
            return;
        }

        if (dvz.getMM().getActiveMap().getLocation("monster").toVector().distance(player.getLocation().toVector()) < 110f) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTurrets can't be placed near the monster spawn &4&l<<"));
            return;
        }

        if (player.getLocation().getBlock().getType() != Material.AIR || player.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't create a turret here! &4&l<<"));
            return;
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTurrets must be placed on the ground! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        CWEntity armorStand = CWEntity.create(EntityType.ARMOR_STAND, player.getLocation().getBlock().getLocation().add(0.5f, 0f, 0.5f));
        player.getLocation().getBlock().setType(Material.SPRUCE_FENCE);
        player.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.SPRUCE_FENCE);
        armorStand.setHelmet(new ItemStack(Material.DISPENSER));
        armorStand.setArmorstandVisibility(false);
        armorStand.setArmorstandPlate(true);
        armorStand.setArmorstandGravity(false);
        armorStand.setSmall(false);
        armorStand.setName(player.getName());
        armorStand.setNameVisible(false);
        armorStand.entity().setMetadata("turret", new FixedMetadataValue(dvz, player.getName()));
        turrets.add((ArmorStand) armorStand.entity());

        ParticleEffect.CLOUD.display(0.5f, 0.5f, 0.5f, 0, 50, player.getLocation().add(0, 0.5f, 0), 500);
        ParticleEffect.SMOKE_LARGE.display(0.5f, 0.5f, 0.5f, 0, 50, player.getLocation().add(0, 0.5f, 0), 500);
        player.getWorld().playSound(player.getLocation(), Sound.PISTON_EXTEND, 1, 0);

        CWUtil.removeItemsFromHand(player, 1);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void entityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (!event.getDamager().hasMetadata("turret")) {
            return;
        }
        Player damaged = (Player)event.getEntity();
        Player shooter = dvz.getServer().getPlayer(event.getDamager().getMetadata("turret").get(0).asString());
        if (shooter == null) {
            return;
        }
        event.setCancelled(true);
        new AbilityDmg(damaged, 2, Ability.TURRET, shooter);
    }

    @EventHandler
    private void destroy(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ArmorStand)) {
            return;
        }
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
        ArmorStand stand = (ArmorStand) event.getRightClicked();
        if (!dvz.getPM().getPlayer(event.getPlayer()).isMonster()) {
            return;
        }
        if (!stand.hasMetadata("turret")) {
            return;
        }
        Player owner = dvz.getServer().getPlayer(stand.getMetadata("turret").get(0).asString());
        if (owner != null) {
            owner.sendMessage(Util.formatMsg("&c&lYour turret has been destroyed!"));
        }

        ParticleEffect.EXPLOSION_NORMAL.display(1,1,1,0, 5, stand.getLocation());
        stand.getWorld().playSound(stand.getLocation(), Sound.EXPLODE, 1, 1);

        turrets.remove(stand);
        stand.remove();
        if (stand.getLocation().getBlock().getType() == Material.SPRUCE_FENCE) {
            stand.getLocation().getBlock().setType(Material.AIR);
        }
        if (stand.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.SPRUCE_FENCE) {
            stand.getLocation().getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        }
    }
}
