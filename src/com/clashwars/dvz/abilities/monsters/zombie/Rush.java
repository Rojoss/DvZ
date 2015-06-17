package com.clashwars.dvz.abilities.monsters.zombie;

import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.LineEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Rush extends BaseAbility {

    public Rush() {
        super();
        ability = Ability.RUSH;
        castItem = new DvzItem(Material.SUGAR, 1, (short)0, displayName, 50, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerloc) {

        final Player target = CWUtil.getTargetedPlayer(player, (int)dvz.getGM().getMonsterPower(20, 50));

        if (target == null) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cLook at a dwarf and click to use! &4&l<<"));
            return;
        }

        if (dvz.getPM().getPlayer(target).getPlayerClass().getType() != ClassType.DWARF) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cLook at a dwarf and click to use! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        showEffect(player, target);

        final DvzClass dvzClass = dvz.getPM().getPlayer(player).getPlayerClass();

        new BukkitRunnable() {
            int iterations = 0;
            double initDistance = player.getLocation().distance(target.getLocation());

            @Override
            public void run() {
                iterations++;
                if (iterations >= 4) {
                    iterations = 0;
                    if (target != CWUtil.getTargetedPlayer(player, (int)dvz.getGM().getMonsterPower(20, 50))) {
                        player.setWalkSpeed(dvzClass.getClassClass().getSpeed());
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&8&l>> &7No dwarf in sight anymore! &8&l<<"));
                        cancel();
                        return;
                    }
                    showEffect(player, target);
                }

                double distance = player.getLocation().distance(target.getLocation());

                player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_WALK, 0.5f, 2);
                ParticleEffect.FIREWORKS_SPARK.display(0.2f, 0.8f, 0.2f, 0.01f, 2, player.getLocation());
                if (distance > initDistance / 2 && player.getWalkSpeed() < dvzClass.getClassClass().getSpeed() + 1.5f) {
                    player.setWalkSpeed(player.getWalkSpeed() + 0.1f);
                } else if (distance <= initDistance / 2 && player.getWalkSpeed() > dvzClass.getClassClass().getSpeed()) {
                    player.setWalkSpeed(player.getWalkSpeed() - 0.1f);
                }
                if (distance < 2) {
                    player.setWalkSpeed(dvzClass.getClassClass().getSpeed());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, (int) dvz.getGM().getMonsterPower(60) + 20, 0));

                    player.getWorld().playSound(player.getLocation(), Sound.ZOMBIE_IDLE, 0.8f, 0.6f);
                    new BukkitRunnable() {
                        private int iterations = 0;
                        @Override
                        public void run () {
                            iterations++;
                            if (iterations >= (int)(dvz.getGM().getMonsterPower(60) + 20) / 3) {
                                cancel();
                                return;
                            }
                            ParticleEffect.BLOCK_DUST.display(new ParticleEffect.BlockData(Material.REDSTONE_BLOCK, (byte)0), 0.5f, 1.5f, 0.5f, 0, 10, player.getLocation().add(0,1,0));
                        }
                    }.runTaskTimer(dvz, 3, 3);
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(dvz, 0, 3);
    }

    private void showEffect(Player player, Player target) {
        LineEffect effect = new LineEffect(dvz.getEM());
        effect.setLocation(player.getLocation().add(0,1,0));
        effect.setTargetEntity(target);
        effect.visibleRange = 300;
        effect.particles = 20;
        effect.particleList.add(new Particle(ParticleEffect.SPELL_MOB, 1, 1, 1, 0.01f, 2, new ParticleEffect.OrdinaryColor(94, 85, 34)));
        effect.particleList.add(new Particle(ParticleEffect.SPELL_MOB, 1, 1, 1, 0.01f, 2, new ParticleEffect.OrdinaryColor(140, 126, 17)));
        effect.start();
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
