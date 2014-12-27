package com.clashwars.dvz.abilities.monsters;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.runnables.PickupRunnable;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class Pickup extends MobAbility {

    public static Map<UUID, UUID> pickupPlayers = new HashMap<UUID, UUID>();

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

        target.setVelocity(target.getVelocity().setY(1.0f));
    }

    @EventHandler
    public void onEntityRightClick(PlayerInteractEntityEvent event) {
        if(!(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        Player target = (Player) event.getRightClicked();
        if(!canCast(player)) {
            return;
        }

        if(!dvz.getPM().getPlayer(target).isDwarf()) {
            return;
        }

        if (pickupPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(Util.formatMsg("&cFirst drop the player you're holding. &8(&7Sneak&8)"));
            return;
        }

        pickupPlayers.put(player.getUniqueId(), target.getUniqueId());
        //TODO: Proper offset
        new PickupRunnable(dvz, player, target, new Vector(0,0,0)).runTaskTimer(dvz, 1, 1);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (cwp.getPlayerClass() != ability.getDvzClass()) {
            return;
        }

        Block block = event.getClickedBlock();

        //Place block
        if (cwp.getEndermanBlock() != Material.AIR) {
            if (block.getRelative(event.getBlockFace()).getType() != Material.AIR) {
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

        cwp.setEndermanBlock(block.getType());
        block.setType(Material.AIR);
        //TODO: Disguise enderman with block.
    }
}
