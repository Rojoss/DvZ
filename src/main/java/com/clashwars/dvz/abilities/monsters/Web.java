package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.helpers.CWItem;
import com.clashwars.dvz.abilities.Ability;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Web extends MobAbility {

    public Web() {
        super();
        ability = Ability.WEB;
        castItem = new CWItem(Material.WEB, 1, (short) 0, displayName);
        castActions = new ArrayList<Action>(Arrays.asList(new Action[]{Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK}));
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        FallingBlock web = player.getLocation().getWorld().spawnFallingBlock(player.getLocation(), Material.WEB, (byte) 0);
        web.setVelocity(player.getLocation().getDirection().multiply(getDoubleOption("multiplier")));
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}