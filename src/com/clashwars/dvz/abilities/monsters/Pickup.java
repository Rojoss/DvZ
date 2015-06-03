package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.runnables.PickupRunnable;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Material;
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

        target.setVelocity(target.getVelocity().setY(0.5f));
        target.showPlayer(player);
        Util.disguisePlayer(player.getName(), (DvzClass.ENDERMAN.getClassClass().getDisguise()));
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
            return;
        }

        if (pickupPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(Util.formatMsg("&cDrop the player you're holding first. &8(&7Sneak&8)"));
            return;
        }

        if (dvz.getPM().getPlayer(player).getEndermanBlock() != Material.AIR) {
            player.sendMessage(Util.formatMsg("&cPlace the block you're holding first."));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        target.hidePlayer(player);
        pickupPlayers.put(player.getUniqueId(), target.getUniqueId());
        PickupRunnable runnable = new PickupRunnable(dvz, player, target, new Vector(0, 0, 0));
        runnable.runTaskTimer(dvz, 1, 1);
        pickupRunnables.put(player.getUniqueId(), runnable);
        Util.disguisePlayer(player.getName(), (DvzClass.ENDERMAN.getClassClass().getDisguise() + " setAggressive true"));
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
            player.sendMessage(Util.formatMsg("&cDrop the player you're holding first. &8(&7Sneak&8)"));
            return;
        }

        Block block = event.getClickedBlock();

        //Place block
        if (cwp.getEndermanBlock() != Material.AIR) {
            if (block.getRelative(event.getBlockFace()).getType() == Material.AIR) {
                block.getRelative(event.getBlockFace()).setType(cwp.getEndermanBlock());
                cwp.setEndermanBlock(Material.AIR);
                Util.disguisePlayer(player.getName(), DvzClass.ENDERMAN.getClassClass().getDisguise());
            } else {
                player.sendMessage(Util.formatMsg("&cCan't place block here."));
            }
            return;
        }

        //Pickup block
        if(!canCast(player)) {
            return;
        }

        if (!Util.isDestroyable(block.getType())) {
            return;
        }

        if (onCooldown(player)) {
            return;
        }
        cwp.setEndermanBlock(block.getType());
        Util.disguisePlayer(player.getName(), (DvzClass.ENDERMAN.getClassClass().getDisguise() + " setHideHeldItemFromSelf false setItemInHand " + block.getType().getId() + ":" + block.getData()));
        block.setType(Material.AIR);
    }
}
