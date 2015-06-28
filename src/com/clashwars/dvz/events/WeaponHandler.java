package com.clashwars.dvz.events;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.AnimatedCircleEffect;
import com.clashwars.cwcore.effect.effects.CircleEffect;
import com.clashwars.cwcore.effect.effects.LineEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.damage.types.CustomDmg;
import com.clashwars.dvz.player.CWPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WeaponHandler implements Listener {

    private DvZ dvz;
    private HashMap<UUID, UUID> flailedPlayers = new HashMap<UUID, UUID>();

    public WeaponHandler(DvZ dvz) {
        this.dvz = dvz;

        /*
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, UUID> entry : flailedPlayers.entrySet()) {
                    Player caster = Bukkit.getPlayer(entry.getKey());
                    Player target = Bukkit.getPlayer(entry.getValue());

                    if (caster == null || target == null || caster.isDead() || target.isDead()) {
                        flailedPlayers.remove(entry.getKey());
                        return;
                    }

                    Vector dir = target.getLocation().add(0,1,0).toVector().subtract(caster.getLocation().add(0, 1, 0).toVector());
                    float length = (float)dir.length();
                    dir.normalize();

                    float ratio = length / 10;
                    Vector v = dir.multiply(ratio);
                    Location loc = caster.getLocation().add(0,1,0).clone().subtract(v);
                    for (int i = 0; i < 10; i++) {
                        loc.add(v);
                        ParticleEffect.CRIT.display(0,0,0,0,1,loc);
                    }

                    dir = dir.multiply(-1);
                    target.setVelocity(target.getVelocity().add(dir.multiply(0.2f)));
                }
            }
        }.runTaskTimer(dvz, 0, 1);
        */
    }

    @EventHandler
    private void hotbarSwitch(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack prevItem = player.getInventory().getItem(event.getPreviousSlot());
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());


        if (prevItem != null && CWUtil.compareItems(Product.BATTLEAXE.getItem(), prevItem, true, false)) {
            if (player.hasPotionEffect(PotionEffectType.SLOW_DIGGING)) {
                player.removePotionEffect(PotionEffectType.SLOW_DIGGING);
            }
        }
        if (newItem != null && CWUtil.compareItems(Product.BATTLEAXE.getItem(), newItem, true, false)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 99999, 0));
        }
    }


    @EventHandler
    private void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        final Player damager = (Player)event.getDamager();

        CWPlayer cwd = dvz.getPM().getPlayer(damager);
        if (!cwd.isDwarf()) {
            return;
        }

        //Swing battleaxe
        if (CWUtil.compareItems(damager.getItemInHand(), Product.BATTLEAXE.getItem(), true, false)) {
            int enchantLvl = damager.getItemInHand().getEnchantmentLevel(Enchantment.LUCK);
            if (enchantLvl <= 0) {
                return;
            }

            if (enchantLvl == 1 && CWUtil.randomFloat() > 0.08f) {
                return;
            } else if (enchantLvl == 2 && CWUtil.randomFloat() > 1.15f) {
                return;
            }

            damager.getWorld().playSound(damager.getLocation(), Sound.WITHER_SHOOT, 0.3f, 1f);
            List<Player> players = CWUtil.getNearbyPlayers(damager.getLocation(), 2.5f);
            for (Player player : players) {
                if (dvz.getPM().getPlayer(player).isDwarf()) {
                    continue;
                }
                new CustomDmg(player, 4, "{0} died from {1}'s swinging axe", "Swinging axe", damager);
                Vector dir = player.getLocation().toVector().subtract(damager.getLocation().toVector()).normalize();
                player.setVelocity(player.getVelocity().add(dir.multiply(2)));
                damager.getWorld().playSound(damager.getLocation(), Sound.ZOMBIE_METAL, 0.2f, 2f);
            }

            AnimatedCircleEffect circleEffect = new AnimatedCircleEffect(dvz.getEM());
            circleEffect.particleList.add(new Particle(ParticleEffect.CRIT_MAGIC, 1f, 0.05f, 1f, 0, 10));
            circleEffect.setEntity(damager);
            circleEffect.angularVelocityX = 0;
            circleEffect.angularVelocityY = 1f;
            circleEffect.iterations = 10;
            circleEffect.angularVelocityZ = 0;
            circleEffect.radius = 1f;
            circleEffect.start();
        }

        //Lifesteal sword
        if (CWUtil.compareItems(damager.getItemInHand(), Product.GREATSWORD.getItem(), true, false)) {
            int enchantLvl = damager.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
            if (enchantLvl <= 0) {
                return;
            }

            if (enchantLvl == 1 && CWUtil.randomFloat() > 0.07f) {
                return;
            } else if (enchantLvl == 2 && CWUtil.randomFloat() > 0.14f) {
                return;
            } else if (enchantLvl == 3 && CWUtil.randomFloat() > 0.21f) {
                return;
            }

            damager.getWorld().playSound(damager.getLocation(), Sound.SILVERFISH_KILL, 0.5f, 0f);
            ParticleEffect.HEART.display(0.2f, 0.1f, 0.2f, 0, 3, damager.getLocation().add(0, 1.5f, 0));
            damager.setHealth(Math.max(damager.getHealth() + 1, damager.getMaxHealth()));
        }
    }

    /*
    @EventHandler
    private void interact(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (!cwp.isDwarf()) {
            return;
        }

        //Flail
        if (CWUtil.compareItems(player.getItemInHand(), Product.FIERY_FLAIL.getItem(), true, false)) {
            final Player target = CWUtil.getTargetedPlayer(player, 10);

            if (target == null) {
                if (flailedPlayers.containsKey(player.getUniqueId())) {
                    flailedPlayers.remove(player.getUniqueId());
                }
                return;
            }

            if (flailedPlayers.containsKey(player.getUniqueId())) {
                flailedPlayers.remove(player.getUniqueId());
            }
            flailedPlayers.put(player.getUniqueId(), target.getUniqueId());
        }
    }
    */

}
