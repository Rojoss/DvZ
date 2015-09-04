package com.clashwars.dvz.abilities.dwarves.builder;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reinforce extends BaseAbility {

    public Reinforce() {
        super();
        ability = Ability.REINFORCE;
        displayName = "&fStone Bricks";
        castItem = new DvzItem(Material.INK_SACK, 1, (short) 8, displayName, -1, -1, true);
        castActions = new ArrayList<Action>(Arrays.asList(Action.LEFT_CLICK_BLOCK, Action.RIGHT_CLICK_BLOCK));
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (triggerLoc.getBlock().getType() != Material.SMOOTH_BRICK) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cClick on stone to reinforce it! &4&l<<"));
            dvz.logTimings("Reinforce.castAbility()[not cracked stone]", t);
            return;
        }

        List<Block> blocks = new ArrayList<Block>();
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block b = triggerLoc.getBlock().getRelative(x,y,z);
                    if (b.getType() == Material.SMOOTH_BRICK && b.getData() == 2) {
                        if (b.getLocation().distance(triggerLoc.getBlock().getLocation()) > 2.5f) {
                            continue;
                        }
                        blocks.add(b);
                    }
                }
            }
        }

        if (blocks.size() <= 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cNo cracked stone to reinforce! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            dvz.logTimings("Reinforce.castAbility()[cd]", t);
            return;
        }

        dvz.getSM().changeLocalStatVal(player, StatType.BUILDER_STONE_REINFORCED, blocks.size());
        for (Block b : blocks) {
            b.setData((byte) 0);
            ParticleEffect.SMOKE_NORMAL.display(0.6f, 0.6f, 0.6f, 0.01f, 20, b.getLocation().add(0.5f, 0.5f, 0.5f));
            b.getWorld().playSound(b.getLocation(), Sound.SLIME_ATTACK, CWUtil.randomFloat(0.1f, 0.6f), CWUtil.randomFloat(0.5f, 1.5f));
        }

        dvz.getPM().getPlayer(player).addClassExp(2);
        dvz.logTimings("Reinforce.castAbility()", t);
    }

    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
