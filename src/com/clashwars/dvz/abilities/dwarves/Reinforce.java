package com.clashwars.dvz.abilities.dwarves;

import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Reinforce extends DwarfAbility {

    public Reinforce() {
        super();
        ability = Ability.REINFORCE;
        displayName = "&fStone Bricks";
        castItem = new DvzItem(Material.SMOOTH_BRICK, 1, (short) 0, displayName, -1, -1, false);
        castActions = new ArrayList<Action>(Arrays.asList(Action.LEFT_CLICK_BLOCK));
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (triggerLoc.getBlock().getType() != Material.SMOOTH_BRICK || triggerLoc.getBlock().getData() == 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cClick on cracked stone to reinforce it! &4&l<<"));
            return;
        }

        triggerLoc.getBlock().setData((byte)0);
        ParticleEffect.SMOKE_NORMAL.display(0.6f, 0.6f, 0.6f, 0.01f, 20, triggerLoc.add(0.5f, 0.5f, 0.5f));
        triggerLoc.getWorld().playSound(triggerLoc, Sound.DIG_STONE, 0.8f, 1f);
        CWUtil.removeItemsFromHand(player, 1);
        player.updateInventory();
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
