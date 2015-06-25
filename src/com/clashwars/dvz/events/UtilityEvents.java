package com.clashwars.dvz.events;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.stats.internal.StatType;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class UtilityEvents implements Listener {

    private final DvZ dvz;

    public UtilityEvents(DvZ dvz) {
        this.dvz = dvz;
    }

    @EventHandler
    private void damage(EntityDamageEvent event) {
        Long t = System.currentTimeMillis();
        //No durability loss
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack[] armorItems = ((Player) event.getEntity()).getInventory().getArmorContents();
            for (ItemStack armor : armorItems) {
                armor.setDurability((short) 0);
            }


            CWPlayer cwp = dvz.getPM().getPlayer(player);

            //Cancel damage if player has no class.
            if (cwp.getPlayerClass().isBaseClass()) {
                event.setCancelled(true);
            }

            //Cancel teleport if taking damage.
            if (cwp.isTeleporting()) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTeleport cancelled because you moved! &4&l<<"));
                cwp.resetTeleport();
            }
        }

        //No fall damage during dwarf time.
        if (dvz.getGM().isDwarves()) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
        dvz.logTimings("UtilityEvents.damage()", t);
    }


    @EventHandler
    private void interact(PlayerInteractEvent event) {
        //No durability loss
        if (event.getItem() != null && event.getItem().getType().getMaxDurability() > 0) {
            event.getItem().setDurability((short) 0);
        }
    }


    @EventHandler
    private void onDamageByEntity(EntityDamageByEntityEvent event) {
        Long t = System.currentTimeMillis();

        //Register arrow hits
        if (event.getDamager() instanceof Arrow) {
            if (event.getDamager().hasMetadata("shooter")) {
                Player shooter = dvz.getServer().getPlayer(event.getDamager().getMetadata("shooter").get(0).asString());
                if (shooter != null) {
                    dvz.getSM().changeLocalStatVal(shooter, StatType.COMBAT_ARROWS_HIT, 1);
                }
            }
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        int dmgModifier = 0;
        Player damaged = (Player) event.getEntity();

        //Increase monster base damage based on power
        if (event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();
            CWPlayer cwDamager = dvz.getPM().getPlayer(damager);
            CWPlayer cwDamaged = dvz.getPM().getPlayer(damaged);

            if (cwDamager.getPlayerClass().getType() == ClassType.MONSTER && cwDamaged.getPlayerClass().getType() == ClassType.DWARF) {
                dmgModifier += (int)dvz.getGM().getMonsterPower(4);
            }
        }

        if (damaged.isBlocking()) {
            //Custom block enchantment.
            //Per level block 0.5 hearth extra while blocking and 1.0 per level if also sneaking.
            if (!damaged.getItemInHand().getEnchantments().containsKey(Enchantment.DURABILITY)) {
                event.setDamage(event.getDamage() + dmgModifier);
                dvz.logTimings("UtilityEvents.damageByEntity()[no block enchant]", t);
                return;
            }

            int enchantLvl = damaged.getItemInHand().getEnchantmentLevel(Enchantment.DURABILITY);
            boolean sneaking = damaged.isSneaking();
            int dmgReduction = enchantLvl * (sneaking ? 2 : 1);
            if (dmgReduction > 0) {
                dmgModifier -= dmgReduction;
                damaged.getWorld().playSound(damaged.getLocation(), Sound.ZOMBIE_METAL, 0.3f, 2 / enchantLvl);
            }
        }

        event.setDamage(event.getDamage() + dmgModifier);
        dvz.logTimings("UtilityEvents.damageByEntity()", t);
    }


    @EventHandler
    private void chat(final AsyncPlayerChatEvent event) {
        dvz.getSM().changeLocalStatVal(event.getPlayer(), StatType.GENERAL_CHAT_MESSAGES, 1);

        //Tips based on keywords
        final String tip = dvz.getTM().getTipFromChat(event.getMessage().replaceAll("[^a-zA-Z ]", ""), dvz.getPM().getPlayer(event.getPlayer()));
        boolean force = false;
        if (event.getMessage().startsWith("!") || event.getMessage().startsWith("?") && event.getMessage().length() > 3) {
            event.setCancelled(true);
            force = true;
            if (tip == null || tip.isEmpty()) {
                event.getPlayer().sendMessage(Util.formatMsg("&cNo answer to this question. (Remove the ! or ? from the start of your message to chat normally)"));
            }
        }
        if (tip != null && !tip.isEmpty()) {
            if (force || dvz.getSettingsCfg().getSettings(event.getPlayer().getUniqueId()).tips) {
                event.setMessage(event.getMessage() + "*");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().sendMessage(CWUtil.integrateColor("&2&lINFO&8&l: &7" + tip));
                    }
                }.runTaskLater(dvz, 5);
            }
        }
    }


    @EventHandler
    private void FoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        //No food loss during dwarf/lobby time and no food loss for monsters.
        final Player player = (Player) event.getEntity();
        if (dvz.getPM().getPlayer(player).getPlayerClass().getType() == ClassType.MONSTER || dvz.getGM().isDwarves() || dvz.getPM().getPlayer(player).getPlayerClass().isBaseClass()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setFoodLevel(20);
                }
            }.runTaskLater(dvz, 5);
        }
    }


    @EventHandler
    private void foodEat(final PlayerItemConsumeEvent event) {
        //More food restored from bread
        if (event.getItem().getType() == Material.BREAD) {
            event.getPlayer().setSaturation(event.getPlayer().getSaturation() + 6);
            event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 5);
            dvz.getSM().changeLocalStatVal(event.getPlayer(), StatType.DWARF_BREAD_EATEN, 1);
        }

        //Cancel potion drinking.
        if (event.getItem().getType() == Material.POTION) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    private void fallingBlockLand(EntityChangeBlockEvent event) {
        //Remove fire that fell after 10 seconds. (For example firebreath from dragon)
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (event.getTo() != Material.FIRE) {
            return;
        }

        final Block block = event.getBlock();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (block.getType() == Material.FIRE) {
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(dvz, 200);

    }

    @EventHandler
    public void shootArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        if (event.getProjectile() == null || !(event.getProjectile() instanceof Arrow)) {
            return;
        }
        Player shooter = (Player)event.getEntity();
        event.getProjectile().setMetadata("shooter", new FixedMetadataValue(dvz, shooter.getName()));
        dvz.getSM().changeLocalStatVal(shooter, StatType.COMBAT_ARROWS_SHOT, 1);
    }

    @EventHandler
    public void onArrowHit(final ProjectileHitEvent event){
        if(!(event.getEntity() instanceof Arrow)) {
            return;
        }
        //Ignore player shot arrows
        if (event.getEntity().hasMetadata("shooter")) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                event.getEntity().remove();
            }
        }.runTaskLater(dvz, 5);
    }
}
