package com.clashwars.dvz.abilities.monsters.witchvillager;

import com.clashwars.dvz.damage.AbilityDmg;
import com.clashwars.cwcore.dependencies.CWWorldGuard;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.clashwars.cwstats.stats.internal.StatType;
import com.clashwars.dvz.abilities.Ability;
import com.clashwars.dvz.abilities.BaseAbility;
import com.clashwars.dvz.abilities.dwarves.bonus.Camouflage;
import com.clashwars.dvz.classes.ClassType;
import com.clashwars.dvz.listeners.custom.GameResetEvent;
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
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionBomb extends BaseAbility {

    HashMap<Location, BukkitTask> bombs = new HashMap<Location, BukkitTask>();

    public PotionBomb() {
        super();
        ability = Ability.POTION_BOMB;
        DvzItem skull = new DvzItem(Material.SKULL_ITEM, 1, (short)0, displayName, -1, 3, false);
        skull.setSkullOwner("Scott11B");
        castItem = skull;
    }

    @Override
    public void onCastItemGiven(Player player) {
        CWPlayer cwp = dvz.getPM().getPlayer(player);
        if (!cwp.getPlayerData().isBombUsed()) {
            player.getInventory().setItem(2, castItem);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent event) {
        if (!isCastItem(event.getItemInHand())) {
            return;
        }

        final Player player = event.getPlayer();
        if (!canCast(player)) {
            return;
        }

        event.setCancelled(true);

        final Location bomb = event.getBlock().getLocation();
        if (event.getBlockAgainst().getType() == Material.BEDROCK) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can't place it on bedrock! &4&l<<"));
            return;
        }

        if (bomb.getWorld().getHighestBlockYAt(bomb) > bomb.getY() + 1) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThere can't be a block above the bomb! &4&l<<"));
            return;
        }

        for (int y = 0; y < 20; y++) {
            if (bomb.clone().subtract(0, y, 0).getBlock().getType() == Material.BEDROCK) {
                CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cYou can't place that here! &8(&7bedrock underneath&8) &4&l<<"));
                return;
            }
        }

        if (bombs.size() > 0) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThere is already an active bomb! &4&l<<"));
            return;
        }

        boolean found = false;
        List<Player> players = CWUtil.getNearbyPlayers(bomb, dvz.getGM().getMonsterPower(20, 30));
        for (Player p : players) {
            if (dvz.getPM().getPlayer(p).isDwarf()) {
                found = true;
            }
        }
        if (!found) {
            CWUtil.sendActionBar(player, CWUtil.integrateColor("&4&l>> &cThere are no dwarves nearby! &4&l<<"));
            return;
        }

        if (onCooldown(player)) {
            return;
        }
        event.setCancelled(false);

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
        bomb.getBlock().setData((byte) 1);
        BlockState state  = bomb.getBlock().getState();
        if (state instanceof Skull) {
            ((Skull)state).setSkullType(SkullType.PLAYER);
            ((Skull)state).setOwner("Scott11B");
            ((Skull)state).update();
        }

        dvz.getSM().changeLocalStatVal(player, StatType.MONSTER_BOMBS_PLACED, 1);

        int fuseTime = 1000 - (int)dvz.getGM().getMonsterPower(800);
        BukkitTask bt = new BukkitRunnable() {
            @Override
            public void run() {
                bombs.remove(bomb);
                fixGround(bomb.getBlock());

                ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 3, bomb);
                bomb.getWorld().playSound(bomb, Sound.EXPLODE, 3, 2);

                dvz.getServer().broadcastMessage(Util.formatMsg("&6The bomb (&c" + bomb.getX() + "&7, &a" + bomb.getY() + "&7, &9" + bomb.getZ() + "&6) has exploded!"));
                Camouflage.removeAllBlocks();

                for(CWPlayer cwp : dvz.getPM().getPlayers(ClassType.DWARF, true, false)) {
                    boolean blindAdded = false;
                    boolean poisonAdded = false;
                    Collection<PotionEffect> activeEffects = cwp.getPlayer().getActivePotionEffects();
                    for (PotionEffect pe : activeEffects) {
                        if(pe.getType().equals(PotionEffectType.BLINDNESS)) {
                            cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int)dvz.getGM().getMonsterPower(100, 180) + pe.getDuration(), 0), true);
                            blindAdded = true;
                        } else if (pe.getType().equals(PotionEffectType.POISON)) {
                            cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int)dvz.getGM().getMonsterPower(120, 240) + pe.getDuration(), 0), true);
                            poisonAdded = true;
                        }
                    }

                    if (!blindAdded) {
                        cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int)dvz.getGM().getMonsterPower(80, 160), 0));
                    }
                    if (!poisonAdded) {
                        cwp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, (int)dvz.getGM().getMonsterPower(100, 200), 0));
                    }

                    new AbilityDmg(cwp.getPlayer(), 0, ability, player);
                    cwp.playSound(cwp.getLocation(), Sound.ZOMBIE_REMEDY, 1, 0);
                }
            }
        }.runTaskLater(dvz, fuseTime);

        Util.broadcast("&6A bomb has been placed at&8: &c" + bomb.getBlockX() + "&7, &a" + bomb.getBlockY() + "&7, &9" + bomb.getBlockZ());
        Util.broadcast("&6If not destroyed, it will explode in &a&l" + fuseTime / 20 + " &6seconds!");
        bombs.put(bomb.getBlock().getLocation(), bt);

        dvz.getPM().getPlayer(player).getPlayerData().setbombUsed(true);

        ParticleEffect.SPELL_WITCH.display(1, 1, 1, 0, 50, bomb.clone().add(0.5f, 0.5f, 0.5f));
        bomb.getWorld().playSound(bomb, Sound.IRONGOLEM_DEATH, 2, 2);
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                if (!bombs.containsKey(bomb.getBlock().getLocation())) {
                    cancel();
                    return;
                }
                i++;
                if (i % 5 == 0) {
                    bomb.getWorld().playSound(bomb, Sound.NOTE_STICKS, 1, 2);
                }
                ParticleEffect.SPELL_WITCH.display(1,1,1,0,1, bomb.clone().add(0.5f, 0.5f, 0.5f));
                ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(104, 184, 0), bomb.clone().add(CWUtil.randomFloat(), CWUtil.randomFloat(), CWUtil.randomFloat()), 500);
                ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(158, 171, 43), bomb.clone().add(CWUtil.randomFloat(), CWUtil.randomFloat(), CWUtil.randomFloat()), 500);
                ParticleEffect.SPELL_MOB.display(new ParticleEffect.OrdinaryColor(94, 68, 11), bomb.clone().add(CWUtil.randomFloat(), CWUtil.randomFloat(), CWUtil.randomFloat()), 500);
            }
        }.runTaskTimer(dvz, 10, 1);
    }

    @EventHandler
    public void blockDestroy(BlockBreakEvent event) {
        Block bomb;

        if(bombs.containsKey(event.getBlock().getLocation())) {
            bomb = event.getBlock();
        } else if (bombs.containsKey(event.getBlock().getRelative(0, 1, 0).getLocation())) {
            bomb = event.getBlock().getRelative(0, 1, 0);
        } else if (bombs.containsKey(event.getBlock().getRelative(0, -1, 0).getLocation())) {
            bomb = event.getBlock().getRelative(0, -1, 0);
        } else {
            return;
        }

        event.setCancelled(true);

        if(!dvz.getPM().getPlayer(event.getPlayer()).isDwarf()) {
            return;
        }

        dvz.getSM().changeLocalStatVal(event.getPlayer(), StatType.DWARF_BOMBS_DESTROYED, 1);

        fixGround(bomb);

        Util.broadcast("&6The bomb (&c" + bomb.getX() + "&7, &a" + bomb.getY() + "&7, &9" + bomb.getZ() + "&6) has been destroyed!");
        bombs.get(bomb.getLocation()).cancel();
        bombs.remove(bomb.getLocation());
    }

    @EventHandler
    private void gameReset(GameResetEvent event) {
        for (Map.Entry<Location, BukkitTask> entry : bombs.entrySet()) {
            fixGround(entry.getKey().getBlock());
            entry.getValue().cancel();
        }
        bombs.clear();
    }

    @EventHandler
    private void pluginUnload(PluginDisableEvent event) {
        for (Map.Entry<Location, BukkitTask> entry : bombs.entrySet()) {
            fixGround(entry.getKey().getBlock());
            entry.getValue().cancel();
        }
        bombs.clear();
    }

    public void fixGround(Block bomb) {
        bomb.getRelative(0, -1, 0).setType(bomb.getRelative(1, -1, 0).getType());
        bomb.setType(Material.AIR);
        bomb.getRelative(0, -19, 0).setType(Material.STONE);
    }

}
