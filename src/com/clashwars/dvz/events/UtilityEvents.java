package com.clashwars.dvz.events;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.DvZ;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.maps.ShrineBlock;
import com.clashwars.dvz.maps.ShrineType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EventListener;

public class UtilityEvents implements Listener {

    private final DvZ dvz;

    public UtilityEvents(DvZ dvz) {
        this.dvz = dvz;
    }


    @EventHandler
    private void damage(EntityDamageEvent event) {
        //No durability loss
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            for (ItemStack armor : ((Player) event.getEntity()).getInventory().getArmorContents()) {
                armor.setDurability((short) 0);
            }

            //Cancel damage if player has no class.
            if (dvz.getPM().getPlayer(player).getPlayerClass().isBaseClass()) {
                event.setCancelled(true);
            }
        }

        //No fall damage during dwarf time.
        if (dvz.getGM().isDwarves()) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
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
        //Custom block enchantment.
        //Per level block 0.5 hearth extra while blocking and 1.0 per level if also sneaking.
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damaged = (Player) event.getEntity();

        if (!damaged.isBlocking()) {
            return;
        }

        if (!damaged.getItemInHand().getEnchantments().containsKey(Enchantment.DURABILITY)) {
            return;
        }

        int enchantLvl = damaged.getItemInHand().getEnchantmentLevel(Enchantment.DURABILITY);
        boolean sneaking = damaged.isSneaking();
        event.setDamage(event.getDamage() - enchantLvl * (sneaking ? 2 : 1));
    }


    @EventHandler
    private void chat(final AsyncPlayerChatEvent event) {
        //Tips based on keywords
        final String tip = dvz.getTM().getTipFromChat(event.getMessage().replaceAll("[^a-zA-Z ]", ""), dvz.getPM().getPlayer(event.getPlayer()));
        if (event.getMessage().startsWith("!") || event.getMessage().startsWith("?") && event.getMessage().length() > 3) {
            event.setCancelled(true);
            if (tip == null || tip.isEmpty()) {
                event.getPlayer().sendMessage(Util.formatMsg("&cNo answer to this question. (Remove the ! or ? from the start of your message to chat normally)"));
            }
        }
        if (tip != null && !tip.isEmpty()) {
            event.setMessage(event.getMessage() + "*");
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().sendMessage(CWUtil.integrateColor("&2&lINFO&8&l: &7" + tip));
                }
            }.runTaskLater(dvz, 5);
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
    private void foodEat(PlayerItemConsumeEvent event) {
        //More food restored from bread
        if (event.getItem().getType() == Material.BREAD) {
            event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + 5);
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
    public void onArrowHit(final ProjectileHitEvent event){
        if(event.getEntity() instanceof Arrow){
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getEntity().remove();
                }
            }.runTaskLater(dvz, 5);
        }
    }
}
