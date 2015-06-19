package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Landmine extends BaseAbility {

    public Landmine() {
        super();
        ability = Ability.LAND_MINE;
        castItem = new DvzItem(Material.STONE_PLATE, 1, (short)0, displayName, -1, -1, false);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (triggerLoc.getBlock().getRelative(BlockFace.UP).getType() != Material.AIR) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cCan't place a mine here! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        triggerLoc.getBlock().getRelative(BlockFace.UP).setType(Material.TRIPWIRE);
        ParticleEffect.SMOKE_NORMAL.display(0.3f, 0.3f, 0.3f, 0, 20, triggerLoc.add(0.5f, 0.5f, 0.5f), 500);
        triggerLoc.getWorld().playSound(triggerLoc, Sound.DOOR_OPEN, 1, 2);
        CWUtil.sendActionBar(player, CWUtil.integrateColor("&2&l>> &aMine placed! &2&l<<"));
    }

    @EventHandler
    private void playerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlock().getType() != Material.TRIPWIRE) {
            return;
        }

        CWPlayer cwp = dvz.getPM().getPlayer(event.getPlayer());
        if (!cwp.isMonster()) {
            return;
        }

        event.getTo().getBlock().setType(Material.AIR);
        ParticleEffect.SMOKE_LARGE.display(2f, 2f, 2f, 0, 200, event.getTo(), 500);
        ParticleEffect.EXPLOSION_NORMAL.display(2f, 2f, 2f, 0, 3, event.getTo(), 200);
        event.getPlayer().getWorld().playSound(event.getTo(), Sound.EXPLODE, 0.8f, 0.3f);

        List<Entity> entities = CWUtil.getNearbyEntities(event.getTo(), 3, Arrays.asList(new EntityType[] {EntityType.PLAYER}));
        for (Entity e : entities) {
            CWPlayer cwt = dvz.getPM().getPlayer((Player)e);
            if (cwt.isMonster()) {
                cwt.getPlayer().damage(10);
                CWUtil.sendActionBar(cwt.getPlayer(), CWUtil.integrateColor("&4&l>> &8Hit by a landmine! &4&l<<"));
            }
        }
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
