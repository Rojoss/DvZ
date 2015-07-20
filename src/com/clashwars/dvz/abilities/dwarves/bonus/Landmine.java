package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Landmine extends BaseAbility {

    public Landmine() {
        super();
        ability = Ability.LAND_MINE;
        castItem = new DvzItem(Material.STONE_PLATE, 1, (short)0, displayName, -1, -1, false);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK}));

        new BukkitRunnable() {
            boolean give = true;

            @Override
            public void run() {
                give = !give;
                if (!dvz.getGM().isMonsters() && !give) {
                    return;
                }
                List<CWPlayer> players = dvz.getPM().getPlayers(ClassType.DWARF, true, false);
                for (CWPlayer cwp : players) {
                    if (cwp.getPlayerData().getDwarfAbilitiesReceived().contains(ability)) {
                        Player player = cwp.getPlayer();
                        int mineCount = 0;
                        for (int i = 0; i < player.getInventory().getSize(); i++) {
                            ItemStack item = player.getInventory().getItem(i);
                            if (item != null && Ability.LAND_MINE.getAbilityClass().isCastItem(item)) {
                                mineCount += item.getAmount();
                            }
                        }
                        if (mineCount < 16) {
                            castItem.giveToPlayer(cwp.getPlayer());
                        }
                    }
                }
            }
        }.runTaskTimer(dvz, 0, 600);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        Block mineBlock = triggerLoc.getBlock().getRelative(BlockFace.UP);
        if (mineBlock.getType() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't place a mine here! &4&l<<"));
            dvz.logTimings("Landmine.castAbility()[invalid loc]", t);
            return;
        }

        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("keep").contains(mineBlock)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cMines can't be placed inside the keep right now &4&l<<"));
            return;
        }
        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("innerwall").contains(mineBlock)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cMines can't be placed inside the keep right now &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            dvz.logTimings("Landmine.castAbility()[cd]", t);
            return;
        }

        mineBlock.setType(Material.TRIPWIRE);
        mineBlock.setMetadata("owner", new FixedMetadataValue(dvz, player.getName()));
        ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 0, 20, mineBlock.getLocation().add(0.5f, 0.1f, 0.5f), 500);
        triggerLoc.getWorld().playSound(triggerLoc, Sound.DOOR_OPEN, 1, 2);
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&2&l>> &aMine placed! &2&l<<"));
        CWUtil.removeItemsFromHand(player, 1);
        dvz.logTimings("Landmine.castAbility()", t);
    }

    @EventHandler
    private void playerMove(PlayerMoveEvent event) {
        Long t = System.currentTimeMillis();
        if (event.getTo().getBlock().getType() != Material.TRIPWIRE) {
            return;
        }

        if (!event.getTo().getBlock().hasMetadata("owner")) {
            return;
        }

        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());
        if (!cwp.isMonster()) {
            dvz.logTimings("Landmine.playerMove()[not monster]", t);
            return;
        }

        explodeMine(event.getTo(), event.getPlayer());
        dvz.logTimings("Landmine.playerMove()", t);
    }

    public void explodeMine(Location loc, Player triggerPlayer) {
        if (loc.getBlock().getType() != Material.TRIPWIRE) {
            return;
        }

        OfflinePlayer owner = dvz.getServer().getOfflinePlayer(loc.getBlock().getMetadata("owner").get(0).asString());

        loc.getBlock().setType(Material.AIR);
        ParticleEffect.SMOKE_LARGE.display(2f, 2f, 2f, 0, 200, loc, 500);
        ParticleEffect.EXPLOSION_NORMAL.display(2f, 2f, 2f, 0, 3, loc, 200);
        loc.getWorld().playSound(loc, Sound.EXPLODE, 0.8f, 0.3f);

        //Damage players
        List<Player> players = CWUtil.getNearbyPlayers(loc, 3);
        for (Player p : players) {
            if (dvz.getPM().getPlayer(p).isMonster()) {
                if (owner == null) {
                    new AbilityDmg(p, 8, ability);
                } else {
                    new AbilityDmg(p, 8, ability, owner);
                }
                CWUtil.sendActionBar(p, CWUtil.integrateColor("&4&l>> &8Hit by a landmine! &4&l<<"));
            }
        }

        //Explode nearby mines too
        for (int x = loc.getBlockX() - 2; x < loc.getBlockX() + 2; x++) {
            for (int y = loc.getBlockY() - 1; y < loc.getBlockY() + 1; y++) {
                for (int z = loc.getBlockZ() - 2; z < loc.getBlockZ() + 2; z++) {
                    explodeMine(loc.getWorld().getBlockAt(x, y, z).getLocation(), triggerPlayer);
                }
            }
        }
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
