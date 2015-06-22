package com.clashwars.dvz.abilities.dragons.water;

import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Geyser extends BaseAbility {

    public Geyser() {
        super();
        ability = Ability.GEYSER;
        castItem = new DvzItem(Material.DIAMOND_AXE, 1, (short)0, displayName, -1, -1);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        Long t = System.currentTimeMillis();
        if (onCooldown(player)) {
            dvz.logTimings("Geyser.castAbility()[cd]", t);
            return;
        }

        final Map<UUID, Vector> players = new HashMap<UUID, Vector>();
        List<Entity> entities = player.getNearbyEntities(getFloatOption("range"), getFloatOption("range"), getFloatOption("range"));
        for (Entity e : entities) {
            if (e instanceof Player) {
                if (!dvz.getPM().getPlayer((Player)e).isDwarf()) {
                    continue;
                }
                players.put(((Player)e).getUniqueId(), ((Player)e).getLocation().toVector());
                e.setVelocity(new Vector(0, getFloatOption("force"), 0));
                ((Player) e).damage(dvz.getGM().getDragonPower() * 3 - 3);
            }
        }

        new BukkitRunnable() {
            private int tick = 0;

            @Override
            public void run() {
                if (onTick(tick, getIntOption("geyser-height"), players, player)) {
                    cancel();
                }
                tick++;
            }
        }.runTaskTimer(dvz, 8, 1);
        dvz.logTimings("Geyser.castAbility()", t);
    }

    protected boolean onTick(int tick, int geyserHeight, Map<UUID, Vector> players, Player caster) {
        Long t = System.currentTimeMillis();
        //Completed animation
        if (tick > geyserHeight*2) {
            return true;
        }

        for (UUID uuid : players.keySet()) {
            Player player = dvz.getServer().getPlayer(uuid);
            Location loc = players.get(uuid).toLocation(player.getWorld());

            if (!player.getName().equals(caster.getName())) {
                if (tick < geyserHeight) { // Animation Up
                    Block block = loc.add(0,tick,0).getBlock();
                    if(block.getType() == Material.AIR) {
                        block.setTypeId(9, false);
                    }
                } else { // Animation Down
                    int n = geyserHeight-(tick-geyserHeight)-1;
                    Block block = loc.add(0,n,0).getBlock();
                    if(block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
                        block.setTypeId(0, false);
                    }
                }
            }
        }
        dvz.logTimings("Geyser.onTick()", t);
        return false;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
