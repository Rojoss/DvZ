package com.clashwars.dvz.abilities.dwarves.bonus;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.events.DelayedPlayerInteractEvent;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.GameState;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Set;

public class Torrent extends BaseAbility {

    public Torrent() {
        super();
        ability = Ability.TORRENT;
        castItem = new DvzItem(Material.RAW_FISH, 1, (short)0, displayName, -1, -1, false);
    }

    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (dvz.getGM().getState() == GameState.DRAGON) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThe dragon his powers are blocking you from using this right now! &4&l<<"));
            return;
        }
        Long t = System.currentTimeMillis();
        //Get the block clicked and validate it.
        List<Block> blocks = player.getLastTwoTargetBlocks((Set<Material>)null, 32);
        Block targetBlock = blocks.get(0);
        if (targetBlock == null) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cAim at a block within 30 block range to cast this! &4&l<<"));
            return;
        }
        if (targetBlock.getType() == Material.AIR) {
            if (targetBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cAim at a block within 30 block range to cast this! &4&l<<"));
                return;
            }
            targetBlock = targetBlock.getRelative(BlockFace.DOWN);
        }
        targetBlock = targetBlock.getRelative(BlockFace.UP);

        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("keep").contains(targetBlock)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTorrent can't be used inside the keep right now &4&l<<"));
            return;
        }
        if (!dvz.getGM().isMonsters() && dvz.getMM().getActiveMap().getCuboid("innerwall").contains(targetBlock)) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cTorrent can't be used inside the keep right now &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }

        //Launch players up (the water is just for effect)
        List<Player> players = CWUtil.getNearbyPlayers(targetBlock.getLocation(), 4f);
        for (Player p : players) {
            if (dvz.getPM().getPlayer((Player)p).isDwarf()) {
                continue;
            }

            new AbilityDmg(p, 0, ability, player);

            //The closer to target the further up. (If on the edge push away instead of up)
            double distance = p.getLocation().distance(targetBlock.getLocation());
            if (distance < 2.8f) {
                p.setVelocity(p.getVelocity().add(new Vector(0, 2f - distance / 6, 0)));
            } else {
                Vector dir = p.getLocation().toVector().subtract(targetBlock.getLocation().toVector());
                p.setVelocity(p.getVelocity().add(new Vector(dir.getX() / 1.5f, 1f, dir.getY() / 1.5f)));
            }
        }

        //Spawn the torrent with a bit delay.
        final Block torrentCenter = targetBlock;
        new BukkitRunnable() {
            int height = 0;
            boolean goingDown = false;
            boolean removedHalf = false;

            @Override
            public void run() {
                Long t = System.currentTimeMillis();
                //Cloud around torrent at start
                if (height == 0 && !goingDown) {
                    for (int x = -5; x < 5; x++) {
                        for (int z = -5; z < 5; z++) {
                            Block b = torrentCenter.getRelative(x,0,z);
                            double centerDistance = b.getLocation().add(0.5f, 0.5f, 0.5f).toVector().distance(torrentCenter.getLocation().toVector());
                            if (centerDistance > 3f && centerDistance < 4f) {
                                ParticleEffect.CLOUD.display(0.4f, 0.2f, 0.4f, 0, 4, b.getLocation().add(0.5f,0.2f,0.5f));
                                ParticleEffect.CLOUD.display(0.4f, 0.6f, 0.4f, 0, 2, b.getLocation().add(0.5f,1f,0.5f));
                            }
                        }
                    }
                }

                for (int x = -3; x < 3; x++) {
                    for (int z = -3; z < 3; z++) {
                        Block b = torrentCenter.getRelative(x,height,z);
                        if (b.getType() == Material.AIR || b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER) {
                            double centerDistance = b.getLocation().add(0.5f, 0.5f, 0.5f).toVector().distance(torrentCenter.getRelative(0, height, 0).getLocation().toVector());
                            //Torrent effect (small on top and round)
                            if (!goingDown && centerDistance > 3f - height / 2) {
                                //Random spikes (if block under is water then 50% chance to create water)
                                if (height == 0 || b.getRelative(BlockFace.DOWN).getType() != Material.STATIONARY_WATER || CWUtil.randomFloat() > 0.5f) {
                                    continue;
                                }
                            }
                            if (goingDown) {
                                //When going down first remove random water and the second time on same height it will remove all.
                                if (!removedHalf && CWUtil.randomFloat() > 0.5f) {
                                    continue;
                                }
                                if (b.getType() == Material.STATIONARY_WATER || b.getType() == Material.WATER) {
                                    b.setTypeIdAndData(0, (byte)0, false);
                                    ParticleEffect.WATER_SPLASH.display(0.2f, 0.2f, 0.2f, 0, 3, b.getLocation().add(0.5f,0.5f,0.5f));
                                }
                            } else {
                                if (b.getType() == Material.AIR) {
                                    b.setTypeIdAndData(9, (byte)0, false);
                                    ParticleEffect.WATER_SPLASH.display(0.2f, 0.2f, 0.2f, 0, 3, b.getLocation().add(0.5f,0.5f,0.5f));
                                }
                            }
                        }
                    }
                }

                if (!goingDown && height <= 7) {
                    torrentCenter.getWorld().playSound(torrentCenter.getLocation(), Sound.SWIM, 0.5f, 0);
                    height++;
                } else if (goingDown && height >= 0) {
                    //When going down run this twice per layer (first time it removes half and second time the rest)
                    if (removedHalf) {
                        removedHalf = false;
                        height--;
                    } else {
                        removedHalf = true;
                    }
                } else if (height > 7) {
                    goingDown = true;
                } else {
                    cancel();
                    return;
                }
                dvz.logTimings("Torrent.castAbilityRunnable()", t);
            }
        }.runTaskTimer(dvz, 6, 1);
        dvz.logTimings("Torrent.castAbility()", t);
    }


    @EventHandler
    public void interact(DelayedPlayerInteractEvent event) {
        super.interact(event);
    }
}
