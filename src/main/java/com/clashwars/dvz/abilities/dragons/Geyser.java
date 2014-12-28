package com.clashwars.dvz.abilities.dragons;

import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Geyser extends DragonAbility {

    public Geyser() {
        super();
        ability = Ability.GEYSER;
        castItem = new DvzItem(Material.DIAMOND_AXE, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        List<Player> nearby = new ArrayList<Player>();
        for (Entity e : player.getNearbyEntities(getFloatOption("range"), getFloatOption("range"), getFloatOption("range"))) {
            if(e instanceof Player) {
                e.setVelocity(new Vector(0, getFloatOption("force"), 0));
                Player p = (Player) e;
                onTick(0, getIntOption("geyser-height"), nearby, p.getLocation());
            }
        }
    }

    protected void onTick(int tick, int geyserHeight, List<Player> nearby, Location start) {
        if (tick > geyserHeight*2) {
            return;
        } else if (tick < geyserHeight) {
            Block block = start.clone().add(0,tick,0).getBlock();
            if(block.getType() == Material.AIR) {
                for (Player p : nearby) {
                        p.sendBlockChange(block.getLocation(), Material.WATER, (byte)0);
                }
            }
            onTick(tick+5, geyserHeight, nearby, start);
        } else {
            int n = geyserHeight-(tick-geyserHeight)-1; // top to bottom
            Block block = start.clone().add(0, n, 0).getBlock();
            for (Player p : nearby) {
                p.sendBlockChange(block.getLocation(), block.getType(), block.getData());
            }
            return;
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
