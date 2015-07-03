package com.clashwars.dvz.events;

import com.clashwars.cwcore.CooldownManager;
import com.clashwars.cwcore.effect.Particle;
import com.clashwars.cwcore.effect.effects.AnimatedCircleEffect;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.Product;
import com.clashwars.dvz.damage.types.CustomDmg;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.runnables.FlailChainRunnable;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WeaponHandler implements Listener {

    private DvZ dvz;
    public static HashMap<UUID, FlailChainRunnable> flailedPlayers = new HashMap<UUID, FlailChainRunnable>();

    public WeaponHandler(DvZ dvz) {
        this.dvz = dvz;
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


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void entityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        final Player damager = (Player)event.getDamager();

        if (damager.getItemInHand() == null) {
            return;
        }

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
            } else if (enchantLvl == 2 && CWUtil.randomFloat() > 0.15f) {
                return;
            }

            damager.getWorld().playSound(damager.getLocation(), Sound.WITHER_SHOOT, 0.3f, 1f);
            List<Player> players = CWUtil.getNearbyPlayers(damager.getLocation(), 2.5f);
            for (Player player : players) {
                if (dvz.getPM().getPlayer(player).isDwarf()) {
                    continue;
                }
                new CustomDmg(player, 4, "{0} died from {1}'s swinging axe", "{1}'s swinging axe", damager);
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

            if (enchantLvl == 1 && CWUtil.randomFloat() > 0.1f) {
                return;
            } else if (enchantLvl == 2 && CWUtil.randomFloat() > 0.2f) {
                return;
            } else if (enchantLvl == 3 && CWUtil.randomFloat() > 0.3f) {
                return;
            }

            damager.getWorld().playSound(damager.getLocation(), Sound.SILVERFISH_KILL, 0.5f, 0f);
            ParticleEffect.HEART.display(0.2f, 0.1f, 0.2f, 0, 3, damager.getLocation().add(0, 1.5f, 0));
            new CustomDmg(damager, -2, "", "lifesteal");
        }


        //Flail fire
        if (CWUtil.compareItems(damager.getItemInHand(), Product.FIERY_FLAIL.getItem(), true, false)) {
            int enchantLvl = damager.getItemInHand().getEnchantmentLevel(Enchantment.FIRE_ASPECT);
            if (enchantLvl <= 0) {
                return;
            }

            ParticleEffect.FLAME.display(0.3f, 0.5f, 0.3f, 0, 10, event.getEntity().getLocation().add(0, 0.5f, 0));
        }
    }

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
            int enchantLvl = player.getItemInHand().getEnchantmentLevel(Enchantment.LURE);
            if (enchantLvl <= 0) {
                return;
            }

            int cooldownTime = 60000;
            int distance = 0;
            if (enchantLvl == 1) {
                cooldownTime = 25000;
                distance = 20;
            } else if (enchantLvl == 2) {
                cooldownTime = 12000;
                distance = 30;
            }

            if (flailedPlayers.containsKey(player.getUniqueId())) {
                flailedPlayers.get(player.getUniqueId()).cancel();
                flailedPlayers.remove(player.getUniqueId());
            }

            final Player target = CWUtil.getTargetedPlayer(player, distance);
            if (target == null) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cLook at a monster and hold right click to use this! &4&l<<"));
                return;
            }

            if (!dvz.getPM().getPlayer(target).isMonster()) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can only chain monsters! &4&l<<"));
                return;
            }

            CooldownManager.Cooldown cd = cwp.getCDM().getCooldown("flail-chain");
            if (cd == null) {
                cwp.getCDM().createCooldown("flail-chain", cooldownTime);
            } else if (!cd.onCooldown()) {
                cd.setTime(cooldownTime);
            } else {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&6&lFlail Chain &4&l> &7" + CWUtil.formatTime(cd.getTimeLeft(), "&c%S&4.&c%%%&4s")));
                return;
            }

            new FlailChainRunnable(player, target).runTaskTimer(dvz, 0, 1);
            new CustomDmg(target, 0, "{0} died from {1}'s flail chain", "{1}'s flail chain", player);
        }
    }

}
