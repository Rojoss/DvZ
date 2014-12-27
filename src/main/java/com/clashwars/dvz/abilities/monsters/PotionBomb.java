package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.cuboid.Cuboid;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.Util;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.*;

public class PotionBomb extends MobAbility {

    HashMap<Location, BukkitTask> bombs = new HashMap<Location, BukkitTask>();

    public PotionBomb() {
        super();
        ability = Ability.POTIONBOMB;
    }

    @EventHandler
    public void castAbility(Player player, Location triggerLoc) {
        if(triggerLoc.getWorld().getHighestBlockYAt(triggerLoc) > triggerLoc.getY() + 1) {
            player.sendMessage(Util.formatMsg("&cThere mustn't be a block above the bomb!"));
            return;
        }

        for(int y = 20; y >= 0; y++) {
            if(triggerLoc.add(0, y, 0).getBlock().getType().equals("Bedrock")) {
                player.sendMessage(Util.formatMsg("&cYou can't place that here!"));
                return;
            }
        }

        if(CWUtil.getNearbyEntities(triggerLoc, getFloatOption("radius")).isEmpty()) {
            player.sendMessage(Util.formatMsg("&cThere are no players nearby!"));
            return;
        }

        try {
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(triggerLoc.getWorld(), CWWorldGuard.getSchematicFile("PotionBomb"), triggerLoc, true, 0, true);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        } catch (DataException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FilenameException e) {
            e.printStackTrace();
        } catch (CommandException e) {
            e.printStackTrace();
        }

        triggerLoc.add(0, 1, 0).getBlock().setType(Material.SKULL);

        dvz.getServer().broadcastMessage(Util.formatMsg("&6A bomb has been placed at:"));
        dvz.getServer().broadcastMessage(Util.formatMsg("&6x: " + triggerLoc.getBlockX()));
        dvz.getServer().broadcastMessage(Util.formatMsg("&6y: " + triggerLoc.getBlockY()));
        dvz.getServer().broadcastMessage(Util.formatMsg("&6z: " + triggerLoc.getBlockZ()));

        BukkitTask bt = new BukkitRunnable() {
            @Override
            public void run() {
                //TODO: Add bomb explosion particles
                for(CWPlayer cwp : dvz.getPM().getPlayers(ClassType.DWARF, true)) {
                    for(PotionEffect pe : cwp.getPlayer().getActivePotionEffects()) {
                        if(pe.getType().equals(PotionEffectType.BLINDNESS)) {
                            cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getIntOption("blindness-duration") + pe.getDuration(), pe.getAmplifier()));
                        } else if (pe.getType().equals(PotionEffectType.POISON)) {
                            cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("poison-duration") + pe.getDuration(), pe.getAmplifier()));
                        }
                        return;
                    }

                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getIntOption("blindness-duration"), getIntOption("blindness-amplifier")));
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("poison-duration"), getIntOption("poison-amplifier")));
                }
            }
        }.runTaskLater(dvz, getIntOption("fuse-time"));

        bombs.put(triggerLoc.add(0, 1, 0), bt);
    }

    @EventHandler
    public void blockDestroy(BlockBreakEvent event) {
        if(bombs.containsKey(event.getBlock().getLocation())) {
            bombs.get(event.getBlock().getLocation()).cancel();
            bombs.remove(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
