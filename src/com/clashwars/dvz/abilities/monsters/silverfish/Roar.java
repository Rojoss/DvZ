package com.clashwars.dvz.abilities.monsters.silverfish;

import com.clashwars.cwcore.Debug;
import com.clashwars.cwcore.helpers.CWEntity;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.classes.DvzClass;
import com.clashwars.dvz.classes.monsters.Silverfish;
import com.clashwars.dvz.util.DvzItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Roar extends BaseAbility {

    private static List<Entity> silverfishes = new ArrayList<Entity>();

    public Roar() {
        super();
        ability = Ability.ROAR;
        castItem = new DvzItem(Material.COBBLE_WALL, 1, (short)0, displayName, 4, -1);
    }


    @Override
    public void castAbility(final Player player, Location triggerLoc) {
        if (onCooldown(player)) {
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.CAT_PURR, 2, 0.4f);
        player.getWorld().playSound(player.getLocation(), Sound.CAT_HISS, 0.05f, 0f);
        player.getWorld().playSound(player.getLocation(), Sound.SILVERFISH_HIT, 0.05f, 0f);

        List<Entity> newList = new ArrayList<Entity>();
        for (Entity e : silverfishes) {
            if (e != null && !e.isDead()) {
                newList.add(e);
            }
        }
        silverfishes = newList;

        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (iterations > 100) {
                    cancel();
                    return;
                }
                ParticleEffect.CLOUD.display(0.2f, 0.2f, 0.2f, 0, 1, player.getLocation());

                Location loc = player.getLocation();

                if (iterations % 10 == 0) {
                    List<Block> nearbyBlocks = new ArrayList<Block>();

                    for (int x = loc.getBlockX() - 8; x < loc.getBlockX() + 8; x++) {
                        for (int y = loc.getBlockY() - 6; y < loc.getBlockY() + 6; y++) {
                            for (int z = loc.getBlockZ() - 8; z < loc.getBlockZ() + 8; z++) {
                                Block block = loc.getWorld().getBlockAt(x,y,z);
                                if (block.getType() == Material.MONSTER_EGGS) {
                                    ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(block.getType(), (byte)block.getData()), 0.5f, 0.5f, 0.5f, 0.1f, 20, block.getLocation(), 500);
                                    nearbyBlocks.add(block);
                                }
                            }
                        }
                    }

                    int blocks = (int)CWUtil.lerp(0, nearbyBlocks.size(), (float)iterations / 100);
                    for (int i = 0; i < blocks; i++) {
                        Block block = nearbyBlocks.get(i);
                        block.getWorld().playSound(block.getLocation(), Sound.DIG_GRAVEL, 1, 0);
                        block.getWorld().playSound(block.getLocation(), Sound.SILVERFISH_HIT, 0.5f, 0);

                        if (silverfishes.size() > 500) {
                            cancel();
                            break;
                        }

                        if (block.hasMetadata("infected") || CWUtil.randomFloat() > 0.7f) {
                            int count = (int)dvz.getGM().getMonsterPower(1, 2);
                            for (int c = 0; c < count; c++) {
                                CWEntity silverfish = CWEntity.create(EntityType.SILVERFISH, block.getLocation().add(0.5f, 0.5f, 0.5f));
                                silverfish.entity().setMetadata("owner", new FixedMetadataValue(dvz, block.getMetadata("infected").get(0).asString()));
                                silverfishes.add(silverfish.entity());
                            }
                        }


                        block.setType(Material.AIR);
                    }
                }


                iterations++;
            }
        }.runTaskTimer(dvz, 0, 1);
    }


    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }
}
