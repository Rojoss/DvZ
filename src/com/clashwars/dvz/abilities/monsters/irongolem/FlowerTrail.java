package com.clashwars.dvz.abilities.monsters.irongolem;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class FlowerTrail extends BaseAbility {

    public FlowerTrail() {
        super();
        ability = Ability.FLOWER_TRAIL;
        castItem = new DvzItem(Material.RED_ROSE, 1, (short)0, displayName, 100, -1);

        new BukkitRunnable() {
            @Override
            public void run() {
                List<CWPlayer> cwplayers = dvz.getPM().getPlayers(DvzClass.IRON_GOLEM, true);
                for (CWPlayer cwp : cwplayers) {
                    if (CWUtil.randomFloat() < 0.06f) {
                        final Item flower = CWUtil.dropItemStack(cwp.getLocation().add(0,1,0), getCastItem());
                        flower.setVelocity(RandomUtils.getRandomCircleVector().setY(1f).multiply(0.2f));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                flower.remove();
                            }
                        }.runTaskLater(dvz, 100);
                    }
                }
                List<CWPlayer> monsters = dvz.getPM().getPlayers(ClassType.MONSTER, true, false);
                for (CWPlayer monster : monsters) {
                    if (monster.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                        ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.RED_ROSE, (byte)0), 0.2f, 0.1f, 0.2f, 0.05f, 10, monster.getLocation().add(0,0.1f,0));
                    }
                }
            }
        }.runTaskTimer(dvz, 5, 5);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void itemPickup(PlayerPickupItemEvent event) {
        if (!isCastItem(event.getItem().getItemStack())) {
            return;
        }
        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());
        if (!cwp.isMonster()) {
            event.setCancelled(true);
            return;
        }
        if (event.getPlayer().hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            event.setCancelled(true);
            return;
        }
        event.setCancelled(false);
        CWUtil.sendActionBar(event.getPlayer(), "&6&l", "&a&lIronRose picked up!");
        event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 0));
        ParticleEffect.ITEM_CRACK.display(new ParticleEffect.ItemData(Material.RED_ROSE, (byte)0), 0.5f, 1f, 0.5f, 0.1f, 100, event.getPlayer().getLocation(), 500);
        event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.DIG_GRASS, 2, 0);
    }

}
