package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Camouflage extends BaseAbility {

    public static HashMap<UUID, Location> blocks = new HashMap<UUID, Location>();

    public Camouflage() {
        super();
        ability = Ability.CAMOUFLAGE;
        castItem = new DvzItem(Material.STAINED_GLASS, 1, (short)15, displayName, -1, -1, false);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK}));

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : blocks.keySet()) {
                    Player player = dvz.getServer().getPlayer(uuid);
                    if (player == null || !player.isOnline() || player.isDead()) {
                        removeBlock(uuid);
                        continue;
                    }
                    if (player.getLocation().getBlockX() != blocks.get(uuid).getBlockX() || player.getLocation().getBlockZ() != blocks.get(uuid).getBlockZ() || player.getLocation().getBlockY() != blocks.get(uuid).getBlockY()) {
                        removeBlock(uuid);
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou are no longer a block because you moved! &4&l<<"));
                        continue;
                    }
                }
            }
        }.runTaskTimer(dvz, 0, 3);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Block block = triggerLoc.getBlock();
        if (block == null || block.getType() == Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cClick a valid block to transform in to! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        Block disguiseBlock = player.getLocation().getBlock();
        blocks.put(player.getUniqueId(), disguiseBlock.getLocation());
        disguiseBlock.setType(block.getType());
        String blockName = block.getType().toString().toLowerCase().replace("_", " ");
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&2&l>> &aYou turned in to a &6" + (blockName.contains("block") ? blockName : blockName + " block") + "&a! &cDon't move! &2&l<<"));

        player.setGameMode(GameMode.SPECTATOR);
        player.setVelocity(new Vector(0,0,0));
        if (HatManager.hasHat(player)) {
            HatManager.getHat(player).unequip();
        }
    }


    private void removeBlock(UUID ownerUUID) {
        blocks.get(ownerUUID).getBlock().setType(Material.AIR);
        blocks.remove(ownerUUID);

        Player player = dvz.getServer().getPlayer(ownerUUID);
        if (player != null && player.isOnline()) {
            player.setGameMode(GameMode.SURVIVAL);

            if (HatManager.hasHat(player)) {
                HatManager.getHat(player).equip();
            }
        }
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);

        //Allow monsters to left click blocks and reveal players.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }
        for (Map.Entry<UUID, Location> entry : blocks.entrySet()) {
            if (entry.getValue().equals(event.getClickedBlock().getLocation())) {
                Player player = dvz.getServer().getPlayer(entry.getKey());
                if (player != null && player.isOnline()) {
                    CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou were found by " + event.getPlayer().getDisplayName() + "&c! &4You are no longer a block! &4&l<<"));
                }
                removeBlock(entry.getKey());
            }
        }
    }

    @EventHandler
    private void playerQuit(PlayerJoinEvent event) {
        if (blocks.containsKey(event.getPlayer().getUniqueId())) {
            removeBlock(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        for (UUID uuid : blocks.keySet()) {
            removeBlock(uuid);
        }
    }
}
