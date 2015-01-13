package com.clashwars.dvz.abilities.monsters;

import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.player.CWPlayer;
import com.clashwars.dvz.util.DvzItem;
import com.clashwars.dvz.util.Util;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.util.io.file.FilenameException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.HashMap;

public class PotionBomb extends MobAbility {

    HashMap<Location, BukkitTask> bombs = new HashMap<Location, BukkitTask>();

    public PotionBomb() {
        super();
        ability = Ability.POTION_BOMB;
        castItem = new DvzItem(Material.STICK, 1, (short)0, displayName, 3, -1);
    }

    @Override
    public void castAbility(Player player, final Location triggerLoc) {
        final Location bomb = triggerLoc.clone().add(0, 1, 0);
        if (triggerLoc.getBlock().getType() == Material.BEDROCK) {
            player.sendMessage(Util.formatMsg("&cYou can't place that here!"));
            return;
        }

        if(bomb.getWorld().getHighestBlockYAt(bomb) > bomb.getY()) {
            player.sendMessage(Util.formatMsg("&cThere mustn't be a block above the bomb!"));
            return;
        }

        for (int y = 0; y < 20; y++) {
            if (triggerLoc.clone().subtract(0, y, 0).getBlock().getType() == Material.BEDROCK) {
                player.sendMessage(Util.formatMsg("&cYou can't place that here!"));
                return;
            }
        }

        /*if (CWUtil.getNearbyEntities(triggerLoc, getFloatOption("radius"), Arrays.asList(new EntityType[]{EntityType.PLAYER})).size() - 1 == 0) {
            player.sendMessage(Util.formatMsg("&cThere are no players nearby!"));
            return;
        }*/

        try {
            CuboidClipboard cc = CWWorldGuard.pasteSchematic(bomb.getWorld(), CWWorldGuard.getSchematicFile("PotionBomb"), bomb, true, 0, true);
        } catch (CommandException e) {
            e.printStackTrace();
        } catch (FilenameException e) {
            e.printStackTrace();
        } catch (com.sk89q.worldedit.world.DataException e) {
            e.printStackTrace();
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        bomb.getBlock().setType(Material.SKULL);
        bomb.getBlock().setData((byte)1);
        BlockState state  = bomb.getBlock().getState();
        if (state instanceof Skull) {
            ((Skull)state).setSkullType(SkullType.PLAYER);
            ((Skull)state).setOwner("Scott11B");
            ((Skull)state).update();
        }

        BukkitTask bt = new BukkitRunnable() {
            @Override
            public void run() {
                //TODO: Add bomb explosion particles
                bombs.remove(bomb);
                fixGround(bomb.getBlock());

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
        dvz.getServer().broadcastMessage(Util.formatMsg("&6A bomb has been placed at&8: &c" + bomb.getBlockX() + "&7, &a" + bomb.getBlockY() + "&7, &9" + bomb.getBlockZ()));
        bombs.put(bomb, bt);
    }

    @EventHandler
    public void blockDestroy(BlockBreakEvent event) {
        Block bomb;

        if(bombs.containsKey(event.getBlock().getLocation())) {
            bomb = event.getBlock();
        } else if (bombs.containsKey(event.getBlock().getRelative(0, 1, 0).getLocation())) {
            bomb = event.getBlock().getRelative(0, 1, 0);
        } else {
            return;
        }

        event.setCancelled(true);

        if(!dvz.getPM().getPlayer(event.getPlayer()).isDwarf()) {
            return;
        }

        fixGround(bomb);


        bombs.get(bomb.getLocation()).cancel();
        dvz.getServer().broadcastMessage(Util.formatMsg("&6The bomb (&c" + bomb.getX() + "&7, &a" + bomb.getY() + "&7, &9" + bomb.getZ() + "&6) has been destroyed!"));
        bombs.remove(bomb.getLocation());
    }

    public void fixGround(Block bomb) {
        bomb.getRelative(0, -1, 0).setType(bomb.getRelative(1, -1, 0).getType());
        bomb.setType(Material.AIR);
        bomb.getRelative(0, -19, 0).setType(Material.STONE);
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        super.interact(event);
    }

}
