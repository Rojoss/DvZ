package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.hat.Hat;
import com.clashwars.cwcore.hat.HatManager;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.listeners.custom.GameResetEvent;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
                Long t = System.currentTimeMillis();
                HashMap<UUID, Location> blocksClone = new HashMap<UUID, Location>(blocks);
                for (UUID uuid : blocksClone.keySet()) {
                    Player player = dvz.getServer().getPlayer(uuid);
                    if (player == null || !player.isOnline() || player.isDead()) {
                        removeBlock(uuid);
                        continue;
                    }
                    if (player.getLocation().getBlockX() != blocksClone.get(uuid).getBlockX() || player.getLocation().getBlockZ() != blocksClone.get(uuid).getBlockZ() || player.getLocation().getBlockY() != blocksClone.get(uuid).getBlockY()) {
                        removeBlock(uuid);
                        CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou are no longer a block because you moved! &4&l<<"));
                        continue;
                    }
                }
            }
        }.runTaskTimer(dvz, 0, 5);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }
        Long t = System.currentTimeMillis();
        Block block = triggerLoc.getBlock();
        if (block == null || block.getType() == Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cClick a valid block to transform in to! &4&l<<"));
            dvz.logTimings("Camouflage.castAbility()[invalid block]", t);
            return;
        }

        if (dvz.getUndestroyableBlocks().contains(block.getType())) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't camouflage in to this block! &4&l<<"));
            dvz.logTimings("Camouflage.castAbility()[undestroyable block]", t);
            return;
        }

        Block disguiseBlock = player.getLocation().getBlock();
        if (disguiseBlock.getType() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't create a block where you're standing! &4&l<<"));
            dvz.logTimings("Camouflage.castAbility()[not air]", t);
            return;
        }

        if (player.getActivePotionEffects().size() > 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't camouflage while you have active potion effects! &4&l<<"));
            dvz.logTimings("Camouflage.castAbility()[active effects]", t);
            return;
        }

        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("keep").contains(disguiseBlock)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCamouflage can't be used inside the keep right now &4&l<<"));
            return;
        }
        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("innerwall").contains(disguiseBlock)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCamouflage can't be used inside the keep right now &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            dvz.logTimings("Camouflage.castAbility()[cd]", t);
            return;
        }

        Location loc = disguiseBlock.getLocation();
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(player.getLocation().getPitch());
        player.teleport(loc.add(0.5f, 0, 0.5f));
        blocks.put(player.getUniqueId(), disguiseBlock.getLocation());
        disguiseBlock.setType(block.getType());
        disguiseBlock.setData(block.getData());
        String blockName = block.getType().toString().toLowerCase().replace("_", " ");
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&2&l>> &aYou turned in to a &6" + (blockName.contains("block") ? blockName : blockName + " block") + "&a! &cDon't move! &2&l<<"));

        player.setGameMode(GameMode.SPECTATOR);
        player.setVelocity(new Vector(0,0,0));
        if (HatManager.hasHat(player)) {
            Hat hat = HatManager.getHat(player);
            hat.unequip();
        }
    }


    public static void removeBlock(UUID ownerUUID) {
        Block disguiseBlock = blocks.get(ownerUUID).getBlock();
        disguiseBlock.setType(Material.AIR);
        blocks.remove(ownerUUID);

        Player player = Bukkit.getPlayer(ownerUUID);
        if (player != null && player.isOnline()) {
            player.setGameMode(GameMode.SURVIVAL);
            Location loc = disguiseBlock.getLocation();
            loc.setYaw(player.getLocation().getYaw());
            loc.setPitch(player.getLocation().getPitch());
            player.teleport(loc.add(0.5f, 0, 0.5f));
            if (HatManager.hasHat(player)) {
                HatManager.getHat(player).equip();
            }
        }
    }

    public static void removeAllBlocks() {
        HashMap<UUID, Location> blocksClone = new HashMap<UUID, Location>(blocks);
        for (UUID uuid : blocksClone.keySet()) {
            removeBlock(uuid);
        }
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);

        //Allow monsters to left click blocks and reveal players.
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        HashMap<UUID, Location> blocksClone = new HashMap<UUID, Location>(blocks);
        for (Map.Entry<UUID, Location> entry : blocksClone.entrySet()) {
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
    private void teleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
            if (blocks.containsKey(event.getPlayer().getUniqueId())) {
                CWUtil.sendActionBar(event.getPlayer(), CWUtil.integrateColor("&4&l>> &cYou can't use this! &4You are no longer a block! &4&l<<"));
                removeBlock(event.getPlayer().getUniqueId());
                event.getPlayer().teleport(event.getFrom());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void playerQuit(PlayerQuitEvent event) {
        if (blocks.containsKey(event.getPlayer().getUniqueId())) {
            removeBlock(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        removeAllBlocks();
    }

    @EventHandler
    private void gameReset(GameResetEvent event) {
        removeAllBlocks();
    }
}
