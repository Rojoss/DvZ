package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.damage.types.AbilityDmg;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
        if (triggerLoc.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't place a mine here! &4&l<<"));
            dvz.logTimings("Landmine.castAbility()[invalid loc]", t);
            return;
        }

        if (onCooldown(player)) {
            dvz.logTimings("Landmine.castAbility()[cd]", t);
            return;
        }

        triggerLoc.getBlock().getRelative(BlockFace.UP).setType(Material.TRIPWIRE);
        triggerLoc.getBlock().getRelative(BlockFace.UP).setMetadata("owner", new FixedMetadataValue(dvz, player.getName()));
        ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 0, 20, triggerLoc.add(0.5f, 0.5f, 0.5f), 500);
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

        OfflinePlayer owner = dvz.getServer().getOfflinePlayer(event.getTo().getBlock().getMetadata("owner").get(0).asString());

        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());
        if (!cwp.isMonster()) {
            dvz.logTimings("Landmine.playerMove()[not monster]", t);
            return;
        }

        event.getTo().getBlock().setType(Material.AIR);
        ParticleEffect.SMOKE_LARGE.display(2f, 2f, 2f, 0, 200, event.getTo(), 500);
        ParticleEffect.EXPLOSION_NORMAL.display(2f, 2f, 2f, 0, 3, event.getTo(), 200);
        event.getPlayer().getWorld().playSound(event.getTo(), Sound.EXPLODE, 0.8f, 0.3f);

        List<Player> players = CWUtil.getNearbyPlayers(event.getTo(), 3);
        for (Player p : players) {
            if (dvz.getPM().getPlayer(p).isMonster()) {
                if (owner == null) {
                    new AbilityDmg(p, 10, ability);
                } else {
                    new AbilityDmg(p, 10, ability, owner);
                }
                CWUtil.sendActionBar(p, CWUtil.integrateColor("&4&l>> &8Hit by a landmine! &4&l<<"));
            }
        }
        dvz.logTimings("Landmine.playerMove()", t);
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
