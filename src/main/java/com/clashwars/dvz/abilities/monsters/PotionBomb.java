package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.*;
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
import java.util.Arrays;
import java.util.HashMap;

public class PotionBomb extends MobAbility {

    HashMap<Location, BukkitTask> bombs = new HashMap<Location, BukkitTask>();

    public PotionBomb() {
        super();
        ability = Ability.POTIONBOMB;
        castItem = new DvzItem(Material.STICK, 1, (short)0, displayName, 3, -1);
    }

    @Override
    public void castAbility(Player player, Location triggerLoc) {
        if (triggerLoc.getBlock().getType() == Material.BEDROCK) {
            player.sendMessage(Util.formatMsg("&cYou can't place that here!"));
            return;
        }

        if(triggerLoc.getWorld().getHighestBlockYAt(triggerLoc) > triggerLoc.getY() + 1) {
            player.sendMessage(Util.formatMsg("&cThere mustn't be a block above the bomb!"));
            return;
        }

        Location newLoc = triggerLoc.clone();
        for (int y = 0; y < 20; y++) {
            if (newLoc.subtract(0, 1, 0).getBlock().getType() == Material.BEDROCK) {
                player.sendMessage(Util.formatMsg("&cYou can't place that here!"));
                return;
            }
        }

        if (CWUtil.getNearbyEntities(triggerLoc, getFloatOption("radius"), Arrays.asList(new EntityType[]{EntityType.PLAYER})).isEmpty()) {
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

        triggerLoc.getBlock().setType(Material.SKULL);
        triggerLoc.getBlock().setData((byte)1);
        BlockState state  = triggerLoc.getBlock().getState();
        if (state instanceof Skull) {
            ((Skull)state).setSkullType(SkullType.PLAYER);
            ((Skull)state).setOwner("Scott11B");
            ((Skull)state).update();
        }

        BukkitTask bt = new BukkitRunnable() {
            @Override
            public void run() {
                //TODO: Add bomb explosion particles
                for(CWPlayer cwp : dvz.getPM().getPlayers(ClassType.DWARF, true)) {
                    for (PotionEffect pe : cwp.getPlayer().getActivePotionEffects()) {
                        if(pe.getType().equals(PotionEffectType.BLINDNESS)) {
                            cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getIntOption("blindness-duration") + pe.getDuration(), pe.getAmplifier()), true);
                        } else if (pe.getType().equals(PotionEffectType.POISON)) {
                            cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("poison-duration") + pe.getDuration(), pe.getAmplifier()), true);
                        }
                        return;
                    }

                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, getIntOption("blindness-duration"), 0));
                    cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, getIntOption("poison-duration"), 0));
                }
            }
        }.runTaskLater(dvz, getIntOption("fuse-time"));
        triggerLoc.add(0, 1, 0);
        dvz.getServer().broadcastMessage(Util.formatMsg("&6A bomb has been placed at&8: &c" + triggerLoc.getBlockX() + "&7, &a" + triggerLoc.getBlockY() + "&7, &9" + triggerLoc.getBlockZ()));
        bombs.put(triggerLoc, bt);
    }

    @EventHandler
    public void blockDestroy(BlockBreakEvent event) {
        if(!dvz.getPM().getPlayer(event.getPlayer()).isDwarf()) {
            event.setCancelled(false);
            return;
        }

        Block b;

        if(bombs.containsKey(event.getBlock().getLocation())) {
            b = event.getBlock();
        } else if (bombs.containsKey(event.getBlock().getRelative(0, 1, 0).getLocation())) {
            b = event.getBlock().getRelative(0, 1, 0);
        } else {
            event.setCancelled(true);
            return;
        }

        b.getRelative(0, -1, 0).setType(b.getRelative(1, -1, 0).getType());
        b.setType(Material.AIR);
        b.getRelative(0, -21, 0).setType(Material.STONE);

        bombs.get(event.getBlock().getLocation()).cancel();
        dvz.getServer().broadcastMessage(Util.formatMsg("&6The bomb (&c" + event.getBlock().getX() + "&7, &a" + event.getBlock().getY() + "&7, &9" + event.getBlock().getZ() + "&6) has been destroyed!"));
        bombs.remove(event.getBlock().getLocation());
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
