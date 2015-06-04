package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.runnables.PickupRunnable;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pickup extends MobAbility {

    public static Map<UUID, UUID> pickupPlayers = new HashMap<UUID, UUID>();
    public static Map<UUID, PickupRunnable> pickupRunnables = new HashMap<UUID, PickupRunnable>();

    public Pickup() {
        super();
        ability = Ability.PICKUP;
        castItem = new DvzItem(Material.GOLD_HOE, 1, (short)0, displayName, 3, -1);
    }

    public void dropTarget(Player player) {
        UUID uuid = player.getUniqueId();
        if (!pickupPlayers.containsKey(uuid)) {
            return;
        }
        Player target = dvz.getServer().getPlayer(pickupPlayers.get(uuid));
        pickupPlayers.remove(uuid);
        pickupRunnables.remove(uuid);

        target.setVelocity(target.getVelocity().setY(0.6f));
        player.showPlayer(target);
        Util.disguisePlayer(player.getName(), (DvzClass.ENDERMAN.getClassClass().getDisguise()));

        CWUtil.sendActionBar(player, "");
        CWUtil.sendActionBar(target, "");

        player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 0.6f, 1);
        player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_HIT, 0.8f, 0.6f);
        player.getWorld().playSound(player.getLocation(), Sound.IRONGOLEM_THROW, 0.7f, 0f);
        ParticleEffect.PORTAL.display(0.5f, 1, 0.5f, 1, 200, player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection()));
        ParticleEffect.CLOUD.display(0.5f, 1, 0.5f, 0, 20, player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection()));
    }

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();
        if(!canCast(player) || !isCastItem(player.getItemInHand())) {
            return;
        }

        if(!dvz.getPM().getPlayer(target).isDwarf()) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't pick up monsters! &4&l<<"));
            return;
        }

        if (pickupPlayers.containsKey(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou already picked a player up! &4&l<<"));
            return;
        }

        if (dvz.getPM().getPlayer(player).getEndermanBlock() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cPlace the block you're holding first. &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        player.hidePlayer(target);
        pickupPlayers.put(player.getUniqueId(), target.getUniqueId());
        PickupRunnable runnable = new PickupRunnable(dvz, player, target, new Vector(1, 0.7f, 0));
        runnable.runTaskTimer(dvz, 1, 1);
        pickupRunnables.put(player.getUniqueId(), runnable);
        Util.disguisePlayer(player.getName(), (DvzClass.ENDERMAN.getClassClass().getDisguise() + " setAggressive true"));

        player.getWorld().playSound(player.getLocation(), Sound.BAT_TAKEOFF, 0.8f, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENDERMAN_HIT, 1f, 0);
        ParticleEffect.PORTAL.display(0.5f, 2, 0.5f, 1, 500, player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection()));
        ParticleEffect.SPELL_WITCH.display(1, 2, 1, 0, 20, player.getLocation().add(0, 1, 0).add(player.getLocation().getDirection()));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.getPlayerClass() != DvzClass.ENDERMAN) {
            return;
        }
        if (!isCastItem(player.getItemInHand())) {
            return;
        }

        if (pickupPlayers.containsKey(player.getUniqueId())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cDrop the player you're holding first. &8(&7Sneak&8) &4&l<<"));
            return;
        }

        Block block = event.getClickedBlock();

        //Place block
        if (cwp.getEndermanBlock() != Material.AIR) {
            if (block.getRelative(event.getBlockFace()).getType() == Material.AIR) {
                block.getRelative(event.getBlockFace()).setType(cwp.getEndermanBlock());
                cwp.setEndermanBlock(Material.AIR);
                Util.disguisePlayer(player.getName(), DvzClass.ENDERMAN.getClassClass().getDisguise());

                ParticleEffect.PORTAL.display(0.7f, 0.7f, 0.7f, 0, 20, block.getRelative(event.getBlockFace()).getLocation().add(0.5f, 0.5f, 0.5f), 500);
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getRelative(event.getBlockFace()).getType(), (byte) block.getRelative(event.getBlockFace()).getData()), 0.5f, 0.5f, 0.5f, 0.1f, 20, block.getRelative(event.getBlockFace()).getLocation(), 500);
                block.getWorld().playSound(block.getLocation(), Sound.ITEM_PICKUP, 1, 0);
            } else {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't place the block here! &4&l<<"));
            }
            return;
        }

        //Pickup block
        if(!canCast(player)) {
            return;
        }

        if (!Util.isDestroyable(block.getType())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThis block can't be picked up! &4&l<<"));
            return;
        }

        if (onCooldown(player, "block", getIntOption("block-cooldown"))) {
            return;
        }
        cwp.setEndermanBlock(block.getType());
        Util.disguisePlayer(player.getName(), (DvzClass.ENDERMAN.getClassClass().getDisguise() + " setHideHeldItemFromSelf false setItemInHand " + block.getType().getId() + ":" + block.getData()));

        ParticleEffect.PORTAL.display(0.7f, 0.7f, 0.7f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f), 500);
        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), (byte)block.getData()), 0.5f, 0.5f, 0.5f, 0.1f, 20, block.getLocation(), 500);
        block.getWorld().playSound(block.getLocation(), Sound.ITEM_PICKUP, 1, 2);

        block.setType(Material.AIR);
    }
}
