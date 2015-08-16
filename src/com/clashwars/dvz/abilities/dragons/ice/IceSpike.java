package com.clashwars.dvz.abilities.dragons.ice;

import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwcore.utils.RandomUtils;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IceSpike extends BaseAbility {

    private HashMap<Vector, List<Vector>> spikes = new HashMap<Vector, List<Vector>>();

    public IceSpike() {
        super();
        ability = Ability.ICESPIKE;
        castItem = new DvzItem(Material.PACKED_ICE, 1, (short)0, displayName, -1, -1);
    }


    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        Location location = player.getLocation();
        Block center = location.getWorld().getHighestBlockAt(location);
        if (center.getType() != Material.AIR) {
            center = center.getRelative(BlockFace.UP);
        }

        //Push players away
        List<Player> players = CWUtil.getNearbyPlayers(center.getLocation(), 7f);
        for (Player p : players) {
            if (!dvz.getPM().getPlayer(p).isDwarf()) {
                continue;
            }
            Vector dir = p.getLocation().toVector().subtract(center.getLocation().toVector());
            p.setVelocity(p.getVelocity().add(new Vector(dir.getX(), 0.5f, dir.getZ())));
        }

        final Block spikeCenter = center;
        new BukkitRunnable() {
            int height = 0;
            List<Vector> iceBlocks = new ArrayList<Vector>();

            @Override
            public void run() {
                //Create the spike
                for (int x = -2; x < 2; x++) {
                    for (int z = -2; z < 2; z++) {
                        Block b = spikeCenter.getRelative(x,height,z);
                        if (b.getType() == Material.AIR || b.getType() == Material.ICE || b.getType() == Material.PACKED_ICE) {
                            double centerDistance = b.getLocation().add(0.5f, 0.5f, 0.5f).toVector().distance(spikeCenter.getRelative(0, height, 0).getLocation().toVector());
                            //Spike effect (small on top and round)
                            if (centerDistance > 2f - height / 2) {
                                //Random spikes (if block under is ice then 50% chance to create ice)
                                if (height == 0 || (b.getRelative(BlockFace.DOWN).getType() != Material.ICE && b.getRelative(BlockFace.DOWN).getType() != Material.PACKED_ICE) || CWUtil.randomFloat() > 0.5f) {
                                    continue;
                                }
                            }
                            if (b.getType() == Material.AIR) {
                                if (CWUtil.randomFloat() < 0.5f) {
                                    b.setTypeIdAndData(174, (byte)0, false);
                                } else {
                                    b.setTypeIdAndData(79, (byte)0, false);
                                }
                                iceBlocks.add(b.getLocation().toVector());
                                ParticleEffect.SNOW_SHOVEL.display(0.5f, 0.5f, 0.5f, 0, 10, b.getLocation().add(0.5f,1f,0.5f));
                            }
                        }
                    }
                }

                if (height <= 9) {
                    height++;
                } else {
                    //Spike created
                    spikes.put(spikeCenter.getLocation().toVector(), iceBlocks);

                    new BukkitRunnable() {
                        int i = 0;
                        @Override
                        public void run() {
                            //Particles
                            for (Vector v : iceBlocks) {
                                Location loc = v.toLocation(player.getWorld());
                                if (loc.getBlock().getType() == Material.ICE || loc.getBlock().getType() == Material.PACKED_ICE) {
                                    ParticleEffect.SNOW_SHOVEL.display(0.5f, 0.5f, 0.5f, 0, 5, loc.add(0.5f,0.5f,0.5f));
                                }
                            }
                            i++;
                            if (i > 30) {
                                //Explode
                                ParticleEffect.EXPLOSION_HUGE.display(0.5f, 0.5f, 0.5f, 0, dvz.getGM().getDragonPower(), spikeCenter.getLocation(), 500);
                                spikeCenter.getWorld().playSound(spikeCenter.getLocation(), Sound.EXPLODE, 2, 0);

                                for (Vector v : iceBlocks) {
                                    Location loc = v.toLocation(player.getWorld());
                                    if (loc.getBlock().getType() == Material.ICE || loc.getBlock().getType() == Material.PACKED_ICE) {
                                        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, loc.getBlock().getType(), loc.getBlock().getData());
                                        Vector dir = loc.toVector().subtract(spikeCenter.getLocation().toVector()).normalize();
                                        fallingBlock.setVelocity(dir.multiply(CWUtil.randomFloat(0.4f, 0.9f)));
                                        fallingBlock.setMetadata("spike", new FixedMetadataValue(dvz, true));

                                        ParticleEffect.SNOW_SHOVEL.display(0.5f, 0.5f, 0.5f, 0, 10, fallingBlock.getLocation().add(0.5f, 0.5f, 0.5f));

                                        loc.getBlock().setType(Material.AIR);
                                    }
                                }
                                cancel();
                            }
                        }
                    }.runTaskTimer(dvz, 0, 10);

                    cancel();
                }
            }
        }.runTaskTimer(dvz, 6, 1);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }


    @EventHandler
    private void fallingBlockLand(EntityChangeBlockEvent event) {
        if (!(event.getEntity() instanceof FallingBlock)) {
            return;
        }
        if (!event.getEntity().hasMetadata("spike")) {
            return;
        }

        Block block = event.getBlock();

        block.getWorld().playSound(block.getLocation(), Sound.GLASS, CWUtil.randomFloat(0.2f,0.6f), CWUtil.randomFloat(0,2));
        ParticleEffect.SNOW_SHOVEL.display(0.5f, 0.5f, 0.5f, 0, 20, block.getLocation().add(0.5f, 0.5f, 0.5f));

        List<Player> players = CWUtil.getNearbyPlayers(block.getLocation(), dvz.getGM().getDragonPower() + 2);
        for (Player p : players) {
            if(dvz.getPM().getPlayer(p).isDwarf()) {
                new AbilityDmg(p, 2, ability, dvz.getGM().getDragonPlayer());
            }
        }
    }

}
